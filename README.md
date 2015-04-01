Dropwizard Elasticsearch
========================

A bundle approach for using [Elasticsearch] [1] in a [Dropwiwzard] [2] >= 0.8.0 application. It is a port of version 0.7.x to use the new bundle system (auto-registration of cluster healthchecks and managed client). Addtional tests are provided. The binary is distributed via [Bintray](https://bintray.com/)

[1]: http://www.elastic.co
[2]: http://dropwizard.io/0.8.0/docs


Usage
-----

Sample yaml configuration

```yaml
elasticsearch:
  nodeClient: false
  servers:
    - 192.168.1.2
    - 192.168.1.3:9300
```

The MainConfiguration class

```java
// ...
import com.saikocat.dropwizard.elasticsearch.EsFactory;
// ...

public class MainConfiguration extends Configuration {

    private EsFactory esFactory = new EsFactory();

    @JsonProperty("elasticsearch")
    public EsFactory getEsFactory() {
        return esFactory;
    }

    @JsonProperty("elasticsearch")
    public void setEsFactory(EsFactory factory) {
        this.esFactory = factory;
    }
}
```

The MainApplication class

```java
// ...
import com.saikocat.dropwizard.elasticsearch.EsBundle;
import com.saikocat.dropwizard.elasticsearch.EsFactory;
// ...

public class MainApplication extends Application<MainConfiguration> {
    private final EsBundle<MainConfiguration> elasticsearch =
            new EsBundle<MainConfiguration>() {
                @Override
                public EsFactory getEsFactory(
                        MainConfiguration configuration) {
                    return configuration.getEsFactory();
                }
            };

    @Override
    public void initialize(Bootstrap<MainConfiguration> bootstrap) {
        bootstrap.addBundle(elasticsearch);
    }

    @Override
    public void run(MainConfiguration configuration,
                    Environment environment) {
        // Get the real ES client: org.elasticsearch.client.Client
        // via `elasticsearch` bundle created above
        // and pass it to a service. Here EsSearchService is an impl of
        // SearchService, and has a contructor that take in an ES Client
        final SearchService searchService = new EsSearchService(elasticsearch.getClient());
        // ...
        // Register your resource with the above searchService
    }
}
```


Configuration
-------------

The following configuration settings are supported by `EsFactory`:

* `nodeClient`: When `true`, `ManagedEsClient` will create a `NodeClient`, otherwise a `TransportClient`; default: `true`. Read more on [NodeClient vs TransportClient](http://www.elastic.co/guide/en/elasticsearch/guide/current/_transport_client_versus_node_client.html)
* `servers`: A list of servers for usage with the created TransportClient if `nodeClient` is `false` i.e: `192.168.1.2, 192.168.1.3:9300`
* `clusterName`: The name of the Elasticsearch cluster; default: "elasticsearch"
* `settings`: Any additional settings for Elasticsearch, see [Configuration](http://www.elastic.co/guide/en/elasticsearch/reference/master/setup-configuration.html)

An example configuration file for creating a Node Client could like this:

    clusterName: Staging
    settings:
      node.name: StagingNode2

An example confiugration file for creating a Transport Client:

    nodeClient: false
    servers:
      - 192.168.1.2
      - 192.168.1.3:9300


Maven Artifacts
---------------

This project is available on JCenter. To add it to your project simply add the following dependencies to your
`pom.xml` (or better yet use gradle instead :D)

    <dependency>
      <groupId>com.saikocat.dropwizard-bundles</groupId>
      <artifactId>dropwizard-elasticsearch</artifactId>
      <version>0.1.0</version>
    </dependency>

My request to get this included in JCenter is still being processed. Meanwhile you can add it via this instruction
https://bintray.com/package/buildSettings?pkgPath=%2Fsaikocat%2Fmaven%2Fdropwizard-elasticsearch


Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/saikocat/dropwizard-elasticsearch/issues)


Acknowledgements
----------------

Thanks to Jochen Schalanda for his initial work of [Dropwizard-Elasticsearch 0.7.x](https://github.com/dropwizard/dropwizard-elasticsearch)
Thanks to Chua Soon Dee [sdchua](https://github.com/chuasoondee) for contributing to this bundle as well as fixing some of my unit tests.

License
-------

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
