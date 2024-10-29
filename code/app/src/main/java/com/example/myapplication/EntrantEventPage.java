package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantEventPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantEventPage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EntrantEventPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntrantEventPage.
     */
    // TODO: Rename and change types and number of parameters
    public static EntrantEventPage newInstance(String param1, String param2) {
        EntrantEventPage fragment = new EntrantEventPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view,savedInstanceState);
    BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
    NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
    //bottomNavigationView.setupWithNavController(navController);
    NavigationUI.setupWithNavController(bottomNavigationView, navController);
    if (getArguments() != null) {
        mParam1 = getArguments().getString(ARG_PARAM1);
        mParam2 = getArguments().getString(ARG_PARAM2);
    }
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_event_page, container, false);

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        Button events = view.findViewById(R.id.button_go_to_entrant_event_list);
        events.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventPage_to_entrantEventsList)
        );
        return view;
    }
}