package com.example.net.Activity;

import static android.Manifest.permission.CALL_PHONE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.net.R;

public class HelpCenterActivity extends AppCompatActivity {
    private static final int REQUEST_CALL_PERMISSION = 1;
    ImageView toolbarIcon, callCustomer, callWhatsApp, q1, q2, q3, q4, q5;
    TextView welcomeTextView, answerQ1, answerQ2, answerQ3, answerQ4, answerQ5;
    LinearLayout trackMyOrd, resetPassword, myPayment, myProfile, returnMyOrd, canselMyOrd;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_center);

        toolbarIcon = findViewById(R.id.toolbar_icon);
        callCustomer = findViewById(R.id.call);
        callWhatsApp = findViewById(R.id.callWhatsApp);
        q1 = findViewById(R.id.q1);
        q2 = findViewById(R.id.q2);
        q3 = findViewById(R.id.q3);
        q4 = findViewById(R.id.q4);
        q5 = findViewById(R.id.q5);

        answerQ1 = findViewById(R.id.answer_q1);
        answerQ2 = findViewById(R.id.answer_q2);
        answerQ3 = findViewById(R.id.answer_q3);
        answerQ4 = findViewById(R.id.answer_q4);
        answerQ5 = findViewById(R.id.answer_q5);

        trackMyOrd = findViewById(R.id.b1);
        resetPassword = findViewById(R.id.b2);
        myPayment = findViewById(R.id.b3);
        myProfile = findViewById(R.id.b4);
        returnMyOrd = findViewById(R.id.b5);
        canselMyOrd = findViewById(R.id.b6);

        welcomeTextView = findViewById(R.id.user_greeting);

        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null) {
            welcomeTextView.setText("Hi " + userName + ", How can we help?");
        }

        setQuestionClickListener(q1, answerQ1);
        setQuestionClickListener(q2, answerQ2);
        setQuestionClickListener(q3, answerQ3);
        setQuestionClickListener(q4, answerQ4);
        setQuestionClickListener(q5, answerQ5);

        trackMyOrd.setOnClickListener(v -> openActivity(working_now.class));
        resetPassword.setOnClickListener(v -> openActivity(ResetPasswordActivity.class));
        myPayment.setOnClickListener(v -> openActivity(working_now.class));
        myProfile.setOnClickListener(v -> openActivity(ProfileActivity.class));
        returnMyOrd.setOnClickListener(v -> openActivity(working_now.class));
        canselMyOrd.setOnClickListener(v -> openActivity(working_now.class));
        toolbarIcon.setOnClickListener(v -> openActivity(ProfileActivity.class));

        callCustomer.setOnClickListener(v -> makePhoneCall("8800"));
        callWhatsApp.setOnClickListener(v -> openWhatsApp("8801586282609"));
    }

    private void setQuestionClickListener(ImageView question, TextView answer) {
        question.setOnClickListener(v -> {
            if (answer.getVisibility() == View.GONE) {
                answer.setVisibility(View.VISIBLE);
            } else {
                answer.setVisibility(View.GONE);
            }
        });
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(HelpCenterActivity.this, activityClass);
        startActivity(intent);
        finish();
    }

    // Method to make a phone call
    private void makePhoneCall(String callNumber) {
        if (ContextCompat.checkSelfPermission(this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Call permission is required to make this call", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, REQUEST_CALL_PERMISSION);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + callNumber));
        startActivity(intent);
    }

    // Method to open WhatsApp
    private void openWhatsApp(String phoneNumber) {
        try {
            String uri = "https://wa.me/" + phoneNumber;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        openActivity(ProfileActivity.class);
        super.onBackPressed();
    }
}
