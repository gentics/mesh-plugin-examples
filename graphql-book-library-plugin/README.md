# GraphQL Book Library Plugin

This plugin is inspired by the link:https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/[Getting Started GraphQL Java Tutorial].

This example shows how to setup a GraphQL plugin which uses a static schema definition.

```gql
{
  pluginApi {
    myPlugin {
      bookById(id: "book-1") {
        name
        pageCount
        author {
          id
          firstName
          lastName
        }
      }
    }
  }
}
```

## How to run

The example can be run via the `GraphQlExamplePluginTest` or `PluginRunnerExample` classes. The latter makes it also possible to access the GraphiQL for the deployed plugin.

## Plugin

A `graphql` plugin must implement the `GraphQLPlugin` interface. This will require the `createRootSchema()` method.

The [GraphQL-Java](https://github.com/graphql-java/graphql-java) library will be used to create the schema and data resolvers for the plugin API.

