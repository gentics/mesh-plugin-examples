package com.gentics.mesh.plugin;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;

import com.gentics.mesh.Mesh;
import com.gentics.mesh.core.rest.graphql.GraphQLResponse;
import com.gentics.mesh.core.rest.project.ProjectCreateRequest;
import com.gentics.mesh.rest.client.MeshRestClient;
import com.gentics.mesh.test.local.MeshLocalServer;

import io.reactivex.Completable;
import io.reactivex.Single;

public class GraphQlExamplePluginTest {

	private static String PROJECT_NAME = "test";

	@ClassRule
	public static final MeshLocalServer server = new MeshLocalServer()
		.withInMemoryMode()
		.waitForStartup();

	@Test
	public void testPlugin() throws IOException {
		Mesh mesh = server.getMesh();
		MeshRestClient client = server.client();

		// Create the project
		Completable createProject = client.createProject(new ProjectCreateRequest().setName(PROJECT_NAME).setSchemaRef("folder")).toCompletable();

		// Deploy the plugin
		Completable deployPlugin = mesh.deployPlugin(GraphQLExamplePlugin.class, "myPlugin");

		// Setup the instance
		Completable setup = Completable.mergeArray(createProject, deployPlugin);

		// Run query on the plugin
		Single<GraphQLResponse> queryOnPlugin = client.graphqlQuery(PROJECT_NAME, "{ pluginApi { myPlugin { text } } }").toSingle();

		// Run the operations
		GraphQLResponse result = setup.andThen(queryOnPlugin).blockingGet();

		// Print the GraphQL API result
		System.out.println(result.getData().encodePrettily());
	}
}
