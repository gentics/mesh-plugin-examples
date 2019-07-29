# GraphQL Plugin

Alternatively a plugin may also extend the GraphQL API.

This plugins shows how to add a field to the pluginApi so that you access it via the plugin name (`graphql`).

```gql
pluginApi {
    graphql {
        text
    }
}
```

## Plugin

A `graphql` plugin must implement the `GraphQLPlugin` interface. This will require the `createType()` method.

The [GraphQL-Java](https://github.com/graphql-java/graphql-java) library will be used to create the schema and data resolvers for the plugin API.


```java
public class GraphQLExamplePlugin extends AbstractPlugin implements GraphQLPlugin {

	private GraphQLObjectType type;

	public GraphQLExamplePlugin(PluginWrapper wrapper, 
                                PluginEnvironment env) {
		super(wrapper, env);
	}

	@Override
	public Completable initialize() {
		type = newObject()
			.name("PluginDataType")
			.description("Dummy GraphQL Test")
			.field(newFieldDefinition().name("text")
				.type(GraphQLString)
				.description("Say hello to the world of plugins")
				.dataFetcher(env -> {
					return "hello-world";
				}))
			.build();
		type = prefixType(type);
		return Completable.complete();
	}

	@Override
	public GraphQLObjectType createType() {
		return type;
	}

}
```