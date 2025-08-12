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

public class DescriptionFragment extends Fragment {

    private static final String ARG_DESCRIPTION = "description";
    private TextView descriptionText;

    public static DescriptionFragment newInstance(String description) {
        DescriptionFragment fragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        descriptionText = view.findViewById(R.id.description_text);

        if (getArguments() != null) {
            String description = getArguments().getString(ARG_DESCRIPTION);
            descriptionText.setText(description);
        }

        return view;
    }

    public void updateDescription(String newDescription) {
        if (descriptionText != null) {
            descriptionText.setText(newDescription);
        }
    }
}
