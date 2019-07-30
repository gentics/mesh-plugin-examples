package com.gentics.mesh.plugin;

import org.junit.ClassRule;
import org.junit.Test;

import com.gentics.mesh.Mesh;
import com.gentics.mesh.core.rest.project.ProjectCreateRequest;
import com.gentics.mesh.rest.client.MeshRestClient;
import com.gentics.mesh.test.local.MeshLocalServer;

import io.reactivex.Completable;

public class GraphQlExamplePluginTest {

	private static String PROJECT_NAME = "test";

	@ClassRule
	public static final MeshLocalServer server = new MeshLocalServer()
		.withInMemoryMode()
		.waitForStartup();

	@Test
	public void testPlugin() {
		Mesh mesh = server.getMesh();
		MeshRestClient client = server.client();

		// Create the project
		Completable createProject = client.createProject(new ProjectCreateRequest().setName(PROJECT_NAME)).toCompletable();
		// Deploy the plugin
		Completable deployPlugin = mesh.deployPlugin(GraphQLExamplePlugin.class, "basic");

		Completable.mergeArray(createProject, deployPlugin)
			.andThen(client.graphqlQuery(PROJECT_NAME, "{ pluginApi { graphql { text } } }").toSingle()).subscribe(result -> {
				System.out.println(result.getData().encodePrettily());
			}, err -> {
				err.printStackTrace();
			});
	}
}
