package com.microsoft.samples.spring.maps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchAddressResponse implements Serializable {

    public Summary summary;
    public List<Result> results = new ArrayList<>();


    public static class Summary implements Serializable {

        public String query;
        public String queryType;
        public Integer queryTime;
        public Integer numResults;
        public Integer offset;
        public Integer totalResults;
        public Integer fuzzyLevel;

    }


    public static class Address implements Serializable {

        public String countryCode;
        public String country;
        public String countryCodeISO3;
        public String freeformAddress;
        public String municipality;
        public String countrySecondarySubdivision;
        public String countryTertiarySubdivision;
        public String countrySubdivision;
        public String countrySubdivisionName;
        public String municipalitySubdivision;

    }


    public static class BoundingBox implements Serializable {

        public TopLeftPoint topLeftPoint;
        public BtmRightPoint btmRightPoint;

    }


    public static class BtmRightPoint implements Serializable {

        public Double lat;
        public Double lon;

    }


    public static class DataSources implements Serializable {

        public Geometry geometry;

    }


    public static class Geometry implements Serializable {

        public String id;

    }


    public static class Position implements Serializable {

        public Double lat;
        public Double lon;

    }


    public static class Result implements Serializable {

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


    public static class TopLeftPoint implements Serializable {

        public Double lat;
        public Double lon;

    }


    public static class Viewport implements Serializable {

        public TopLeftPoint topLeftPoint;
        public BtmRightPoint btmRightPoint;

    }
}
