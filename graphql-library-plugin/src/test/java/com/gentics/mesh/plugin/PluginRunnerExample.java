package com.gentics.mesh.plugin;

import com.gentics.mesh.Mesh;
import com.gentics.mesh.OptionsLoader;
import com.gentics.mesh.core.rest.project.ProjectCreateRequest;
import com.gentics.mesh.etc.config.MeshOptions;
import com.gentics.mesh.rest.client.MeshRestClient;

import io.reactivex.Completable;

/**
 * This example shows how an to directly deploy into a Gentics Mesh instance. 
 */
public class PluginRunnerExample {

	private static String PROJECT_NAME = "test";

	public static void main(String[] args) {
		MeshOptions options = OptionsLoader.generateDefaultConfig();
		// Enable in-memory mode
		options.getStorageOptions().setDirectory(null);

		// Disable embedded ES
		options.getSearchOptions().setUrl(null).setStartEmbedded(false);
		options.getAuthenticationOptions().setKeystorePath("target/keystore_" + System.currentTimeMillis() + ".jceks");

		MeshRestClient client = MeshRestClient.create("localhost", 8080, false);
		Mesh mesh = Mesh.mesh(options);

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
				.andThen(client.graphqlQuery(PROJECT_NAME, "{ pluginApi { myPlugin { bookById(id: \"book-1\") {id name } } } }").toSingle()))
			.subscribe(result -> {
				System.out.println(result.getData().encodePrettily());
				System.out.println("You can access the GraphiQL browser via this link:\n");
				System.out.println("http://localhost:8080/api/v1/test/graphql/browser/#query=%7B%0A%20%20pluginApi%20%7B%0A%20%20%20%20myPlugin%20%7B%0A%20%20%20%20%20%20bookById(id%3A%20%22book-1%22)%20%7B%0A%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%20%20pageCount%0A%20%20%20%20%20%20%20%20author%20%7B%0A%20%20%20%20%20%20%20%20%20%20id%0A%20%20%20%20%20%20%20%20%20%20firstName%0A%20%20%20%20%20%20%20%20%20%20lastName%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D%0A");
				System.out.println("\nPress any key to shutdown");
				System.in.read();
				mesh.shutdown();
			}, err -> {
				err.printStackTrace();
			});
	}

}
