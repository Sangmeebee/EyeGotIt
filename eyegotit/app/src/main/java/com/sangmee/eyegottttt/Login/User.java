package com.sangmee.eyegottttt.Login;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String spot;
    public String message;
    public String sLongitude;
    public String sLatitiude;

    public User(){

    }

    public User(String spot, String message, String sLongitude, String sLatitiude) {
        this.spot = spot;
        this.message = message;
        this.sLongitude=sLongitude;
        this.sLatitiude=sLatitiude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("spot", spot);
        result.put("message", message);
        result.put("sLongitude", sLongitude);
        result.put("sLatitude", sLatitiude);
        return result;
    }
}
