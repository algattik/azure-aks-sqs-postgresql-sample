package com.microsoft.samples.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LocationRequestReceiver {

    private static final Logger log = LoggerFactory.getLogger(LocationRequestReceiver.class);

    @Autowired
    private LocationRepository repository;

    private static ObjectMapper om =  new ObjectMapper();

    @MessageMapping("${message.queue}")
    private void receiveMessage(String requestStr, @Header("SenderId") String senderId) throws JsonProcessingException {
        LocationRequest request = om.readValue(requestStr, LocationRequest.class);
        Location loc = new Location();
        loc.id = UUID.fromString(request.id);
        loc.name = request.name;
        log.info("Saving location with id {}", loc.id);
        repository.save(loc);
    }
}
