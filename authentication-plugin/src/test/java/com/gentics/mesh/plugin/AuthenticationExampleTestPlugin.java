package com.gentics.mesh.plugin;

import org.pf4j.PluginWrapper;

import com.gentics.mesh.plugin.config.AuthenticationExamplePluginConfig;
import com.gentics.mesh.plugin.env.PluginEnvironment;

/**
 * The test plugin overrides the actual plugin class and overrides the {@link #getConfig()} method so that the config can be injected without the use of the
 * filesystem.
 */
public class AuthenticationExampleTestPlugin extends AuthenticationExamplePlugin {

	public static AuthenticationExamplePluginConfig config;

	public AuthenticationExampleTestPlugin(PluginWrapper wrapper, PluginEnvironment env) {
		super(wrapper, env);
	}

	@Override
	public AuthenticationExamplePluginConfig getConfig() {
		return config;
	}

}
