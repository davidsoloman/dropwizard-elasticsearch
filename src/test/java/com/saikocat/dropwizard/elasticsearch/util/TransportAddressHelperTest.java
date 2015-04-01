package com.saikocat.dropwizard.elasticsearch.util;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link TransportAddressHelper}.
 */
public class TransportAddressHelperTest {

    private static final int ES_DEFAULT_PORT = 9300;
    private static final int EXAMPLE_NET_PORT = 1234;
    private static final int EXAMPLE_ORG_PORT = 5678;
    private static final int LOCALHOST_PORT = 1234;
    private static final int HOSTS_COUNT = 3;

    @Test(expected = NullPointerException.class)
    public void fromHostAndPortWithNullShouldFail() {
        TransportAddressHelper.fromHostAndPort(null);
    }

    @Test
    public void fromHostAndPortsWithNullShouldReturnEmptyArray() {
        TransportAddress[] result = TransportAddressHelper.fromHostAndPorts(null);

        assertEquals(0, result.length);
    }

    @Test
    public void fromHostAndPortsWithEmptyListShouldReturnEmptyArray() {
        TransportAddress[] result = TransportAddressHelper.fromHostAndPorts(Collections.<HostAndPort>emptyList());

        assertEquals(0, result.length);
    }

    @Test
    public void fromHostAndPortWithoutPortShouldUseDefaultPort() {
        InetSocketTransportAddress result = (InetSocketTransportAddress) TransportAddressHelper
                .fromHostAndPort(HostAndPort.fromString("localhost"));

        assertEquals("localhost", result.address().getHostName());
        assertEquals(ES_DEFAULT_PORT, result.address().getPort());
    }

    @Test
    public void fromHostAndPortWithCorrectDataShouldSucceed() {
        InetSocketTransportAddress result = (InetSocketTransportAddress) TransportAddressHelper
                .fromHostAndPort(HostAndPort.fromParts("localhost", LOCALHOST_PORT));

        assertEquals("localhost", result.address().getHostName());
        assertEquals(LOCALHOST_PORT, result.address().getPort());
    }

    @Test
    public void fromHostAndPostWithCorrectDataShouldSucceed() {
        final List<HostAndPort> hostAndPorts = ImmutableList.of(
                HostAndPort.fromParts("example.net", EXAMPLE_NET_PORT),
                HostAndPort.fromParts("example.com", EXAMPLE_ORG_PORT),
                HostAndPort.fromString("example.org")
        );
        final TransportAddress[] result = TransportAddressHelper.fromHostAndPorts(hostAndPorts);

        assertEquals(HOSTS_COUNT, result.length);

        for (int i = 0; i < result.length; i++) {
            final InetSocketTransportAddress transportAddress = (InetSocketTransportAddress) result[i];
            assertEquals(hostAndPorts.get(i).getHostText(), transportAddress.address().getHostName());
            assertEquals(hostAndPorts.get(i).getPortOrDefault(ES_DEFAULT_PORT), transportAddress.address().getPort());
        }
    }
}
