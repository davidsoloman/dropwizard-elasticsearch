package com.saikocat.dropwizard.elasticsearch;

import io.dropwizard.lifecycle.Managed;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Dropwizard managed Elasticsearch {@link Client}. Depending on the a Node Client or
 * a TransportClient is being created and its lifecycle is managed by Dropwizard.
 *
 * @see <a href="http://www.elasticsearch.org/guide/reference/java-api/client/#nodeclient">Node Client</a>
 * @see <a href="http://www.elasticsearch.org/guide/reference/java-api/client/#transportclient">Transport Client</a>
 */
public class EsManager implements Managed {
    private Node node = null;
    private Client client = null;

    /**
     * Create a new managed Elasticsearch {@link Client} from the provided {@link Node}.
     *
     * @param node a valid {@link Node} instance
     */
    public EsManager(final Node node) {
        this.node = checkNotNull(node, "Elasticsearch node must not be null");
        this.client = node.client();
    }


    /**
     * Create a new managed Elasticsearch {@link Client} from the provided {@link Client}.
     *
     * @param client an initialized {@link Client} instance
     */
    public EsManager(Client client) {
        this.client = checkNotNull(client, "Elasticsearch client must not be null");
    }

    /**
     * Starts the Elasticsearch {@link Node} (if appropriate). Called <i>before</i> the service becomes available.
     *
     * @throws Exception if something goes wrong; this will halt the service startup.
     */
    @Override
    public void start() throws Exception {
        startNode();
    }

    /**
     * Stops the Elasticsearch {@link Client} and (if appropriate) {@link Node} objects. Called <i>after</i> the service
     * is no longer accepting requests.
     *
     * @throws Exception if something goes wrong.
     */
    @Override
    public void stop() throws Exception {
        closeClient();
        closeNode();
    }

    /**
     * Get the managed Elasticsearch {@link Client} instance.
     *
     * @return a valid Elasticsearch {@link Client} instance
     */
    public Client getClient() {
        return client;
    }

    private Node startNode() {
        if (null != node) {
            return node.start();
        }

        return null;
    }

    private void closeNode() {
        if (null != node && !node.isClosed()) {
            node.close();
        }
    }

    private void closeClient() {
        if (null != client) {
            client.close();
        }
    }
}
