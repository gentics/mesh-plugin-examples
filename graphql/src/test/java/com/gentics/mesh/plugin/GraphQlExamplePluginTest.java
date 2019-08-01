package com.gentics.mesh.plugin;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;

import com.gentics.mesh.core.rest.graphql.GraphQLResponse;
import com.gentics.mesh.core.rest.project.ProjectCreateRequest;
import com.gentics.mesh.rest.client.MeshRestClient;
import com.gentics.mesh.test.local.MeshLocalServer;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * This example test demonstrates how to use the {@link MeshLocalServer} to deploy a plugin.
 */
public class GraphQlExamplePluginTest {

	private static String PROJECT = "test";

	@ClassRule
	public static final MeshLocalServer server = new MeshLocalServer()
		.withInMemoryMode()
		.withPlugin(GraphQLExamplePlugin.class, "myPlugin")
		.waitForStartup();

	@Test
	public void testPlugin() throws IOException {
		MeshRestClient client = server.client();

		// Create the project
		Completable createProject = client.createProject(
			new ProjectCreateRequest()
				.setName(PROJECT)
				.setSchemaRef("folder"))
			.toCompletable();

		// Run query on the plugin
		String query = "{ pluginApi { myPlugin { text } } }";
		Single<GraphQLResponse> rxQuery = client.graphqlQuery(PROJECT, query)
			.toSingle();

		// Run the operations
		GraphQLResponse result = createProject.andThen(rxQuery).blockingGet();

		// Print the GraphQL API result
		System.out.println(result.getData().encodePrettily());
	}
}
