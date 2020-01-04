package com.sangmee.eyegottttt.Login;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Signup {
    public String signup_u_pw;
    public String who;
    public String user_id;
    public String topic;

    public Signup(){

    }

    public Signup(String signup_u_pw, String user_id, String who, String topic) {
        this.signup_u_pw = signup_u_pw;
        this.user_id=user_id;
        this.who=who;
        this.topic = topic;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("signup_u_pw", signup_u_pw);
        result.put("user_id", user_id);
        result.put("who", who);
        result.put("topic", topic);
        return result;
    }
}
