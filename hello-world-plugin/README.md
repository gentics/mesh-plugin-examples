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

Every plugin must contain a manifest. When using Maven the manifest can be generated via the `maven-shade-plugin`.

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.2.1</version>
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>shade</goal>
      </goals>
      <configuration>
        <transformers>
          <transformer
            implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
            <manifestEntries>
              <Plugin-Id>${plugin.id}</Plugin-Id>
              <Plugin-Version>${plugin.version}</Plugin-Version>
              <Plugin-Author>${plugin.author}</Plugin-Author>
              <Plugin-Class>${plugin.class}</Plugin-Class>
              <Plugin-Description>${plugin.description}</Plugin-Description>
              <Plugin-License>${plugin.license}</Plugin-License>
              <Plugin-Inception>${plugin.inception}</Plugin-Inception>
            </manifestEntries>
          </transformer>
        </transformers>
      </configuration>
    </execution>
  </executions>
</plugin>
```

## Documentation

You can read more about the [plugin system in our documentation](https://getmesh.io/docs/plugin-system).