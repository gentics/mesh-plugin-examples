package com.gentics.mesh.plugin;

import static com.gentics.mesh.test.ClientHelper.call;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.wait.strategy.Wait;

import com.gentics.mesh.OptionsLoader;
import com.gentics.mesh.core.rest.user.UserAPITokenResponse;
import com.gentics.mesh.core.rest.user.UserResponse;
import com.gentics.mesh.etc.config.MeshOptions;
import com.gentics.mesh.etc.config.OAuth2Options;
import com.gentics.mesh.etc.config.OAuth2ServerConfig;
import com.gentics.mesh.handler.VersionHandler;
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

	@Rule
	public final MeshLocalServer server = new MeshLocalServer()
		.withInMemoryMode()
		.withPlugin(AuthenticationExamplePlugin.class, "authPlugin")
		.withOptions(meshOptions())
		.waitForStartup();

	private static OkHttpClient httpClient = new OkHttpClient.Builder()
		.writeTimeout(15, TimeUnit.SECONDS)
		.readTimeout(15, TimeUnit.SECONDS)
		.connectTimeout(15, TimeUnit.SECONDS)
		.build();

	private static MeshOptions meshOptions() {
		MeshOptions options = OptionsLoader.generateDefaultConfig();
		OAuth2Options oauth2Options = options.getAuthenticationOptions().getOauth2();
		oauth2Options.setEnabled(true);

		OAuth2ServerConfig realmConfig = new OAuth2ServerConfig();
		realmConfig.setAuthServerUrl("http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080) + "/auth");
		realmConfig.setRealm("master-test");
		realmConfig.setSslRequired("external");
		realmConfig.setResource("mesh");
		realmConfig.setConfidentialPort(0);
		realmConfig.addCredential("secret", "9b65c378-5b4c-4e25-b5a1-a53a381b5fb4");

		oauth2Options.setConfig(realmConfig);
		return options;
	}

	@Test
	public void testPlugin() throws IOException {

		// 1. Login the user
		JsonObject authInfo = loginKeycloak();
		String token = authInfo.getString("access_token");
		client().setAPIKey(token);
		System.out.println("Login Token:\n" + authInfo.encodePrettily());

		// 2. Invoke authenticated request
		UserResponse me = call(() -> client().me());
		assertEquals("mapped@email.tld", me.getEmailAddress());
		assertEquals("mapepdFirstname", me.getFirstname());
		assertEquals("mapepdLastname", me.getLastname());
		assertEquals("dummyuser", me.getUsername());
		String uuid = me.getUuid();

		// 3. Invoke request again to ensure that the previously created user gets returned
		call(() -> client().me());

		UserResponse me2 = call(() -> client().me());
		System.out.println(me2.toJson());

		assertEquals("The uuid should not change. The previously created user should be returned.", uuid, me2.getUuid());
		assertEquals("group1", me2.getGroups().get(0).getName());
		assertEquals("group2", me2.getGroups().get(1).getName());

		// Invoke request without token
		JsonObject meJson = new JsonObject(get(VersionHandler.CURRENT_API_BASE_PATH + "/auth/me"));
		assertEquals("anonymous", meJson.getString("username"));

		client().setAPIKey(null);
		client().setLogin("admin", "admin");
		client().login().blockingGet();

		// Now invoke request with regular Mesh API token.
		UserAPITokenResponse meshApiToken = call(() -> client().issueAPIToken(me2.getUuid()));
		client().logout().blockingGet();
		client().setAPIKey(meshApiToken.getToken());
		me = call(() -> client().me());
		assertEquals("dummyuser", me.getUsername());

		// Test broken token
		client().setAPIKey("borked");
		call(() -> client().me(), UNAUTHORIZED, "error_not_authorized");

		client().setAPIKey(null);
		UserResponse anonymous = call(() -> client().me());
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

	private MeshRestClient client() {
		return server.client();
	}
}
