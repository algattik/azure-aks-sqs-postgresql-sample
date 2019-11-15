package com.microsoft.samples.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ActiveProfiles("test")
class LocationControllerTest {

    @Autowired
    private LocationController controller;
    @MockBean
    private LocationRepository repository;

    private Location locationInRepository;

    @BeforeEach
    void setUp() {
        locationInRepository = new Location();
        locationInRepository.id = UUID.randomUUID();
        given(repository.findById(locationInRepository.id)).willReturn(Optional.of(locationInRepository));
    }

    @Test
    void when_getLocation_should_returnLocation() throws Exception {
        Optional<Location> location2 = controller.location(locationInRepository.id.toString());
        assertEquals(locationInRepository, location2.get());
    }

    @Test
    void when_getLocation_with_unknownLocation_should_returnNone() throws Exception {
        Optional<Location> location2 = controller.location(UUID.randomUUID().toString());
        assertFalse(location2.isPresent());
    }

}