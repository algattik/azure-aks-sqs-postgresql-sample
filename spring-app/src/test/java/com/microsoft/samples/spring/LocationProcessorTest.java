package com.microsoft.samples.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.samples.spring.maps.AzureMapsClient;
import com.microsoft.samples.spring.maps.SearchAddressResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ActiveProfiles("test")
class LocationProcessorTest {

    private static final ObjectMapper om = new ObjectMapper();
    @Autowired
    private LocationProcessor receiver;
    @MockBean
    private AzureMapsClient mapsClient;
    @MockBean
    private LocationRepository repository;
    @Value("classpath:com/microsoft/samples/spring/maps/sample-search-address-response.json")
    Resource sampleResponse;

    @Test
    void when_receiveMessage() throws Exception {
        SearchAddressResponse response = om.readValue(sampleResponse.getURL(), SearchAddressResponse.class);
        given(mapsClient.searchAddress(any(String.class))).willReturn(response);

        UUID uuid = UUID.randomUUID();
        receiver.receiveMessage("{\"id\":\"" + uuid + "\",\"name\":\"Mars\"}");

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(repository).save(locationCaptor.capture());
        verifyNoMoreInteractions(repository);

        Location location = locationCaptor.getValue();
        assertEquals(uuid, location.id);
        assertEquals("Mars", location.name);
        assertEquals("Switzerland", location.country);
        assertEquals(46.948, location.latitude);
        assertEquals(7.44813, location.longitude);
    }

}