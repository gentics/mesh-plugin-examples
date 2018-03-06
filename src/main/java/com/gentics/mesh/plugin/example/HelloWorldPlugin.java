package com.gentics.mesh.plugin.example;

import org.osgi.framework.BundleContext;

import com.gentics.mesh.plugin.rest.AbstractPlugin;

public class HelloWorldPlugin extends AbstractPlugin {

	@Override
	public void start(BundleContext context) {
		// Note that an extension will be deployed multiple times
		// and thus constructed multiple times.
		addExtension(() -> new HelloWorldRestExtension());
	}

	@Override
	public void stop(BundleContext context) {

	}

}
