package com.saikocat.dropwizard.elasticsearch;

import io.dropwizard.Configuration;

public interface EsConfiguration<T extends Configuration> {
    EsFactory getEsFactory(T configuration);
}
