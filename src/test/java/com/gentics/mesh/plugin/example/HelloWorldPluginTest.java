package com.gentics.mesh.plugin.example;

import static com.gentics.mesh.test.ClientHelper.call;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;
import org.pf4j.Plugin;

import com.gentics.mesh.Mesh;
import com.gentics.mesh.core.rest.project.ProjectCreateRequest;
import com.gentics.mesh.core.rest.project.ProjectResponse;
import com.gentics.mesh.json.JsonUtil;
import com.gentics.mesh.plugin.HelloWorldPlugin;
import com.gentics.mesh.test.local.MeshLocalServer;

import io.vertx.core.json.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

public class HelloWorldPluginTest {

	private Plugin plugin = new HelloWorldPlugin(null);

	@ClassRule
	public static final MeshLocalServer server = new MeshLocalServer()
		.withInMemoryMode()
		.waitForStartup();

	@Test
	public void testPlugin() throws IOException {
		Mesh mesh = server.getMesh();
		mesh.getVertx().deployVerticle(plugin);

		for (int i = 0; i < 2; i++) {
			ProjectCreateRequest request = new ProjectCreateRequest();
			request.setName("test" + i);
			request.setSchemaRef("folder");
			call(() -> server.client().createProject(request));
		}

		assertEquals("world", get("/api/v1/plugins/" + plugin.getAPIName() + "/hello"));
		assertEquals("world-project-test0", get("/api/v1/test0/plugins/" + plugin.getAPIName() + "/hello"));
		assertEquals("world-project-test1", get("/api/v1/test1/plugins/" + plugin.getAPIName() + "/hello"));

		JsonObject meResponse = new JsonObject(get("/api/v1/plugins/" + plugin.getAPIName() + "/me"));
		assertEquals("anonymous", meResponse.getString("username"));

		ProjectResponse project = JsonUtil.readValue(get("/api/v1/plugins/" + plugin.getAPIName() + "/project"), ProjectResponse.class);
		assertEquals(HelloWorldPlugin.PROJECT_NAME, project.getName());

	}

	private OkHttpClient client() {
		Builder builder = new OkHttpClient.Builder();
		return builder.build();
	}

	private String get(String path) throws IOException {
		Request request = new Request.Builder()
			.header("Accept", "application/json")
			.url("http://" + server.getHostname() + ":" + server.getPort() + path)
			.build();

		Response response = client().newCall(request).execute();
		return response.body().string();
	}
}
