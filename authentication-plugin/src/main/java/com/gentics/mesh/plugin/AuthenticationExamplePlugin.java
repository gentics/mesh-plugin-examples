package com.gentics.mesh.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pf4j.PluginWrapper;

import com.gentics.mesh.core.rest.group.GroupResponse;
import com.gentics.mesh.core.rest.role.RoleReference;
import com.gentics.mesh.core.rest.role.RoleResponse;
import com.gentics.mesh.core.rest.user.UserUpdateRequest;
import com.gentics.mesh.plugin.auth.AuthServicePlugin;
import com.gentics.mesh.plugin.auth.MappingResult;
import com.gentics.mesh.plugin.env.PluginEnvironment;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class AuthenticationExamplePlugin extends AbstractPlugin implements AuthServicePlugin {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationExamplePlugin.class);

	public AuthenticationExamplePlugin(PluginWrapper wrapper, PluginEnvironment env) {
		super(wrapper, env);
	}

	@Override
	public MappingResult mapToken(RoutingContext rc, JsonObject token) {
		log.info("Mapping groups in plugin");
		MappingResult result = new MappingResult();
		List<GroupResponse> groupList = new ArrayList<>();
		groupList.add(new GroupResponse().setName("group1"));
		groupList.add(new GroupResponse()
			.setName("group2")
			.setRoles(Arrays.asList(new RoleReference().setName("role1"))));
		groupList.add(new GroupResponse()
			.setName("group3")
			.setRoles(Arrays.asList(new RoleReference().setName("role1"), new RoleReference().setName("role2"))));
		result.setGroups(groupList);

		log.info("Mapping role in plugin");
		List<RoleResponse> roleList = new ArrayList<>();
		roleList.add(new RoleResponse().setName("role1"));
		roleList.add(new RoleResponse().setName("role2"));
		result.setRoles(roleList);

		log.info("Mapping user in plugin");
		printToken(token);
		String username = token.getString("preferred_username");
		UserUpdateRequest user = new UserUpdateRequest();
		user.setUsername(username);
		user.setEmailAddress("mapped@email.tld");
		user.setFirstname("mapepdFirstname");
		user.setLastname("mapepdLastname");
		result.setUser(user);
		return result;
	}

	@Override
	public boolean acceptToken(HttpServerRequest httpServerRequest, JsonObject token) {
		log.info("Checking token. Accepting..");
		printToken(token);
		return true;
	}

	@Override
	public boolean removeRoleFromGroup(String roleName, String groupName, JsonObject token) {
		log.info("Handling removal of role {" + roleName + "} from {" + groupName + "}");
		return false;
	}

	@Override
	public boolean removeUserFromGroup(String groupName, JsonObject token) {
		log.info("Handling removel of user from group {" + groupName + "}");
		return false;
	}

	private void printToken(JsonObject token) {
		String username = token.getString("preferred_username");
		System.out.println("Token for {" + username + "}");
		System.out.println(token.encodePrettily());
	}
}
