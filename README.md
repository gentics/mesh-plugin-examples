This project contains a collection of example Gentics Mesh plugins.

# Hello World

This plugin demonstrates how to extend the REST API.

# GraphQL

Alternatively a plugin may also extend the GraphQL API.

This plugins shows how to add a field to the pluginApi so that you access it via the plugin name (`graphql`).

```
pluginApi {
    graphql {
        text
    }
}
```

# Extensions

Plugins may also interact with each other. In this example two plugins (extension-consumer, extension-provider) use extension points to share custom logic.

The extension-provider defines the extension point and the extension consumer will utilize this point.

This mechanism is useful if you want to extend or alter the logic of a plugin using other provider plugins.



