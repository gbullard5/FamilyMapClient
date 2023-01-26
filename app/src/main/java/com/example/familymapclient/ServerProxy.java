package com.example.familymapclient;


import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import Request.LoginRequest;
import Request.RegisterRequest;
import Response.EventResponse;
import Response.LoginResponse;
import Response.PersonResponse;
import Response.RegisterResponse;

public class ServerProxy {
    private static ServerProxy serverProxy;
    public static ServerProxy initialize(){
        if(serverProxy == null){
            serverProxy = new ServerProxy();
        }
        return serverProxy;
    }
    public LoginResponse login(LoginRequest loginRequest, String serverHost, String serverPort) throws IOException {
        try{
            URL url=new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http =  (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            Gson gson = new Gson();
            String reqData = gson.toJson(loginRequest);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();
            http.connect();
            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                LoginResponse loginResponse = gson.fromJson(respData, LoginResponse.class);
                return loginResponse;
            }else{
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                return new LoginResponse(false, http.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new LoginResponse(false, "Login Error");
        }

    }
    public RegisterResponse register(RegisterRequest registerRequest, String serverHost, String serverPort) throws IOException {
        try{
            URL url=new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http =  (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            Gson gson = new Gson();
            String reqData = gson.toJson(registerRequest);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();
            http.connect();
            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                RegisterResponse registerResponse = gson.fromJson(respData, RegisterResponse.class);
                return registerResponse;
            }else{
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                RegisterResponse registerResponse = new RegisterResponse(http.getResponseMessage(), false);
                return registerResponse;
            }
        } catch (IOException e) {
            return new RegisterResponse("Register Error", false);
        }

    }

    public EventResponse getEvents(String serverHost, String serverPort, String authtoken) {
        try {
            URL url=new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http =  (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authtoken);
            http.connect();
            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                Gson gson = new Gson();
                EventResponse eventResponse = gson.fromJson(respData, EventResponse.class);
                return eventResponse;
            }else{
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                return new EventResponse(false, http.getResponseMessage(), null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new EventResponse(false,"Error retrieving Event data", null);
        }
    }
    public PersonResponse getPeople(String serverHost, String serverPort, String authtoken) {
        try {
            URL url=new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http =  (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authtoken);
            http.connect();
            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                Gson gson = new Gson();
                PersonResponse personResponse = gson.fromJson(respData, PersonResponse.class);
                return personResponse;
            }else{
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                return new PersonResponse(false, http.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new PersonResponse(false, "Error retrieving Person data");
        }
    }
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();

    }
}
