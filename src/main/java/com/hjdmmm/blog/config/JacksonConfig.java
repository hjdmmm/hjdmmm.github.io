package com.hjdmmm.blog.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.time.Instant;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
            .failOnEmptyBeans(false)
            .failOnUnknownProperties(false)
            .featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .featuresToEnable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .serializerByType(Instant.class, InstantSerializer.INSTANCE)
            .deserializerByType(Instant.class, InstantDeserializer.INSTANT)
            // 大数字转为String，防止精度丢失
            .serializerByType(BigInteger.class, ToStringSerializer.instance)
            .serializerByType(Long.class, ToStringSerializer.instance)
            .serializerByType(Long.TYPE, ToStringSerializer.instance)
            ;
    }
}
