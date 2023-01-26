package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import models.Event;
import models.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap map;
    DataCache dataCache = DataCache.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View personButton = view.findViewById(R.id.person_button);
        personButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataCache.getPerson() != null) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        if(dataCache.getEventID() == null) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu, menu);
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        ImageView defaultImage = getView().findViewById(R.id.iconMap);
        Drawable mapIcon = new IconDrawable(getActivity(),
                FontAwesomeIcons.fa_map_marker).colorRes(R.color.map_icon).sizeDp(40);
        defaultImage.setImageDrawable(mapIcon);

        Map<String, Float> colorMap = assignColors(dataCache.getEventsMap());
        ArrayList<Event> events = dataCache.getEvents();
        Marker marker;
        Map<String, Event> eventsByID = dataCache.getEventMapByID();
        Map<String, Marker> markerByID = dataCache.getMarkerByID();
        for (int i =0; i<events.size(); i++){
            eventsByID.put(events.get(i).getEventID(), events.get(i));
        }
        dataCache.setEventMapByID(eventsByID);
        for(Event e : events){
            LatLng location = new LatLng(e.getLatitude(), e.getLongitude());
            Float color = colorMap.get(e.getEventType().toUpperCase(Locale.ROOT));
            marker= map.addMarker(new MarkerOptions().position(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));

            marker.setTag(e.getEventID());
            markerByID.put(e.getEventID(), marker);
        }
        dataCache.setMarkerByID(markerByID);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Event event = eventsByID.get(marker.getTag());
                TextView text1 = getView().findViewById(R.id.person);
                String personId = event.getPersonID();
                Map<String, Person> personIDMap = dataCache.getPeopleMap();
                Person person = personIDMap.get(personId);
                dataCache.setPerson(person);

                text1.setText(person.getFirstName()+ " "+ person.getLastName());
                TextView text2 = getView().findViewById(R.id.event);
                text2.setText(event.getEventType()+ ": "+ event.getCity()+
                        ", " + event.getCountry()+ " ("+ event.getYear()+ ")");
                ImageView image = getView().findViewById(R.id.iconMap);
                if(person.getGender().toUpperCase(Locale.ROOT).equals("M")){
                    Drawable genderIcon = new IconDrawable(getActivity(),
                            FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(40);
                    image.setImageDrawable(genderIcon);
                }else if(person.getGender().toUpperCase(Locale.ROOT).equals("F")){
                    Drawable genderIcon = new IconDrawable(getActivity(),
                            FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(40);
                    image.setImageDrawable(genderIcon);
                }
                map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                return true;
            }
        });
        if(dataCache.getEventID() != null) {
            Event event = eventsByID.get(dataCache.getEventID());
            TextView text1 = getView().findViewById(R.id.person);
            String personId = event.getPersonID();
            Map<String, Person> personIDMap = dataCache.getPeopleMap();
            Person person = personIDMap.get(personId);
            dataCache.setPerson(person);

            text1.setText(person.getFirstName()+ " "+ person.getLastName());
            TextView text2 = getView().findViewById(R.id.event);
            text2.setText(event.getEventType()+ ": "+ event.getCity()+
                    ", " + event.getCountry()+ " ("+ event.getYear()+ ")");

            ImageView image = getView().findViewById(R.id.iconMap);
            if(person.getGender().toLowerCase(Locale.ROOT) == "m"){
                Drawable genderIcon = new IconDrawable(getActivity(),
                        FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(40);
                image.setImageDrawable(genderIcon);
            }else if(person.getGender().toLowerCase(Locale.ROOT) == "m"){
                Drawable genderIcon = new IconDrawable(getActivity(),
                        FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(40);
                image.setImageDrawable(genderIcon);
            }

            animateMarker(dataCache.getEventID());
        }

    }



    @Override
    public void onMapLoaded() {

    }



    public void animateMarker(String eventID){
        Map<String, Event> eventMapByID =dataCache.getEventMapByID();
        Event event = eventMapByID.get(eventID);
        Map<String, Marker> markerMap= dataCache.getMarkerByID();
        Marker marker = markerMap.get(event.getEventID());
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }


    public Map<String, Float> assignColors(Map<String, Event> eventMap){
        Map<String, Float> colorMap = new HashMap<>();
        int count = 1;
        for (String e : eventMap.keySet()) {
            if (count == 1){
                colorMap.put(e, BitmapDescriptorFactory.HUE_AZURE);
                count++;
            }
            else if (count == 2){
                colorMap.put(e, BitmapDescriptorFactory.HUE_BLUE);
                count++;
            }
            else if (count == 3){
                colorMap.put(e, BitmapDescriptorFactory.HUE_CYAN);
                count++;
            }
            else if (count == 4){
                colorMap.put(e, BitmapDescriptorFactory.HUE_GREEN);
                count++;
            }
            else if (count == 5){
                colorMap.put(e, BitmapDescriptorFactory.HUE_MAGENTA);
                count++;
            }
            else if (count == 6){
                colorMap.put(e, BitmapDescriptorFactory.HUE_ORANGE);
                count++;
            }
            else if (count == 7){
                colorMap.put(e, BitmapDescriptorFactory.HUE_RED);
                count++;
            }
            else if (count == 8){
                colorMap.put(e, BitmapDescriptorFactory.HUE_ROSE);
                count++;
            }
            else if (count == 9){
                colorMap.put(e, BitmapDescriptorFactory.HUE_VIOLET);
                count++;
            }
            else if (count == 10){
                colorMap.put(e, BitmapDescriptorFactory.HUE_YELLOW);
                count = 1;
            }

        }
        return colorMap;
    }

}