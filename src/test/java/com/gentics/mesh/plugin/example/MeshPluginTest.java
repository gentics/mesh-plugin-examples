package com.gentics.mesh.plugin.example;

import static com.gentics.mesh.test.ClientHelper.call;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;

import com.gentics.mesh.Mesh;
import com.gentics.mesh.core.rest.project.ProjectCreateRequest;
import com.gentics.mesh.plugin.HelloWorldPlugin;
import com.gentics.mesh.plugin.Plugin;
import com.gentics.mesh.test.local.MeshLocalServer;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

public class MeshPluginTest {

	private Plugin plugin = new HelloWorldPlugin();

	@ClassRule
	public static final MeshLocalServer server = new MeshLocalServer()
		.withInMemoryMode()
		.waitForStartup();

	private OkHttpClient client() {
		Builder builder = new OkHttpClient.Builder();
		return builder.build();
	}

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

		assertEquals("world", get("/api/v1/plugins/" + plugin.getName() + "/hello"));
		assertEquals("world-project", get("/api/v1/test0/plugins/" + plugin.getName() + "/hello"));
		assertEquals("world-project", get("/api/v1/test1/plugins/" + plugin.getName() + "/hello"));

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
