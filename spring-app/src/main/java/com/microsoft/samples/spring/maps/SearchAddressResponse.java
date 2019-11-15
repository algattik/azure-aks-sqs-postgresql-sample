package com.microsoft.samples.spring.maps;

import java.io.Serializable;
import java.util.List;

public class SearchAddressResponse implements Serializable {

    public Summary summary;
    public List<Result> results = null;

}
