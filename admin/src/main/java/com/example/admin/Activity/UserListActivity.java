/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.admin.Adapter.UserAdapter;
import com.example.admin.Config.Config;
import com.example.admin.MainActivity;
import com.example.admin.Model.User;
import com.example.admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListActivity extends AppCompatActivity implements UserAdapter.OnStatusUpdateListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private List<User> filteredUserList;
    private static final String FETCH_URL = Config.Base_Url + "/fetch_users.php";
    private static final String UPDATE_URL = Config.Base_Url + "/update_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>();

        userAdapter = new UserAdapter((Context) this, filteredUserList, this);
        recyclerView.setAdapter(userAdapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search Users...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });

        fetchUserData();
    }

    /**
     * Fetch user data from the server and populate the list.
     */
    private void fetchUserData() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, FETCH_URL, null,
                response -> {
                    userList.clear();
                    filteredUserList.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject userObject = response.getJSONObject(i);
                            String id = userObject.getString("id");
                            String name = userObject.getString("name");
                            String phone = userObject.getString("phone");
                            String shopname = userObject.getString("shopname");
                            String address = userObject.getString("address");
                            String status = userObject.getString("status");
                            String postalCode = userObject.getString("postalCode");

                            User user = new User(id, name, phone, shopname, address, status, postalCode);
                            userList.add(user);
                        }
                        filteredUserList.addAll(userList);
                        userAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("UserListActivity", "Error fetching data: " + error.getMessage()));

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    /**
     * Update user status on the server.
     *
     * @param userId    The user ID.
     * @param newStatus The new status to set.
     */
    public void updateUserStatus(String userId, String newStatus) {
        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("success")) {
                            Toast.makeText(this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                            fetchUserData();
                        } else {
                            Toast.makeText(this, jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userId);
                params.put("status", newStatus);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * Filter users based on the search query.
     *
     * @param query The search query.
     */
    private void filterUsers(String query) {
        filteredUserList.clear();

        if (query.isEmpty()) {
            filteredUserList.addAll(userList);
        } else {
            for (User user : userList) {
                if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                        user.getShopname().toLowerCase().contains(query.toLowerCase())) {
                    filteredUserList.add(user);
                }
            }
        }

        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
