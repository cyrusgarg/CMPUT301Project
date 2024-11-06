package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.DeviceUtils;
import com.example.myapplication.R;
import com.example.myapplication.model.Facility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgAddFacility#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgAddFacility extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText editFacilityName;
    private EditText editFacilityAddress;
    private EditText editFacilityEmail;
    private EditText editFacilityPhoneNumber;
    private Button createFacilityButton;
    private String organizerId;

    public OrgAddFacility() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrgAddFacility.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgAddFacility newInstance(String param1, String param2) {
        OrgAddFacility fragment = new OrgAddFacility();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        organizerId = DeviceUtils.getDeviceId(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_add_facility, container, false);

        editFacilityName = view.findViewById(R.id.editTextFacilityName);
        editFacilityAddress = view.findViewById(R.id.editTextFacilityAddress);
        editFacilityEmail = view.findViewById(R.id.editTextFacilityEmail);
        editFacilityPhoneNumber = view.findViewById(R.id.editTextFacilityPhone);
        createFacilityButton = view.findViewById(R.id.buttonCreateFacility);

        createFacilityButton.setOnClickListener(v -> {
            String facilityName = editFacilityName.getText().toString().trim();
            String facilityAddress = editFacilityAddress.getText().toString().trim();
            String facilityEmail = editFacilityEmail.getText().toString().trim();
            String facilityPhoneNumber = editFacilityPhoneNumber.getText().toString().trim();

            // Validate inputs
            if (facilityName.isEmpty() || facilityAddress.isEmpty() || facilityEmail.isEmpty() || facilityPhoneNumber.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!facilityPhoneNumber.matches("\\d{7,15}")){
                Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            Facility facility = new Facility(facilityName, facilityAddress, facilityEmail, facilityPhoneNumber, organizerId);
            facility.saveToFirestore();
            Navigation.findNavController(v).navigate(R.id.action_orgAddFacility_to_orgFacilityList);
        });

        Button buttonGoToFacilityList = view.findViewById(R.id.button_go_to_facility_list_from_add_facility);
        buttonGoToFacilityList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_orgAddFacility_to_orgFacilityList));

        return view;
    }
}