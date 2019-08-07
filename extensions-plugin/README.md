# Extensions

Plugins may also interact with each other. In this example two plugins (`extension-consumer`, `extension-provider`) use extension points to share custom logic.

The extension-provider defines the extension point and the extension consumer will utilize this point.

This mechanism is useful if you want to extend or alter the logic of a plugin using other provider plugins.

## extension-api

This module contains the commmonly used API which defines the `ExtensionPoint` which is refernced by the `extension-consumer` and implemented (and provided) by the `extension-provider`.

```java
public interface DummyExtensionPoint extends ExtensionPoint {

	String name();

}
```

## extension-provider

The provider implements the `DummyExtensionPoint`.  

```java
@Extension
public class DummyExtension implements DummyExtensionPoint {

	@Override
	public String name() {
		return "My dummy extension";
	}

}
```

The provider plugin does not need the `plugin-extension-api` dependency. The classes of this module will be loaded from the dependent plugin (`extension-consumer`). The dependency must thus be set to scope `provided`.

```xml
<dependency>
    <groupId>com.gentics.mesh.plugin</groupId>
    <artifactId>plugin-extension-api</artifactId>
    <version>${project.version}</version>
    <scope>provided</scope>
</dependency>
```

Instead it is important to set the `Plugin-Dependencies` manifest entry to include the dependent plugin (`extension-consumer`).

## extension-consumer

The consumer plugin accesses the provided extensions via the plugin manager.

```java
public class ExtensionConsumerPlugin extends AbstractPlugin implements RestPlugin {

	private static final Logger log = LoggerFactory.getLogger(ExtensionConsumerPlugin.class);

	public ExtensionConsumerPlugin(PluginWrapper wrapper, PluginEnvironment env) {
		super(wrapper, env);
	}

	@Override
	public Router createGlobalRouter() {
		log.info("Registering routes for {" + name() + "}");
		Router router = Router(vertx());
		router.route("/extensions").handler(rc -> {
			StringBuilder builder = new StringBuilder();
			getWrapper().getPluginManager().getExtensions(DummyExtensionPoint.class).stream().map(e -> e.name()).forEach(name -> {
				builder.append(name);
			});
			rc.response().end(builder.toString());
		});
		return router;
	}

}
```

The consumer plugin will provide the `DummyExtension` classes to other plugins. Thus the dependency must be included and *not* to provided:

```xml
<dependency>
    <groupId>com.gentics.mesh.plugin</groupId>
    <artifactId>plugin-extension-api</artifactId>
    <version>${project.version}</version>
</dependency>
```


