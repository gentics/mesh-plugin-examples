# Gentics Mesh Hello World Plugin

This plugin demonstrates the basic concept of a Gentics Mesh plugin.

The plugin implementation within the `HelloWorldPlugin.java` file shows:

How to add global routes to enhance the REST API.

→ `/api/v1/plugins/helloworld/hello` 

How to add project specific routes to the REST API.

→ `/api/v1/:projectName/plugins/helloworld/hello`

How to use the adminClient to access Gentics Mesh data within a plugin.

→ `/api/v1/plugins/helloworld/project` 

How to serve static resources via a static handler route.

→ `/api/v1/plugins/helloworld/static` 


## Gentics Mesh Plugin 101

The project uses [Apache Maven](https://maven.apache.org/) in order to create the plugin jar file which can be deployed.

The `HelloWorldPluginTest.java` test class demonstrates how to test a plugin.


### Format

Every plugin must contain a manifest file. The `mesh-plugin.json` must be placed in the project resources.

```json
{
  "name": "Hello World",
  "apiName": "helloworld",
  "version": "1.0",
  "description": "A very basic hello world plugin which demonstrates the plugin concept",
  "license": "Apache License 2.0",
  "inception": "05-03-2018",
  "author": "Johannes Schüth"
}
```

The `com.gentics.mesh.plugin.plugin-service.json` file must reference the plugin which should be loaded. 

```json
{
  "main": "com.gentics.mesh.plugin.HelloWorldPlugin"
}
```

## Documentation

You can read more about the [plugin system in our documentation](https://getmesh.io/docs/beta/plugin-system.html).