# Authentication Plugin

This plugin demonstrates how to use the authentication service plugin API.

## Documentation

More information on the API and the functions of authentication plugins can be found in our [authentication service plugin API documentation](https://getmesh.io/docs/plugin-types/auth-service-plugin/).

## Building

```bash
mvn clean package
```

## Docker

```bash
docker run --rm \
    -v $PWD/target/mesh-authentication-example-plugin-0.0.1-SNAPSHOT.jar:/plugins/graphql.jar \
    -p 8080:8080 \
    gentics/mesh:0.39.2
```