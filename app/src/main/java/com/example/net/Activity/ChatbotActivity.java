package com.example.net.Activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.net.Adapter.ChatAdapter;
import com.example.net.Config.Config;
import com.example.net.Model.ChatMessage;
import com.example.net.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatbotActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;
    private List<ChatMessage> messageList;
    private ChatAdapter adapter;
    private Config config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatbot);

        recyclerView = findViewById(R.id.recyclerViewChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        EditText etMessage = findViewById(R.id.etMessage);
        messageList = new ArrayList<>();
        config = new Config();
        adapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.messageContent), (v, insets) -> {
            v.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        });

        btnSend.setOnClickListener(v -> {
            String userMessage = etMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendMessage(userMessage);
                etMessage.setText("");
            }
        });
    }

    private void sendMessage(String userMessage) {
        messageList.add(new ChatMessage(userMessage, true));
        adapter.notifyDataSetChanged();
        String baseUrl = Config.Base_Url;
        StringRequest request = new StringRequest(Request.Method.POST, baseUrl + "/chatbot.php",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String answer = jsonObject.getString("answer");
                        messageList.add(new ChatMessage(answer, false));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("question", userMessage);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}