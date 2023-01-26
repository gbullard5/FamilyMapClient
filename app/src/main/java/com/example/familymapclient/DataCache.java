package com.example.familymapclient;



import android.provider.Settings;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import models.Event;
import models.Person;

public class DataCache {
    private static DataCache instance = new DataCache();

    public static DataCache getInstance(){
        return instance;
    }

    private DataCache(){
    }

    private String authtoken;
    private String host;
    private String port;
    private Person person;

    private String personID;
    private String eventID;

    Map<String, Event> eventsMap = new HashMap<>();
    Map<String, Marker> markerByID = new HashMap<>();
    Map<String, Event> eventMapByID = new HashMap<>();

    ArrayList<Person> people;
    ArrayList<Event> events;

    public Map<String, Event> getEventMapByID() {
        return eventMapByID;
    }

    public void setEventMapByID(Map<String, Event> eventMapByID) {
        this.eventMapByID = eventMapByID;
    }

    public void setPeopleMap(Map<String, Person> peopleMap) {
        this.peopleMap = peopleMap;
    }

    public Map<String, Person> getPeopleMap() {
        return peopleMap;
    }

    Map<String, Person> peopleMap = new HashMap<>();

    public Map<String, Event> getEventsMap() {
        return eventsMap;
    }

    public void setEventsMap(Map<String, Event> eventsMap) {
        this.eventsMap = eventsMap;
    }
    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getPersonID() { return personID; }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }



    public ArrayList<Person> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }


    public Map<String, Marker> getMarkerByID() {
        return markerByID;
    }

    public void setMarkerByID(Map<String, Marker> markerByID) {
        this.markerByID = markerByID;
    }

}
