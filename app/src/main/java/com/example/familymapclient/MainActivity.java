package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.joanzapata.iconify.Iconify;

import com.joanzapata.iconify.fonts.FontAwesomeModule;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        if(fragment == null){
            fragment = createLoginFragment();
            fragmentManager.beginTransaction().add(R.id.main, fragment).commit();

        }else{
            if(fragment instanceof LoginFragment){
                ((LoginFragment) fragment).loginListener((LoginFragment.Listener) this);
            }
          }


    }
    private Fragment createLoginFragment(){
        LoginFragment fragment = new LoginFragment();
        fragment.loginListener((LoginFragment.Listener) this);
        return fragment;
    }



    public void notifyDone(){
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        fragmentManager.beginTransaction().replace(R.id.main, fragment).commit();
    }

    public void postToast(String message){
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}