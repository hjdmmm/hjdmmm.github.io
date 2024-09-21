package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.VirusTotalConfig;
import com.hjdmmm.blog.enums.VirusScanErrorTypeEnum;
import com.hjdmmm.blog.exception.VirusScannerException;
import com.hjdmmm.blog.service.VirusScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class VirusTotalVirusScanner implements VirusScanner {
    private static final int RETRY_TIMES = 3;

    private static final Duration RETRY_DURATION = Duration.ofSeconds(10);

    private final RestTemplate restTemplate;

    private final VirusTotalConfig virusTotalConfig;

    public VirusTotalVirusScanner(RestTemplate restTemplate, VirusTotalConfig virusTotalConfig) {
        this.restTemplate = restTemplate;
        this.virusTotalConfig = virusTotalConfig;
    }

    @Override
    public boolean scan(File file) throws VirusScannerException {
        String key = virusTotalConfig.getKey();
        if (!StringUtils.hasText(key)) {
            throw new VirusScannerException(VirusScanErrorTypeEnum.NO_SCANNER_CONFIG, "VirusTotal未配置");
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

            try {
                Thread.sleep(RETRY_DURATION.toMillis());
            } catch (InterruptedException e) {
                throw new VirusScannerException(VirusScanErrorTypeEnum.THIRD_PARTY_FAIL, String.format("url： %s ，调用VirusTotal分析数据接口时，重试时Thread.sleep被打断", analysisUrl), e);
            }
        }

        throw new VirusScannerException(VirusScanErrorTypeEnum.THIRD_PARTY_TIMEOUT, String.format("url： %s ，调用VirusTotal分析数据接口超时", analysisUrl));
    }

    private String upload(File file) throws VirusScannerException {
        String key = virusTotalConfig.getKey();
        String uploadUrl = virusTotalConfig.getUrl();
        if (!StringUtils.hasText(key) || !StringUtils.hasText(uploadUrl)) {
            throw new VirusScannerException(VirusScanErrorTypeEnum.NO_SCANNER_CONFIG, "VirusTotal未配置");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-apikey", key);
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("accept", "application/json");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(uploadUrl, httpEntity, Map.class);
        } catch (Exception e) {
            throw new VirusScannerException(VirusScanErrorTypeEnum.THIRD_PARTY_FAIL, String.format("文件 %s ，调用VirusTotal上传文件接口失败", file.getName()), e);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new VirusScannerException(VirusScanErrorTypeEnum.THIRD_PARTY_FAIL, String.format("文件 %s ，调用VirusTotal上传文件接口时，HTTP状态码为：%s", file.getName(), responseEntity.getStatusCode()));
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
                .orElseThrow(() -> new VirusScannerException(VirusScanErrorTypeEnum.THIRD_PARTY_RESULT_ERROR, String.format("文件 %s ，调用VirusTotal上传接口时，处理返回结果异常，", file.getName()) + responseEntity.getBody(), new ClassCastException()));
    }

    private Boolean doScan(String analysisUrl, HttpEntity<Void> httpEntity) throws VirusScannerException {
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.exchange(analysisUrl, HttpMethod.GET, httpEntity, Map.class);
        } catch (Exception e) {
            log.error("调用VirusTotal分析数据接口失败", e);
            return null;
        }

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
                .orElseThrow(() -> new VirusScannerException(VirusScanErrorTypeEnum.THIRD_PARTY_RESULT_ERROR, String.format("url： %s ，调用VirusTotal分析数据接口时处理返回结果异常，", analysisUrl) + responseEntity.getBody(), new ClassCastException()));

        String status = Optional.ofNullable(attributes.get("status"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElseThrow(() -> new VirusScannerException(VirusScanErrorTypeEnum.THIRD_PARTY_RESULT_ERROR, String.format("url： %s ，调用VirusTotal分析数据接口时处理返回结果异常，", analysisUrl) + responseEntity.getBody(), new ClassCastException()));

        if (!"completed".equals(status)) {
            return null;
        }

        int maliciousCount = Optional.ofNullable(attributes.get("stats"))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(stats -> stats.get("malicious"))
                .filter(Integer.class::isInstance)
                .map(Integer.class::cast)
                .orElseThrow(() -> new VirusScannerException(VirusScanErrorTypeEnum.THIRD_PARTY_RESULT_ERROR, String.format("url： %s ，调用VirusTotal分析数据接口时处理返回结果异常，", analysisUrl) + responseEntity.getBody(), new ClassCastException()));

        return maliciousCount != 0;
    }
}
