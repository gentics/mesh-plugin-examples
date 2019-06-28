package com.gentics.mesh.plugin;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.io.File;

import org.pf4j.Extension;
import org.pf4j.PluginWrapper;

import com.gentics.mesh.core.rest.project.ProjectCreateRequest;
import com.gentics.mesh.plugin.ext.AbstractRestExtension;
import com.gentics.mesh.rest.client.MeshRestClient;

import io.reactivex.Completable;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.reactivex.core.buffer.Buffer;

public class HelloWorldPlugin extends AbstractPlugin {

	private static final Logger log = LoggerFactory.getLogger(HelloWorldPlugin.class);

	public static final String PROJECT_NAME = "HelloWorld";

	public HelloWorldPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public Completable initialize() {
		// The initialize method can be used to setup initial data which is needed by the plugin.
		// You can use the admin client to setup initial data or access the filesystem to read/write data.
		String path = new File(getStorageDir(), "dummyFile.txt").getAbsolutePath();
		return getRxVertx().fileSystem()
			.rxWriteFile(path, Buffer.buffer("test"))
			.andThen(createProject());
	}

	/**
	 * Utilize the admin client and create a project.
	 * 
	 * @return
	 */
	private Completable createProject() {
		ProjectCreateRequest request = new ProjectCreateRequest();
		request.setName(PROJECT_NAME);
		request.setSchemaRef("folder");
		MeshRestClient client = adminClient();
		return client.createProject(request).toCompletable();
	}

	@Extension
	public static class HelloRestEndpointExtension extends AbstractRestExtension {

		public StaticHandler staticHandler = StaticHandler.create("webroot", getClass().getClassLoader());

		@Override
		public void registerEndpoints(Router globalRouter, Router projectRouter) {
			log.info("Registering routes for {" + getName() + "}");

			// Route which demonstrates that the API can be directly extended
			// Path: /api/v1/plugins/helloworld/hello
			globalRouter.route("/hello").handler(rc -> {
				rc.response().end("world");
			});

			// Route which demonstrates that plugins can also have project specific routes.
			// Path: /api/v1/:projectName/plugins/helloworld/hello
			// It is possible to access the project information via the context project() method.
			projectRouter.route("/hello").handler(rc -> {
				PluginContext context = wrap(rc);
				rc.response().end("world-project-" + context.project().getString("name"));
			});

			// Route which will use the admin client to load the previously created project and return it.
			// Path: /api/v1/plugins/helloworld/project
			globalRouter.route("/project").handler(rc -> {
				PluginContext context = wrap(rc);
				adminClient().findProjectByName(PROJECT_NAME).toSingle().subscribe(project -> {
					context.send(project, OK);
				}, rc::fail);
			});

			// Route to serve static contents from the webroot resources folder of the plugin.
			// Path: /api/v1/plugins/helloworld/static
			globalRouter.route("/static/*").handler(staticHandler);

			// Route which will return the user information
			// Path: /api/v1/plugins/helloworld/me
			globalRouter.route("/me").handler(rc -> {
				PluginContext context = wrap(rc);
				context.client().me().toSingle().subscribe(me -> {
					rc.response().putHeader("content-type", "application/json");
					rc.response().end(me.toJson());
				}, rc::fail);
			});

		}

	}

}
