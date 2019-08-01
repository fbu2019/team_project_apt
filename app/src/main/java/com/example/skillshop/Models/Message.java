package com.example.skillshop.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_ID_KEY = "userId";
    public static final String BODY_KEY = "body";
    public static final String NAME_KEY = "name";
    public static final String WORKSHOP_KEY = "workshop";
    public static final String TEACHER_KEY = "teacher";


    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }
    public String getName() {
        return getString(NAME_KEY);
    }
    public String getWorkshop() {
        return getString(WORKSHOP_KEY);
    }
    public String getTeacher() {
        return getString(TEACHER_KEY);
    }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
    public void setName(String name) {
        put(NAME_KEY, name);
    }
    public void setWorkshop(String objectId) {
        put(WORKSHOP_KEY, objectId);
    }
    public void setTeacher(String objectId) {
        put(TEACHER_KEY, objectId);
    }


}
