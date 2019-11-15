package com.microsoft.samples.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.samples.spring.maps.AzureMapsClient;
import com.microsoft.samples.spring.maps.SearchAddressResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS;

@Component
public class LocationProcessor {

    private static final Logger log = LoggerFactory.getLogger(LocationProcessor.class);

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private LocationRepository repository;

    @Autowired
    private AzureMapsClient maps;

    @SqsListener(value = "${message.queue}", deletionPolicy = ON_SUCCESS)
    void receiveMessage(String requestStr) throws JsonProcessingException {
        LocationRequest request = om.readValue(requestStr, LocationRequest.class);

        Location loc = new Location();
        loc.id = UUID.fromString(request.id);
        loc.name = request.name;

        geocode(request, loc);

        log.info("Saving location with id {}", loc.id);
        repository.save(loc);
    }

    private void geocode(LocationRequest request, Location loc) {
        SearchAddressResponse address = maps.searchAddress(request.name);

        if (address.results != null && !address.results.isEmpty()) {
            SearchAddressResponse.Result result = address.results.get(0);
            if (result.address != null) {
                loc.country = result.address.country;
            }
            if (result.position != null) {
                loc.latitude = result.position.lat;
                loc.longitude = result.position.lon;
            }
        }
    }
}
