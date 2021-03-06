package com.microsoft.samples.spring;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LocationRepository extends CrudRepository<Location, UUID> {
}