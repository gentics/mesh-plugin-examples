package com.gentics.mesh.plugin;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class HelloWorldPlugin extends AbstractPluginVerticle {

	private static final Logger log = LoggerFactory.getLogger(HelloWorldPlugin.class);

	@Override
	public String getName() {
		return "hello-world";
	}

	@Override
	public void registerEndpoints(Router globalRouter, Router projectRouter) {
		log.info("Registering routes for {" + getName() + "}");

		globalRouter.route("/hello").handler(rc -> {
			rc.response().end("world");
		});

		projectRouter.route("/hello").handler(rc -> {
			rc.response().end("world-project");
		});

	}

}
