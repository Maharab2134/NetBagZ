package com.example.net.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.net.R;

public class DetailsFragment extends Fragment {

    private static final String ARG_DETAILS = "details";
    private TextView detailsText;

    public static DetailsFragment newInstance(String details) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DETAILS, details);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        detailsText = view.findViewById(R.id.details_text);

        if (getArguments() != null) {
            String details = getArguments().getString(ARG_DETAILS);
            detailsText.setText(details);
        }

        return view;
    }

    public void updateDetails(String newDetails) {
        if (detailsText != null) {
            detailsText.setText(newDetails);
        }
    }
}
