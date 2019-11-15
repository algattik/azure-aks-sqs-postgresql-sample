package com.microsoft.samples.spring;

import java.util.Optional;
import java.util.UUID;

import com.microsoft.samples.spring.Location;
import com.microsoft.samples.spring.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationRepository repository;

    @GetMapping
    public Optional<Location> location(@RequestParam(value="id") String id) {
        return repository.findById(UUID.fromString(id));
    }
}