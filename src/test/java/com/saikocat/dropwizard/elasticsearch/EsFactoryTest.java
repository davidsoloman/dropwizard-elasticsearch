package com.saikocat.dropwizard.elasticsearch;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.Node;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class EsFactoryTest {
    @Mock
    private Environment environment;
    @Mock
    private LifecycleEnvironment lifecycleEnvironment;
    @Mock
    private TransportClient transportClient;
    @Mock
    private Node node;
    @Mock
    private Client client;
    @Spy
    private EsFactory factory = new EsFactory();

    private ImmutableMap.Builder<String, String> settingsBuilders = new ImmutableMap.Builder<>();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
    }

    @Test
    public void shouldBuildNodeClient()
            throws URISyntaxException, IOException, ConfigurationException {
        doReturn(settingsBuilders.build()).when(factory).getSettings();
        doReturn(true).when(factory).isNodeClient();
        doReturn(node).when(factory).buildNode(any(Settings.class));
        when(node.client()).thenReturn(client);

        factory.build(environment);

        verify(node, times(2)).client();
    }

    @Test
    public void shouldBuildTransportClient()
            throws URISyntaxException, IOException, ConfigurationException {
        ImmutableMap<String, String> settings = settingsBuilders
                .put("nodeClient", "false")
                .put("clusterName", "foo")
                .build();
        doReturn(settings).when(factory).getSettings();
        doReturn(false).when(factory).isNodeClient();
        doReturn(transportClient).when(factory).buildTransportClient(any(Settings.class));
        when(transportClient.addTransportAddresses(Matchers.<TransportAddress>anyVararg())).thenReturn(transportClient);
        factory.build(environment);

        verify(factory, times(1)).buildTransportClient(any(Settings.class));
        verify(transportClient, times(1)).addTransportAddresses(Matchers.<TransportAddress>anyVararg());
    }

    @Test
    public void shouldManageLifeCycle() throws URISyntaxException, IOException, ConfigurationException {
        doReturn(settingsBuilders.build()).when(factory).getSettings();
        doReturn(true).when(factory).isNodeClient();
        doReturn(node).when(factory).buildNode(any(Settings.class));
        when(node.client()).thenReturn(client);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);

        factory.build(environment);

        verify(lifecycleEnvironment, times(1)).manage(any(EsManager.class));
    }
}
