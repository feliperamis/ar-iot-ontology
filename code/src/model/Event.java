package model;

import java.io.Serializable;

public class Event implements Serializable {

    private String eventType;
    private String eventName;

    public Event(String eventType, String eventName) {
        this.eventType = eventType;
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return eventName + "(" + eventType + ")";
    }
}
