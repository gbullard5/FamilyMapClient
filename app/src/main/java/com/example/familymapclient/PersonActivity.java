package com.example.familymapclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import models.Event;
import models.Person;

public class PersonActivity extends AppCompatActivity {


    private DataCache dataCache = DataCache.getInstance();
    private Person person = dataCache.getPerson();
    private TextView firstName;
    private TextView lastName;
    private TextView gender;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Family Map: Person Details");


        firstName = findViewById(R.id.personFirstName);
        firstName.setText(person.getFirstName());

        lastName = findViewById(R.id.personLastName);
        lastName.setText(person.getLastName());

        gender = findViewById(R.id.personGender);
        String genderSet = person.getGender();
        if(genderSet.toLowerCase(Locale.ROOT).equals("m")){
            gender.setText("Male");
        }else{
            gender.setText("Female");
        }



        ExpandableListView expandableListView = findViewById(R.id.expListPerson);

        String personID = person.getPersonID();

        ArrayList<Event> eventList = dataCache.events;
        ArrayList<Event> personEventList = new ArrayList<>();
        for (Event e : eventList) {
            if (e.getPersonID().equals(personID)) {
                personEventList.add(e);
            }
        }
        Comparator<Event> eventComparator = Comparator.comparingInt(Event::getYear);
        personEventList.sort(eventComparator);

        ArrayList<Person> peopleList = dataCache.getPeople();
        ArrayList<Person> peopleRelation = new ArrayList<>();
        for (Person p : peopleList) {
            if (p.getPersonID().equals(person.getSpouseID())
                    || p.getPersonID().equals(person.getMotherID())
                    || p.getPersonID().equals(person.getFatherID())) {
                peopleRelation.add(p);
            }
            if (person.getPersonID().equals(p.getFatherID())
                    || person.getPersonID().equals(p.getMotherID())) {
                peopleRelation.add(p);
            }
        }
        expandableListView.setAdapter(new ExpandableListAdapter(personEventList, peopleRelation));
    }



    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int PERSON_GROUP_POSITION = 0;
        private static final int EVENT_GROUP_POSITION = 1;

        private final List<Person> relatives;
        private final List<Event> events;

        ExpandableListAdapter(ArrayList<Event> events, ArrayList<Person> relatives) {
            this.events = events;
            this.relatives = relatives;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int i) {
            switch (i) {
                case PERSON_GROUP_POSITION:
                    return relatives.size();
                case EVENT_GROUP_POSITION:
                    return events.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + i);
            }
        }

        @Override
        public Object getGroup(int i) {
            //not used
            return null;
        }

        @Override
        public Object getChild(int i, int i1) {
            //not used
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    titleView.setText(R.string.relativesTitle);
                    break;
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.eventTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }


        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.family_item, parent, false);
                    initializeRelativeView(itemView, childPosition);
                    break;
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }


        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        @SuppressLint("SetTextI18n")
        private void initializeRelativeView(View view, final int childPosition) {
            TextView personView = view.findViewById(R.id.family);
            personView.setText(relatives.get(childPosition).getFirstName()
                    + " " + relatives.get(childPosition).getLastName());
            String relation;

            ImageView image = view.findViewById(R.id.iconPerson);
            if (relatives.get(childPosition).getGender().toUpperCase(Locale.ROOT).equals("M")) {
                Drawable genderIcon = new IconDrawable(getBaseContext(),
                        FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(40);
                image.setImageDrawable(genderIcon);
            } else if (relatives.get(childPosition).getGender().toUpperCase(Locale.ROOT).equals("F")) {
                Drawable genderIcon = new IconDrawable(getBaseContext(),
                        FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(40);
                image.setImageDrawable(genderIcon);
            }

            if (relatives.get(childPosition).getPersonID().equals(person.getSpouseID())) {
                relation = "Spouse";
            } else if (relatives.get(childPosition).getPersonID().equals(person.getMotherID())) {
                relation = "Mother";
            } else if (relatives.get(childPosition).getPersonID().equals(person.getFatherID())) {
                relation = "Father";
            } else {
                relation = "Child";
            }
            TextView relationView = view.findViewById(R.id.familyRelation);
            relationView.setText(relation);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    dataCache.setPerson(relatives.get(childPosition));
                    startActivity(intent);
                }
            });
        }


        @SuppressLint("SetTextI18n")
        private void initializeEventView(View view, final int childPosition) {
            TextView eventView = view.findViewById(R.id.eventName);
            eventView.setText(events.get(childPosition).getEventType().toLowerCase(Locale.ROOT) + ": " +
                    events.get(childPosition).getCity() + ", " + events.get(childPosition).getCountry() +
                    " (" + events.get(childPosition).getYear() + ")");

            @SuppressLint("CutPasteId") ImageView image = view.findViewById(R.id.iconEvent);
            Drawable mapIcon = new IconDrawable(getBaseContext(),
                    FontAwesomeIcons.fa_map_marker).colorRes(R.color.map_icon).sizeDp(40);
            image.setImageDrawable(mapIcon);

            TextView eventPerson = view.findViewById(R.id.eventPerson);
            eventPerson.setText(person.getFirstName() + " "+ person.getLastName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);

                    dataCache.setEventID(events.get(childPosition).getEventID());
                    finish();
                    startActivity(intent);
                }
            });

        }
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}













