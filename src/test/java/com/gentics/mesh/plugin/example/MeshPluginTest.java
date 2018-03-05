package com.gentics.mesh.plugin.example;

import static com.gentics.mesh.test.ClientHelper.call;

import org.junit.ClassRule;
import org.junit.Test;

import com.gentics.mesh.core.rest.user.UserResponse;
import com.gentics.mesh.test.docker.MeshDockerServer;

import io.vertx.core.Vertx;

public class MeshPluginTest {

	public static final Vertx vertx = Vertx.vertx();

	@ClassRule
	public static final MeshDockerServer mesh = new MeshDockerServer("gentics/mesh:0.17.0", vertx)
		.waitForStartup();

	@Test
	public void testPlugin() {
		UserResponse user = call(() -> mesh.client().me());
		System.out.println(user.getUsername());
	}
}
