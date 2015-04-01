package com.saikocat.dropwizard.elasticsearch;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class ConfigurationFactoryTest {

    @Mock
    private Environment environment;
    @Mock
    private LifecycleEnvironment lifecycleEnvironment;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ConfigurationFactory<EsFactory> configFactory =
            new ConfigurationFactory<>(EsFactory.class, validator, Jackson.newObjectMapper(), "dw");

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
    }

    @Test
    public void defaultConfigShouldBeValid() throws IOException, ConfigurationException {
        configFactory.build();
    }

    @Test(expected = ConfigurationException.class)
    public void eitherNodeClientOrServerListMustBeSet()
            throws IOException, ConfigurationException, URISyntaxException {
        URL configFileUrl = this.getClass().getResource("/elasticsearch/invalid.yml");
        File configFile = new File(configFileUrl.toURI());
        configFactory.build(configFile);
    }

    @Test
    public void shouldBuildEsFactoryClass()
            throws URISyntaxException, IOException, ConfigurationException {
        URL configFileUrl = this.getClass().getResource("/elasticsearch/node_client.yml");
        File configFile = new File(configFileUrl.toURI());
        EsFactory factory = configFactory.build(configFile);

        assertNotNull(factory);
    }

}
