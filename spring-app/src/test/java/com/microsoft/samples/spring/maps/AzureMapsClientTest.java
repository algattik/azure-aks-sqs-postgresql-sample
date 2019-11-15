package com.microsoft.samples.spring.maps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(SpringExtension.class)
@RestClientTest(AzureMapsClient.class)
@ActiveProfiles("test")
class AzureMapsClientTest {

    @Value("classpath:com/microsoft/samples/spring/maps/sample-search-address-response.json")
    Resource sampleResponse;
    @Autowired
    private AzureMapsClient service;
    @Autowired
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        this.server.expect(requestTo("https://atlas.microsoft.com/search/address/json?api-version=1.0&query=Switzerland&subscription-key=testKey")
        )
                .andExpect(header("x-ms-client-id", "testClientId"))
                .andRespond(withSuccess(sampleResponse, MediaType.APPLICATION_JSON));
    }

    @Test
    void when_searchAddress_should_returnCountry() {
        SearchAddressResponse result = service.searchAddress("Switzerland");
        assertEquals("CHE", result.results.get(0).address.countryCodeISO3);
    }

}