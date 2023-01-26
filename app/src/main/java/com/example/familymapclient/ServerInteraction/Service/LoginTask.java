package com.example.familymapclient.ServerInteraction.Service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.ServerProxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Request.LoginRequest;
import Response.EventResponse;
import Response.LoginResponse;
import Response.PersonResponse;
import models.Event;
import models.Person;

public class LoginTask implements Runnable{
    Handler messageHandler;
    LoginRequest loginRequest;
    public LoginTask(Handler messageHandler, LoginRequest loginRequest) {
        this.messageHandler = messageHandler;
        this.loginRequest = loginRequest;
    }

    @Override
    public void run() {
        DataCache dataCache = DataCache.getInstance();
        LoginResponse loginResponse;
        ServerProxy serverProxy = new ServerProxy();

        try {
            loginResponse = serverProxy.login(loginRequest, dataCache.getHost(), dataCache.getPort());
            if(loginResponse.getAuthtoken() == null) {
                sendMessage(false, null, null);
            }else {
                dataCache.setAuthtoken(loginResponse.getAuthtoken());
                dataCache.setPersonID(loginResponse.getPersonID());

                PersonResponse getPeople = serverProxy.getPeople(dataCache.getHost(), dataCache.getPort(), dataCache.getAuthtoken());
                ArrayList<Person> people = getPeople.getData();
                Map<String, Person> peopleMap = new HashMap<>();
                dataCache.setPeople(people);

                EventResponse getEvents = serverProxy.getEvents(dataCache.getHost(), dataCache.getPort(), dataCache.getAuthtoken());
                ArrayList<Event> events = getEvents.getData();
                Map<String, Event> eventMap = new HashMap<>();
                dataCache.setEvents(events);

                for (int i = 0; i < people.size(); i++) {
                    Person person = people.get(i);
                    String personID = person.getPersonID();
                    peopleMap.put(personID, person);
                }

                for (int i = 0; i < events.size(); i++) {
                    Event event = events.get(i);
                    String eventType = event.getEventType().toUpperCase(Locale.ROOT);
                    eventMap.put(eventType, event);
                }

                dataCache.setPeopleMap(peopleMap);
                dataCache.setEventsMap(eventMap);


                sendMessage(true, people.get(0).getFirstName(), people.get(0).getLastName());
            }
        }catch (IOException e) {
        sendMessage(false, null, null);
        e.printStackTrace();
        }
    }

    public void sendMessage(boolean success, String firstName, String lastName){
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putBoolean("success", success);
        bundle.putString("First Name", firstName);
        bundle.putString("Last Name", lastName);
        message.setData(bundle);
        messageHandler.sendMessage(message);
    }
}
