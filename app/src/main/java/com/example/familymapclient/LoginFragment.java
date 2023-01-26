package com.example.familymapclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.familymapclient.ServerInteraction.Service.LoginTask;
import com.example.familymapclient.ServerInteraction.Service.RegisterTask;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Request.LoginRequest;
import Request.RegisterRequest;



public class LoginFragment extends Fragment {

    private EditText serverHost;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private Listener listener;
    private String gender;


    private Button registerButton;
    private Button loginButton;

    public LoginFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        serverHost = view.findViewById(R.id.serverHostField);
        serverPort = view.findViewById(R.id.serverPortField);
        username = view.findViewById(R.id.usernameField);
        password = view.findViewById(R.id.passwordField);
        firstName = view.findViewById(R.id.firstNameField);
        lastName = view.findViewById(R.id.lastNameField);
        email = view.findViewById(R.id.emailField);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                activateRegisterButton();
                activateLoginButton();
            }

        };


        serverHost.addTextChangedListener(watcher);
        serverPort.addTextChangedListener(watcher);
        username.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);
        firstName.addTextChangedListener(watcher);
        lastName.addTextChangedListener(watcher);
        email.addTextChangedListener(watcher);

        registerButton = view.findViewById(R.id.registerButton);
        loginButton = view.findViewById(R.id.loginButton);

        loginButton.setEnabled(false);
        registerButton.setEnabled(false);


        Button maleButton = view.findViewById(R.id.genderMaleField);
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "m";
                activateRegisterButton();
                activateLoginButton();
            }
        });

        Button femaleButton = view.findViewById(R.id.genderFemaleField);
        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "f";
                activateRegisterButton();
                activateLoginButton();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();
                String firstNameStr = firstName.getText().toString();
                String lastNameStr = lastName.getText().toString();
                String emailStr = email.getText().toString();
                String genderStr = gender;

                DataCache dataCache = DataCache.getInstance();
                String host = serverHost.getText().toString();
                String port = serverPort.getText().toString();

                dataCache.setHost(host);
                dataCache.setPort(port);

                Handler returnMessageHandler = new Handler(){
                    @SuppressLint("HandlerLeak")
                    public void handleMessage(Message message){
                        Bundle bundle = message.getData();
                        boolean success = bundle.getBoolean("success", false);
                        String firstName = bundle.getString("First Name", "");
                        String lastName = bundle.getString("Last Name", "");

                        if(success == false){
                           listener.postToast("Unsuccessful Register Attempt");
                        }else{
                            listener.postToast("Registration Success for "+firstName+" "+lastName);
                            if (listener != null){
                                listener.notifyDone();
                            }
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(usernameStr, passwordStr,
                        emailStr, firstNameStr, lastNameStr, genderStr);
                RegisterTask registerTask = new RegisterTask(returnMessageHandler ,registerRequest);
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(registerTask);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();

                DataCache dataCache = DataCache.getInstance();
                String host = serverHost.getText().toString();
                String port = serverPort.getText().toString();

                dataCache.setHost(host);
                dataCache.setPort(port);

                Handler returnMessageHandler = new Handler(){
                    @SuppressLint("HandlerLeak")
                    public void handleMessage(Message message){
                        Bundle bundle = message.getData();
                        boolean success = bundle.getBoolean("success", false);
                        String firstName = bundle.getString("First Name", "");
                        String lastName = bundle.getString("Last Name", "");

                        if(success == false){
                            listener.postToast("Unsuccessful Login Attempt");
                        }else{
                            listener.postToast("Login Success "+firstName+" "+lastName);
                            if (listener != null){
                                listener.notifyDone();
                            }
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(passwordStr, usernameStr);
                LoginTask loginTask = new LoginTask(returnMessageHandler ,loginRequest);
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(loginTask);
            }
        });

        return view;
    }


    private void activateRegisterButton(){
        if(TextUtils.isEmpty(serverHost.getText().toString()) || TextUtils.isEmpty(serverPort.getText().toString()) ||
                TextUtils.isEmpty(firstName.getText().toString()) || TextUtils.isEmpty(gender) ||
                TextUtils.isEmpty(lastName.getText().toString()) || TextUtils.isEmpty(username.getText().toString()) ||
                TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(email.getText().toString())){
            registerButton.setEnabled(false);
        }
        else{
            registerButton.setEnabled(true);
        }
    }

    private void activateLoginButton(){
        if(TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) ||
                TextUtils.isEmpty(serverHost.getText().toString()) || TextUtils.isEmpty(serverPort.getText().toString())) {
            loginButton.setEnabled(false);
        }
        else{
            loginButton.setEnabled(true);
        }
    }

    public interface Listener{
        void notifyDone();
        void postToast(String message);
    }

    public void loginListener(Listener listener){
        this.listener = listener;
 }
}