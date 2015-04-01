package com.saikocat.dropwizard.elasticsearch;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.elasticsearch.client.Client;

public abstract class EsBundle<T extends Configuration>
        implements ConfiguredBundle<T>, EsConfiguration<T> {

    private Client client;

    public Client getClient() {
        return client;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        client = getEsFactory(configuration).build(environment);

        environment.healthChecks().register("elasticsearch", new EsClusterHealthCheck(client));
    }
}
