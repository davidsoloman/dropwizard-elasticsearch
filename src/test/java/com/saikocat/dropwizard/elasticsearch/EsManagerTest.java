package com.saikocat.dropwizard.elasticsearch;

import io.dropwizard.lifecycle.Managed;

import org.junit.Test;
import org.elasticsearch.node.Node;
import org.elasticsearch.client.Client;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EsManagerTest {
    @Test(expected = NullPointerException.class)
    public void ensureNodeIsNotNull() {
        new EsManager((Node) null);
    }

    @Test(expected = NullPointerException.class)
    public void ensureClientIsNotNull() {
        new EsManager((Client) null);
    }

    @Test
    public void stopShouldCloseTheClient() throws Exception {
        Client client = mock(Client.class);
        Managed managed = new EsManager(client);

        managed.start();
        managed.stop();

        verify(client).close();
    }

    @Test
    public void lifecycleMethodsShouldStartAndCloseTheNode() throws Exception {
        Node node = mock(Node.class);
        when(node.isClosed()).thenReturn(false);
        Managed managed = new EsManager(node);

        managed.start();
        managed.stop();

        verify(node).start();
        verify(node).close();
    }

    @Test
    public void managedEsManagertWithNodeShouldReturnClient() throws Exception {
        Client client = mock(Client.class);
        Node node = mock(Node.class);
        when(node.client()).thenReturn(client);

        EsManager managed = new EsManager(node);

        assertSame(client, managed.getClient());
    }
}
