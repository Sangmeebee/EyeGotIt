package com.sangmee.eyegottttt.Map;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class CurrentLocation {
    public String sLongitude;
    public String sLatitiude;

    public CurrentLocation(){

    }

    public CurrentLocation(String sLongitude, String sLatitiude) {
        this.sLongitude=sLongitude;
        this.sLatitiude=sLatitiude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sLongitude", sLongitude);
        result.put("sLatitude", sLatitiude);
        return result;
    }
}
