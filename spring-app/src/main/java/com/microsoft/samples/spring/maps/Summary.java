package com.microsoft.samples.spring.maps;

import java.io.Serializable;

public class Summary implements Serializable {

    public String query;
    public String queryType;
    public Integer queryTime;
    public Integer numResults;
    public Integer offset;
    public Integer totalResults;
    public Integer fuzzyLevel;

}
