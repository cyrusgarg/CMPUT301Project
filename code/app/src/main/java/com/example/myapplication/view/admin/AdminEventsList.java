package com.example.myapplication.view.admin;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.controller.EventsListAdapter;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.Facility;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminEventsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminEventsList extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventDataList;
    private ArrayList<User> userDataList;
    private ArrayList<String> userIds;
    private ArrayList<Facility> facilityDataList;
    private ArrayList<String> facilityOrganizerIds;
    private EventsListAdapter eventArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference facilitiesRef;
    List<DocumentSnapshot> events;


    public AdminEventsList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdminEventsList.
     */
    public static AdminEventsList newInstance() {
        AdminEventsList fragment = new AdminEventsList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes the fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Inflates the view for the fragment, sets up the ListView and buttons.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle with saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_events_list, container, false);

        db = FirebaseFirestore.getInstance();

        usersRef = db.collection("users");
        eventsRef  = db.collection("events");
        facilitiesRef = db.collection("facilities");

        fetchEvents();
        fetchUsers();
        fetchFacilities();

        //initialize event data list and array adapter
        eventList = view.findViewById(R.id.list_view_admin_events_list);

        eventDataList = new ArrayList<Event>();
        userDataList = new ArrayList<User>();
        facilityDataList = new ArrayList<Facility>();
        userIds = new ArrayList<String>();
        facilityOrganizerIds = new ArrayList<String>();

        eventArrayAdapter = new EventsListAdapter(this.getContext(), eventDataList,true);
        eventList.setAdapter(eventArrayAdapter);

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Event clicked = eventDataList.get(i);

                // Show a dialog to choose what to delete
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Options")
                        .setMessage("What would you like to delete?")
                        .setPositiveButton("Delete Entire Event", (dialog, which) -> deleteEvent(clicked))
                        .setNeutralButton("Delete Image Only", (dialog, which) -> deleteEventImage(clicked))
                        .setNegativeButton("Delete QR Code Only", (dialog, which) -> deleteEventQRCode(clicked))
                        .show();
            }
        });

        Button home = view.findViewById(R.id.button_go_to_home_from_admin_events_list);
        home.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminEventsList_to_home));

        ImageButton users = view.findViewById(R.id.users);
        users.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminEventsList_to_adminUsersList));

        ImageButton facilities = view.findViewById(R.id.facilities);
        facilities.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminEventsList_to_adminFacilitiesList));

        ImageButton pictures = view.findViewById(R.id.images);
        pictures.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminEventsList_to_adminPicturesList));

        return view;
    }

    /**
     * This method loads event data from Firestore
     */
    private void fetchEvents(){
        Task<QuerySnapshot> task = eventsRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                events = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for(int i = 0; i < events.size(); i++){
                    String eventName = events.get(i).getString("eventName");
                    int capacity = events.get(i).getDouble("capacity").intValue();


                    // I set the event description as the doc ID to make it easier to pass when clicked
                    eventDataList.add(new Event(events.get(i).getId(),eventName, events.get(i).getString("eventDescription"), ((Timestamp)
                            events.get(i).get("eventStart")).toDate(), ((Timestamp) events.get(i).get("eventEnd")).toDate(),
                            ((Timestamp) events.get(i).get("registrationStart")).toDate(), ((Timestamp) events.get(i).get("registrationEnd")).toDate(),
                            events.get(i).getString("location"),capacity, ((Number)events.get(i).get("price")).intValue(),
                            events.get(i).getString("posterUrl"), events.get(i).getString("qrCodeHash"), events.get(i).getString("organizerId"),
                            (List<String>) events.get(i).get("acceptedEntrants"), (List<String>) events.get(i).get("selectedEntrants"),
                            (List<String>) events.get(i).get("declinedEntrants"), (List<String>) events.get(i).get("waitlist"), Boolean.TRUE.equals(events.get(i).getBoolean("geolocationRequired"))));
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This method loads user data from Firestore
     */
    private void fetchUsers(){
        Task<QuerySnapshot> task = usersRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> users = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for (int i = 0; i < users.size(); i++) {
                    DocumentSnapshot current = users.get(i);
                    User user = new User(current.getId(), loadedUser -> {
                        if (loadedUser != null) {
                            userDataList.add(loadedUser);
                            userIds.add(loadedUser.getDeviceID());
                            Log.d("AdminEventsList", "Loaded User: " + loadedUser.getName());
                        }
                    });
                }
            }
        });
    }

    /**
     * This method loads facility data from Firestore
     */
    private void fetchFacilities(){
        Task<QuerySnapshot> task = facilitiesRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> facilities = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for(int i = 0; i < facilities.size(); i++){
                    Facility facility = new Facility(facilities.get(i).getId(),facilities.get(i).getString("facilityName"),facilities.get(i).getString("facilityAddress"),
                            facilities.get(i).getString("facilityEmail"),facilities.get(i).getString("facilityPhoneNumber"),
                            facilities.get(i).getString("organizerId"),facilities.get(i).getString("profileImageUrl"));
                    if(facilities.get(i).get("events") == null){
                        facility.setEvents(new ArrayList<String>());
                    } else {
                        facility.setEvents((List<String>) facilities.get(i).get("events"));
                    }


                    facilityDataList.add(facility);
                    facilityOrganizerIds.add(facility.getOrganizerId());
                }
            }
        });
    }

    /**
     * @param event
     *
     * This method takes an event and goes through the lists of accepted, cancelled, selected, and accepted
     * users and goes into the user profile to remove the event being deleted, then it goes into the host
     * facility and deletes the event, then finally, the event is deleted from firestore
     */
    private void deleteEvent(Event event){
        List<String> accepted = event.getAcceptedEntrants();
        List<String> cancelled = event.getCancelledEntrants();
        List<String> selected = event.getSelectedEntrants();
        List<String> waitlist = event.getWaitlist();

        if(!accepted.isEmpty()){
            for (int j = 0; j < accepted.size(); j++) {
                // finds the user by searching the userIds list, then removes the event from their accepted list
                User user = userDataList.get(userIds.indexOf(accepted.get(j)));
                user.removeAcceptedEvent(event.getEventId());            }
        }
        if(!cancelled.isEmpty()){
            for (int j = 0; j < cancelled.size(); j++) {
                // finds the user by searching the userIds list, then removes the event from their accepted list
                User user = userDataList.get(userIds.indexOf(cancelled.get(j)));
                user.removeCancelledEvent(event.getEventId());
            }
        }
        if(!selected.isEmpty()){
            for (int j = 0; j < selected.size(); j++) {
                Log.d("Kenny", "requested users detected");
                // finds the user by searching the userIds list, then removes the event from their accepted list
                User user = userDataList.get(userIds.indexOf(selected.get(j)));
                user.removeSelectedEvent(event.getEventId());            }
        }
        if(!waitlist.isEmpty()){
            for (int j = 0; j < waitlist.size(); j++) {
                // finds the user by searching the userIds list, then removes the event from their accepted list
                User user = userDataList.get(userIds.indexOf(waitlist.get(j)));
                user.removeRequestedEvent(event.getEventId());            }
        }


        if(!(event.getOrganizerId().isEmpty())) {
            Facility eventLocation = facilityDataList.get(facilityOrganizerIds.indexOf(event.getOrganizerId()));
            List<String> eventsAtLocation = eventLocation.getEvents();
            Log.d("Kenny", eventsAtLocation.toString());
            eventsAtLocation.remove(event.getEventId());
            eventLocation.setEvents(eventsAtLocation);
            Log.d("Kenny", eventLocation.getEvents().toString());
            eventLocation.updateFirestore();
        }


        eventsRef.document(event.getEventId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        eventDataList.remove(event);
                        eventArrayAdapter.notifyDataSetChanged();
                        Log.d("AdminEventsList", "Event successfully Deleted");
                    }
                });
    }

    /**
     * @param event
     *
     * This method sets the event QR code to be null
     */
    private void deleteEventQRCode(Event event) {
        // Remove the QR code hash from Firestore
        eventsRef.document(event.getEventId())
                .update("qrCode", null)
                .addOnSuccessListener(unused -> {
                    Log.d("AdminEventsList", "QR Code successfully deleted.");
                    // Update local data and refresh UI if needed
                    event.setQrCodeHash(null);
                    eventArrayAdapter.notifyDataSetChanged();
                });
    }

    /**
     * @param event
     *
     * This method sets the event poster URL to be null
     */
    private void deleteEventImage(Event event) {
        // Remove the image URL from Firestore
        eventsRef.document(event.getEventId())
                .update("posterUrl", null)
                .addOnSuccessListener(unused -> {
                    Log.d("AdminEventsList", "Image successfully deleted.");
                    // Update local data and refresh UI if needed
                    event.setPosterUrl(null);
                    eventArrayAdapter.notifyDataSetChanged();
                });
    }

}
