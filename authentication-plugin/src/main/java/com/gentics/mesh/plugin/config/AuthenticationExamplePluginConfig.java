package com.gentics.mesh.plugin.config;

public class AuthenticationExamplePluginConfig {

	/**
	 * URL to keycloak server
	 */
	private String url;

	/**
	 * Realm to be used
	 */
	private String realmName;

	public String getUrl() {
		return url;
	}

	public AuthenticationExamplePluginConfig setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getRealmName() {
		return realmName;
	}

	public AuthenticationExamplePluginConfig setRealmName(String realmName) {
		this.realmName = realmName;
		return this;
	}
}
