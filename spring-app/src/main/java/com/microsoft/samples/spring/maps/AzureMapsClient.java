package com.microsoft.samples.spring.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AzureMapsClient {

    private static Logger logger = LoggerFactory.getLogger(AzureMapsClient.class);
    private final RestTemplate restTemplate;
    @Autowired
    private RetryTemplate retryTemplate;
    @Value("${azure.maps.clientId}")
    private String azureMapsClientId;
    @Value("${azure.maps.subscriptionKey}")
    private String azureMapsSubscriptionKey;
    @Value("${azure.maps.endpoint.address}")
    private String endpoint;

    public AzureMapsClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    private SearchAddressResponse getAzureMapsSearchAddress(String query) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-ms-client-id", azureMapsClientId);
        Map<String, Object> urlParameters = new HashMap<>();
        urlParameters.put("query", query);
        urlParameters.put("key", azureMapsSubscriptionKey);
        logger.info("Querying {} with query {}", endpoint, query);

        ResponseEntity<SearchAddressResponse> exchange = restTemplate.exchange(endpoint,
                HttpMethod.GET, new HttpEntity(headers), new ParameterizedTypeReference<SearchAddressResponse>() {
                }, urlParameters);
        return exchange.getBody();
    }

    @CircuitBreaker(maxAttempts = 5, openTimeout = 30000L, resetTimeout = 60000L)
    public SearchAddressResponse searchAddress(String query) {
        return retryTemplate.execute(context -> {
            logger.info(String.format("Retry count %d", context.getRetryCount()));
            return getAzureMapsSearchAddress(query);
        });
    }

}