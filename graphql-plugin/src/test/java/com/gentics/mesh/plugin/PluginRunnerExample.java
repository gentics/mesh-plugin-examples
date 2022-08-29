package com.gentics.mesh.plugin;

import com.gentics.mesh.Mesh;
import com.gentics.mesh.OptionsLoader;
import com.gentics.mesh.core.rest.project.ProjectCreateRequest;
import com.gentics.mesh.etc.config.OrientDBMeshOptions;
import com.gentics.mesh.rest.client.MeshRestClient;

import io.reactivex.Completable;

/**
 * This example shows how an to directly deploy into a Gentics Mesh instance. 
 */
public class PluginRunnerExample {

	private static String PROJECT_NAME = "test";

	public static void main(String[] args) {
		OrientDBMeshOptions options = OptionsLoader.createOrloadOptions(OrientDBMeshOptions.class);
		// Enable in-memory mode
		options.getStorageOptions().setDirectory(null);

		// Disable embedded ES
		options.getSearchOptions().setUrl(null);
		options.getAuthenticationOptions().setKeystorePath("target/keystore_" + System.currentTimeMillis() + ".jceks");

		MeshRestClient client = MeshRestClient.create("localhost", 8080, false);
		Mesh mesh = Mesh.create(options);

		// Deploy the plugin
		Completable deployPlugin = mesh.deployPlugin(GraphQLExamplePlugin.class, "myPlugin");

		// Login and create the needed project
		client.setLogin("admin", "admin");
		Completable clientLogin = client.login().ignoreElement();
		Completable createProject = Completable
			.defer(() -> client.createProject(new ProjectCreateRequest().setName(PROJECT_NAME).setSchemaRef("folder")).toCompletable());

		mesh.rxRun()
			.andThen(clientLogin)
			.andThen(Completable.mergeArray(createProject, deployPlugin)
				.andThen(client.graphqlQuery(PROJECT_NAME, "{ pluginApi { myPlugin { text } } }").toSingle()))
			.subscribe(result -> {
				System.out.println(result.getData().encodePrettily());
				mesh.shutdown();
			}, err -> {
				err.printStackTrace();
			});
	}

}
