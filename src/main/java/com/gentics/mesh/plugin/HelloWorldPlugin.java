package com.gentics.mesh.plugin;


import com.gentics.mesh.plugin.rest.AbstractPlugin;

import io.vertx.core.Future;

public class HelloWorldPlugin extends AbstractPlugin {

	@Override
	public String getName() {
		return "hello world plugin";
	}

	
	@Override
	public void start(Future<Void> future) {
		System.out.println("Starting " + getName());
		
		System.out.println("Test");
		
		// Note that an extension will be deployed multiple times
		// and thus constructed multiple times.
		addExtension(() -> new HelloWorldRestExtension());
	}

	@Override
	public void stop(Future<Void> future) {
		System.out.println("Stopping " + getName());
	}

}
