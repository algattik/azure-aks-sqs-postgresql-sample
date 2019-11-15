package com.microsoft.samples.spring;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class Location implements Serializable {

    @Id
    public UUID id;

    public String name;

    public String country;

    public Double latitude;

    public Double longitude;

}