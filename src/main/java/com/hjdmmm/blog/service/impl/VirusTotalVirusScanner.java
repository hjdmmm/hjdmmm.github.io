package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.VirusTotalConfig;
import com.hjdmmm.blog.service.VirusScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class VirusTotalVirusScanner implements VirusScanner {
    private static final Duration REST_TEMPLATE_READ_TIMEOUT = Duration.ofSeconds(10);

    private static final Duration REST_TEMPLATE_CONNECT_TIMEOUT = Duration.ofSeconds(15);

    private static final int RETRY_TIMES = 3;

    private static final Duration RETRY_DURATION = Duration.ofSeconds(3);

    private final RestTemplate restTemplate;

    private final VirusTotalConfig virusTotalConfig;

    public VirusTotalVirusScanner(VirusTotalConfig virusTotalConfig, RestTemplateBuilder restTemplateBuilder) {
        this.virusTotalConfig = virusTotalConfig;
        restTemplate = restTemplateBuilder
            .setReadTimeout(REST_TEMPLATE_READ_TIMEOUT)
            .setConnectTimeout(REST_TEMPLATE_CONNECT_TIMEOUT)
            .build();
    }

    @Override
    public boolean scan(Path file) throws Exception {
        String key = virusTotalConfig.getKey();
        if (!StringUtils.hasText(key)) {
            throw new IllegalStateException("VirusTotal未配置");
        }

        // 上传文件，获取analysis Url
        String analysisUrl = upload(file);

        // 获取对应分析结果
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-apikey", key);
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("accept", "application/json");
        HttpEntity<Void> httpEntity = new HttpEntity<>(null, headers);

        for (int time = 0; time < RETRY_TIMES; time++) {
            Boolean result = doScan(analysisUrl, httpEntity);
            if (result != null) {
                return result;
            }

            Thread.sleep(RETRY_DURATION.toMillis());
        }

        throw new IOException("调用VirusTotal分析数据接口超时");
    }

    private String upload(Path file) throws Exception {
        String key = virusTotalConfig.getKey();
        String uploadUrl = virusTotalConfig.getUrl();
        if (!StringUtils.hasText(key) || !StringUtils.hasText(uploadUrl)) {
            throw new IllegalStateException("VirusTotal未配置");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-apikey", key);
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("accept", "application/json");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<?> responseEntity = restTemplate.postForEntity(uploadUrl, httpEntity, Map.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new IOException("调用VirusTotal上传文件接口失败, HTTP状态码为: " + responseEntity.getStatusCode());
        }

        return Optional.of(responseEntity)
            .map(HttpEntity::getBody)
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(body -> body.get("data"))
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(data -> data.get("links"))
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(links -> links.get("self"))
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .orElseThrow(() -> new IOException("调用VirusTotal上传接口时，处理返回结果异常, 响应体为: " + responseEntity.getBody()));
    }

    private Boolean doScan(String analysisUrl, HttpEntity<Void> httpEntity) throws Exception {
        ResponseEntity<?> responseEntity = restTemplate.exchange(analysisUrl, HttpMethod.GET, httpEntity, Map.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        Map<?, ?> attributes = Optional.of(responseEntity)
            .map(HttpEntity::getBody)
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(body -> body.get("data"))
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(data -> data.get("attributes"))
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .orElseThrow(() -> new IOException("调用VirusTotal分析数据接口时，处理返回结果异常, 响应体为: " + responseEntity.getBody()));

        String status = Optional.ofNullable(attributes.get("status"))
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .orElseThrow(() -> new IOException("调用VirusTotal分析数据接口时，处理返回结果异常, 响应体为: " + responseEntity.getBody()));

        if (!"completed".equals(status)) {
            return null;
        }

        int maliciousCount = Optional.ofNullable(attributes.get("stats"))
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(stats -> stats.get("malicious"))
            .filter(Integer.class::isInstance)
            .map(Integer.class::cast)
            .orElseThrow(() -> new IOException("调用VirusTotal分析数据接口时，处理返回结果异常, 响应体为: " + responseEntity.getBody()));

        return maliciousCount != 0;
    }
}
