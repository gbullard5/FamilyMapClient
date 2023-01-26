package com.example.familymapclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;
import Request.LoginRequest;
import Request.RegisterRequest;
import Response.LoginResponse;
import Response.RegisterResponse;

public class JUnitTests {
    DataCache dataCache = DataCache.getInstance();
    ServerProxy serverProxy;

    @Before
    public void setUp(){
        serverProxy = new ServerProxy();
        dataCache.setHost("localhost");
        dataCache.setPort("8080");
    }

    @Before
    public void Register() throws IOException {
        RegisterRequest registerRequest = new RegisterRequest("test",
                "test", "test@test.com", "testf",
                "testl","f");
        serverProxy.register(registerRequest, dataCache.getHost(), dataCache.getPort());
    }

    @Test
    public void RegisterPass() throws IOException {
        UUID uuid = UUID.randomUUID();
        String user = uuid.toString();
        RegisterRequest registerRequest = new RegisterRequest(user,
                "pass", "test1@test.com", "test1f",
                "test1l","m");
        RegisterResponse registerResponse = serverProxy.register(registerRequest, dataCache.getHost(), dataCache.getPort());
        assertEquals( user, registerResponse.getUsername());
    }

    @Test
    public void RegisterFail() throws IOException {
        RegisterRequest registerRequest = new RegisterRequest("test",
                "test", "test@test.com", "testf",
                "testl","f");
        RegisterResponse registerResponse = serverProxy.register(registerRequest, dataCache.getHost(), dataCache.getPort());
        assertNull( registerResponse.getUsername());
    }

    @Test
    public void LoginPass() throws IOException {
        LoginRequest loginRequest = new LoginRequest("test", "test");
        LoginResponse loginResponse = serverProxy.login(loginRequest, dataCache.getHost(), dataCache.getPort());
        assertEquals( "test",  loginResponse.getUsername());
    }

    @Test
    public void LoginFail() throws IOException {
        LoginRequest loginRequest = new LoginRequest("test1", "test");
        LoginResponse loginResponse = serverProxy.login(loginRequest, dataCache.getHost(), dataCache.getPort());
        assertNull(loginResponse.getUsername());
    }
}
