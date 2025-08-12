/**
 * Copyright (c) 2024 Md. Maharab Hosen. All rights reserved.
 * Author: Md. Maharab Hosen
 */
package com.example.admin.Adapter;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.admin.Config.Config;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
public class UpdatePaymentStatusTask extends AsyncTask<String, Void, Boolean> {
    private Context context;
    private Config config;

    public UpdatePaymentStatusTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        config = new Config();
        String baseUrl = Config.Base_Url;
        String paymentId = params[0];
        String newStatus = params[1];

        HttpURLConnection connection = null;
        try {
            URL url = new URL(baseUrl + "/update_payment_status.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            connection.setDoOutput(true);

            String postData = "payment_id=" + paymentId + "&status=" + newStatus;

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(postData);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "Payment status updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to update payment status", Toast.LENGTH_SHORT).show();
        }
    }
}
