package com.saikocat.dropwizard.elasticsearch;

import com.saikocat.dropwizard.elasticsearch.util.TransportAddressHelper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.net.HostAndPort;
import com.google.common.base.Optional;
import io.dropwizard.setup.Environment;
import io.dropwizard.validation.ValidationMethod;
import org.hibernate.validator.constraints.NotEmpty;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Configuration class for Elasticsearch related settings.
 */
public class EsFactory {
    private static final String CLUSTER_NAME = "elasticsearch";

    @JsonProperty
    @NotNull
    private List<HostAndPort> servers = Collections.emptyList();

    @JsonProperty
    @NotEmpty
    private String clusterName = CLUSTER_NAME;

    @JsonProperty
    private boolean nodeClient = true;

    @JsonProperty
    @NotNull
    private Map<String, String> settings = Collections.emptyMap();

    public List<HostAndPort> getServers() {
        return servers;
    }

    public String getClusterName() {
        return clusterName;
    }

    public boolean isNodeClient() {
        return nodeClient;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    @JsonIgnore
    @ValidationMethod
    public boolean isValidConfig() {
        return nodeClient || !servers.isEmpty();
    }

    public Client build(Environment environment) {
        final Settings esSettings = ImmutableSettings.settingsBuilder()
                .put(getSettings())
                .put("cluster.name", getClusterName())
                .build();
        Optional<Client> client = Optional.absent();
        Optional<Node> node = Optional.absent();
        Optional<EsManager> manager = Optional.absent();

        if (isNodeClient()) {
            node = Optional.of(buildNode(esSettings));
            client = Optional.of(node.get().client());
            manager = Optional.of(new EsManager(node.get()));
        } else {
            final TransportAddress[] addresses = TransportAddressHelper.fromHostAndPorts(getServers());
            client = Optional.of((Client) buildTransportClient(esSettings).addTransportAddresses(addresses));
            manager = Optional.of(new EsManager(client.get()));
        }

        environment.lifecycle().manage(manager.get());

        return client.get();
    }

    Node buildNode(Settings esSettings) {
        return nodeBuilder()
            .client(true)
            .settings(esSettings)
            .build();
    }

    TransportClient buildTransportClient(Settings esSettings) {
        return new TransportClient(esSettings);
    }
}
