package org.acme.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {
        mapper.setDefaultPropertyInclusion(JsonInclude.Value.construct(
                JsonInclude.Include.NON_NULL,
                JsonInclude.Include.ALWAYS));
    }
}