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

A `graphql` plugin must implement the `GraphQLPlugin` interface. This will require the `createRootSchema()` method.

The [GraphQL-Java](https://github.com/graphql-java/graphql-java) library will be used to create the schema and data resolvers for the plugin API.

