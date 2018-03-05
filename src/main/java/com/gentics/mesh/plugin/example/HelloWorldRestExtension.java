package com.gentics.mesh.plugin.example;

import com.gentics.mesh.plugin.rest.AbstractRestExtension;
import com.gentics.mesh.plugin.rest.RestExtensionScope;

public class HelloWorldRestExtension extends AbstractRestExtension {

	public HelloWorldRestExtension() {
		// The plugin should be accessible via /api/v1/plugins/hello-world
		super(RestExtensionScope.GLOBAL, "hello-world");
	}

	@Override
	public void start() {
		router().route("/hello").handler(rc -> {
			rc.response().end("world");
		});
	}

}
