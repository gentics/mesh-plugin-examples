package com.gentics.mesh.plugin.example;

import com.gentics.mesh.plugin.rest.AbstractPlugin;

public class HelloWorldPlugin extends AbstractPlugin {

	@Override
	public void start() {
		// Note that an extension will be deployed multiple times 
		// and thus constructed multiple times.
		addExtension(() -> new HelloWorldRestExtension());
	}

	@Override
	public void stop() {

	}

}
