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

    public String getEventType() {
        return eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventTypeLabel() {
        return eventType.split("#")[1];
    }

    public String getEventNameLabel() {
        return eventName.split("#")[1];
    }
}
