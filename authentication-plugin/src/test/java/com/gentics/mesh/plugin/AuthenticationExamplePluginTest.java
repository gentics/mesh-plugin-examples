package com.gentics.mesh.plugin;

import static com.gentics.mesh.test.ClientHelper.call;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.gentics.mesh.MeshVersion;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.wait.strategy.Wait;

import com.gentics.mesh.core.rest.user.UserAPITokenResponse;
import com.gentics.mesh.core.rest.user.UserResponse;
import com.gentics.mesh.plugin.config.AuthenticationExamplePluginConfig;
import com.gentics.mesh.rest.client.MeshRestClient;
import com.gentics.mesh.test.docker.KeycloakContainer;
import com.gentics.mesh.test.local.MeshLocalServer;

import io.vertx.core.json.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthenticationExamplePluginTest {

	@ClassRule
	public static final KeycloakContainer keycloak = new KeycloakContainer("/keycloak/realm.json")
		.waitingFor(Wait.forHttp("/auth/realms/master-test"));

	@BeforeClass
	public static void configurePlugin() {
		AuthenticationExamplePluginConfig config = new AuthenticationExamplePluginConfig();
		config.setRealmName("master-test");
		config.setUrl("http://" + keycloak.getContainerIpAddress() + ":" + keycloak.getFirstMappedPort());
		AuthenticationExampleTestPlugin.config = config;
	}

	@Rule
	public final MeshLocalServer server = new OrientDBMeshLocalServer()
		.withInMemoryMode()
		.withPlugin(AuthenticationExampleTestPlugin.class, "authPlugin")
		.waitForStartup();

	private static OkHttpClient httpClient = new OkHttpClient.Builder().build();

	@Test
	public void testPlugin() throws IOException {

		// 1. Login the user
		JsonObject authInfo = loginKeycloak();
		String token = authInfo.getString("access_token");
		restClient().setAPIKey(token);
		System.out.println("Login Token:\n" + authInfo.encodePrettily());

		// 2. Invoke authenticated request
		UserResponse me = call(() -> restClient().me());
		assertEquals("mapped@email.tld", me.getEmailAddress());
		assertEquals("mapepdFirstname", me.getFirstname());
		assertEquals("mapepdLastname", me.getLastname());
		assertEquals("dummyuser", me.getUsername());
		String uuid = me.getUuid();

		// 3. Invoke request again to ensure that the previously created user gets returned
		call(() -> restClient().me());

		UserResponse me2 = call(() -> restClient().me());
		System.out.println(me2.toJson());

		assertEquals("The uuid should not change. The previously created user should be returned.", uuid, me2.getUuid());
		assertEquals("group1", me2.getGroups().get(0).getName());
		assertEquals("group2", me2.getGroups().get(1).getName());

		// Invoke request without token
		JsonObject meJson = new JsonObject(get(MeshVersion.CURRENT_API_BASE_PATH + "/auth/me"));
		assertEquals("anonymous", meJson.getString("username"));

		restClient().setAPIKey(null);
		restClient().setLogin("admin", "admin");
		restClient().login().blockingGet();

		// Now invoke request with regular Mesh API token.
		UserAPITokenResponse meshApiToken = call(() -> restClient().issueAPIToken(me2.getUuid()));
		restClient().logout().blockingGet();
		restClient().setAPIKey(meshApiToken.getToken());
		me = call(() -> restClient().me());
		assertEquals("dummyuser", me.getUsername());

		// Test broken token
		restClient().setAPIKey("borked");
		call(() -> restClient().me(), UNAUTHORIZED, "error_not_authorized");

		restClient().setAPIKey(null);
		UserResponse anonymous = call(() -> restClient().me());
		assertEquals("anonymous", anonymous.getUsername());
	}

	protected JsonObject get(String path, String token) throws IOException {
		Request request = new Request.Builder()
			.header("Accept", "application/json")
			.header("Authorization", "Bearer " + token)
			.url("http://localhost:" + server.getPort() + path)
			.build();

		Response response = httpClient().newCall(request).execute();
		return new JsonObject(response.body().string());
	}

	protected String get(String path) throws IOException {
		Request request = new Request.Builder()
			.header("Accept", "application/json")
			.url("http://localhost:" + server.getPort() + path)
			.build();

		Response response = httpClient().newCall(request).execute();
		System.out.println("Response: " + response.code());
		return response.body().string();
	}

	protected JsonObject loadJson(String path) throws IOException {
		return new JsonObject(IOUtils.toString(getClass().getResource(path), StandardCharsets.UTF_8));
	}

	private JsonObject loginKeycloak() throws IOException {
		String secret = "9b65c378-5b4c-4e25-b5a1-a53a381b5fb4";

		int port = keycloak.getFirstMappedPort();

		StringBuilder content = new StringBuilder();
		content.append("client_id=mesh&");
		content.append("username=dummyuser&");
		content.append("password=finger&");
		content.append("grant_type=password&");
		content.append("client_secret=" + secret);
		RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), content.toString());
		Request request = new Request.Builder()
			.post(body)
			.url("http://localhost:" + port + "/auth/realms/master-test/protocol/openid-connect/token")
			.build();

		Response response = httpClient().newCall(request).execute();
		return new JsonObject(response.body().string());
	}

	private OkHttpClient httpClient() {
		return httpClient;
	}

	private MeshRestClient restClient() {
		return server.client();
	}
}
