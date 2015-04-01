package com.saikocat.dropwizard.elasticsearch;

import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.Configuration;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.elasticsearch.client.Client;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

public class EsBundleTest {
    static class TestConfig extends Configuration { };

    private Environment environment;
    private EsFactory factory;
    private Bootstrap<?> bootstrap;
    private JerseyEnvironment jerseyEnvironment;
    private LifecycleEnvironment lifecycleEnvironment;
    private HealthCheckRegistry healthChecks;
    private TestConfig config;
    private Client client;

    private EsBundle<TestConfig> bundle;

    @Before
    public void setup() throws java.io.IOException {
        environment = mock(Environment.class);
        factory = mock(EsFactory.class);
        bootstrap = mock(Bootstrap.class);
        jerseyEnvironment = mock(JerseyEnvironment.class);
        lifecycleEnvironment = mock(LifecycleEnvironment.class);
        healthChecks = mock(HealthCheckRegistry.class);
        config = mock(TestConfig.class);
        client  = mock(Client.class);

        bundle = new EsBundle<TestConfig>() {
            @Override
            public EsFactory getEsFactory(TestConfig configuration) {
                return factory;
            }
        };

        when(environment.healthChecks()).thenReturn(healthChecks);
        when(environment.jersey()).thenReturn(jerseyEnvironment);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);

        when(factory.build(environment)).thenReturn(client);
    }

    @Test
    public void bootstrapsNothing() throws Exception {
        bundle.initialize(bootstrap);
        verifyZeroInteractions(bootstrap);
    }

    @Test
    public void createsEsClientFromFactory() throws Exception {
        bundle.run(config, environment);
        verify(factory).build(environment);
        assertThat(bundle.getClient(), is(client));
    }

    @Test
    public void registersHealthCheck() throws Exception {
        bundle.run(config, environment);

        ArgumentCaptor<EsClusterHealthCheck> captor =
            ArgumentCaptor.forClass(EsClusterHealthCheck.class);
        verify(healthChecks).register(eq("elasticsearch"), captor.capture());
    }
}
