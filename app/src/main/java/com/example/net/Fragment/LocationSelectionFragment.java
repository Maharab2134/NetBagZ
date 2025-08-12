package com.example.net.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.net.Activity.SignupActivity;
import com.example.net.Config.Config;
import com.example.net.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LocationSelectionFragment extends BottomSheetDialogFragment {

    private ListView listView;
    private List<String> dataList;
    private List<Integer> idList;
    private ArrayAdapter<String> adapter;
    private RequestQueue requestQueue;
    private int selectedDistrictId = -1;
    private int selectedUnionId = -1;
    private String selectedLocation = "";
    private Config config;

    @NonNull
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          android.view.ViewGroup container,
                                          Bundle savedInstanceState) {
        android.view.View rootView = inflater.inflate(R.layout.fragment_location_selection, container, false);
        rootView.setBackgroundResource(R.drawable.sheet_bg);

        listView = rootView.findViewById(R.id.listView);
        dataList = new ArrayList<>();
        idList = new ArrayList<>();

        FragmentActivity activity = getActivity();
        if (activity == null) return rootView;

        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(activity);

        config = new Config();
        fetchData("district", -1);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (selectedDistrictId == -1) {
                selectedDistrictId = idList.get(position);
                selectedLocation = dataList.get(position);
                fetchData("union", selectedDistrictId);
            } else if (selectedUnionId == -1) {
                selectedUnionId = idList.get(position);
                selectedLocation += ", " + dataList.get(position);
                fetchData("village", selectedUnionId);
            } else {
                selectedLocation += ", " + dataList.get(position);
                sendResultToActivity(selectedLocation);
                dismiss();
            }
        });

        return rootView;
    }

    private void fetchData(String type, int parentId) {
        String url = Config.Address_Url + "get_location.php?type=" + type;
        if (parentId != -1) {
            url += "&parent_id=" + parentId;
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (!isAdded() || getActivity() == null) return;

                    if (response.length() == 0) {
                        Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    dataList.clear();
                    idList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.optJSONObject(i);
                        if (obj != null) {
                            dataList.add(obj.optString("name"));
                            idList.add(obj.optInt("id"));
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    if (!isAdded() || getActivity() == null) return;

                    Toast.makeText(getActivity(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void sendResultToActivity(String location) {
        if (getActivity() instanceof SignupActivity) {
            ((SignupActivity) getActivity()).addressEditText.setText(location);
        }
    }
}
