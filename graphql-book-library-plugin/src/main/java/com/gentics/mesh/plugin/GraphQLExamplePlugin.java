package com.gentics.mesh.plugin;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import java.net.URL;

import org.pf4j.PluginWrapper;

import com.gentics.mesh.plugin.env.PluginEnvironment;
import com.gentics.mesh.plugin.graphql.GraphQLPlugin;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.reactivex.Completable;

public class GraphQLExamplePlugin extends AbstractPlugin implements GraphQLPlugin {

	private GraphQLSchema schema;

	private GraphQLDataFetchers fetcher = new GraphQLDataFetchers();

	public GraphQLExamplePlugin(PluginWrapper wrapper, PluginEnvironment env) {
		super(wrapper, env);
	}

	@Override
	public Completable initialize() {
		return Completable.fromAction(() -> {
			URL url = Resources.getResource("schema.graphqls");
			String sdl = Resources.toString(url, Charsets.UTF_8);
			schema = buildSchema(sdl);
		});
	}

	private GraphQLSchema buildSchema(String sdl) {
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
		RuntimeWiring runtimeWiring = buildWiring();
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
	}

	private RuntimeWiring buildWiring() {
		return RuntimeWiring.newRuntimeWiring()
			.type(newTypeWiring("Query")
				.dataFetcher("bookById", fetcher.getBookByIdDataFetcher()))
			.type(newTypeWiring("Book")
				.dataFetcher("author", fetcher.getAuthorDataFetcher()))
			.build();
	}

	@Override
	public GraphQLSchema createRootSchema() {
		return schema;
	}

}
