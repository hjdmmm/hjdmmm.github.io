package com.hjdmmm.blog.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class JacksonConfig {
    private static final Locale DEFAULT_LOCALE = Locale.CHINA;
    private static final String DEFAULT_TIME_ZONE = "GMT+8";
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT, DEFAULT_LOCALE);
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT, DEFAULT_LOCALE);
    private static final DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT, DEFAULT_LOCALE);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                .failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
                .locale(DEFAULT_LOCALE)

                // 时间配置
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .timeZone(DEFAULT_TIME_ZONE)
                .simpleDateFormat(DEFAULT_DATE_TIME_FORMAT)
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DEFAULT_DATE_TIME_FORMATTER))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DEFAULT_DATE_FORMATTER))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(DEFAULT_TIME_FORMATTER))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DEFAULT_DATE_TIME_FORMATTER))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DEFAULT_DATE_FORMATTER))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(DEFAULT_TIME_FORMATTER))

                // 大数字转为String，防止精度丢失
                .serializerByType(BigInteger.class, ToStringSerializer.instance)
                .serializerByType(Long.class, ToStringSerializer.instance)
                .serializerByType(Long.TYPE, ToStringSerializer.instance);
    }
}
