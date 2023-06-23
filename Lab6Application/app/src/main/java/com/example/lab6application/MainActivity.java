package com.example.lab6application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private ProgressBar progressBar;
    private boolean isRunning = true; // Add the boolean flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        new CatImages().execute();
    }

    // ...

    private class CatImages extends AsyncTask<String, Integer, String> {
        private Bitmap currentCatImage;

        @Override
        protected String doInBackground(String... params) {
            while (isRunning && !isCancelled()) {
                try {
                    URL url = new URL("https://cataas.com/cat?json=true");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        StringBuilder stringBuilder = new StringBuilder();
                        int data;
                        while ((data = inputStream.read()) != -1) {
                            stringBuilder.append((char) data);
                        }
                        String response = stringBuilder.toString();
                        connection.disconnect();

                        String imageUrl = parseImageUrlFromJson(response);
                        if (imageUrl != null) {
                            currentCatImage = getBitmapFromUrl(imageUrl);
                            publishProgress(0);
                            Thread.sleep(3000);
                        }
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private String parseImageUrlFromJson(String json) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                return "https://cataas.com" + jsonObject.getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap getBitmapFromUrl(String imageUrl) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (currentCatImage != null) {
                progressBar.setProgress(values[0]);
                imageView.setImageBitmap(currentCatImage);
            }
        }

        @Override
        protected void onCancelled() { // Add this method to set isRunning flag to false when task is cancelled
            super.onCancelled();
            isRunning = false;
        }
    }
}
