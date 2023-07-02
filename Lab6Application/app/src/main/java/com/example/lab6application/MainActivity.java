package com.example.lab6application;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CatImages req = new CatImages();
        req.execute("https://cataas.com/cat?json=true");
    }

    private class CatImages extends AsyncTask<String, Integer, Bitmap> {

        ImageView img = findViewById(R.id.imageView);
        ProgressBar prg = findViewById(R.id.progressBar);
        Bitmap bitmap;
        ArrayList<String> allIds = new ArrayList<>();
        public Bitmap doInBackground(String ... args) {

            while(true) {
                try {
                    URL url = new URL(args[0]);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream response = urlConnection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    String result = sb.toString();

                    JSONObject catPic = new JSONObject(result);

                    String catId = catPic.getString("_id");
                    String catURLString = catPic.getString("url");
                    URL catURL = new URL("https://cataas.com"+catURLString);
                    File catFile = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), catId);
                    if (allIds.contains(catId)) {

                        bitmap = BitmapFactory.decodeFile(catFile.getPath());
                    } else {

                        bitmap = BitmapFactory.decodeStream(catURL.openConnection().getInputStream());
                        FileOutputStream outStream = new FileOutputStream(catFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        allIds.add(catId);
                    }

                    for (int i = 0; i < 100; i++) {
                        try {
                            publishProgress(i);
                            Thread.sleep(20);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        public void onProgressUpdate(Integer ... args) {
            onPostExecute(bitmap);
            prg.setProgress(args[0]);
        }
        public void onPostExecute(Bitmap returned) {
            img.setImageBitmap(returned);
        }
    }
}