package com.microsoft.samples.spring.maps;

import java.io.Serializable;

public class Result implements Serializable {

    public String type;
    public String id;
    public Double score;
    public String entityType;
    public Address address;
    public Position position;
    public Viewport viewport;
    public BoundingBox boundingBox;
    public DataSources dataSources;

}
