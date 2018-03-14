package com.gentics.mesh.plugin;

import org.osgi.framework.BundleContext;

import com.gentics.mesh.plugin.rest.AbstractPlugin;

public class HelloWorldPlugin extends AbstractPlugin {

	@Override
	public String getName() {
		return "hello world plugin";
	}

	@Override
	public void start(BundleContext context) {
		System.out.println("Starting " + getName());
		// Note that an extension will be deployed multiple times
		// and thus constructed multiple times.
		addExtension(() -> new HelloWorldRestExtension());

		System.out.println("Add service on {" + getName() + "}");
		context.registerService(Plugin.class, this, null);

	}

	@Override
	public void stop(BundleContext context) {
		System.out.println("Stoping " + getName());
	}

}
