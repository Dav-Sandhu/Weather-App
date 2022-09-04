package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private String APIKey;
    private String finalURL;
    private String message;
    private TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submit = findViewById(R.id.submit);
        TextView result = findViewById(R.id.result);
        location = findViewById(R.id.location);

        mQueue = Volley.newRequestQueue(this);
        APIKey = "06d576e9f74c8c786506664dfbfad50c";

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!location.getText().toString().equals("")){
                    finalURL = "https://api.openweathermap.org/data/2.5/weather?q=";
                    finalURL += location.getText().toString() + "&appid=" + APIKey;
                    jsonParse(finalURL, result, 1);
                }
            }
        });
    }

    private void jsonParse(String url, TextView t, Integer type){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arr = response.getJSONArray("weather");

                    for (int i = 0; i < arr.length(); i++){
                        JSONObject weatherObj = arr.getJSONObject(i);
                        message = weatherObj.getString("main") + ": " + weatherObj.getString("description");
                    }

                    t.setText(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (type == 1){             //checks for city id
                    finalURL = "https://api.openweathermap.org/data/2.5/weather?id=";
                    finalURL += location.getText().toString() + "&appid=" + APIKey;
                    jsonParse(finalURL, t, 2);
                }else if (type == 2){       //checks for zip code
                    finalURL = "https://api.openweathermap.org/data/2.5/weather?zip=";
                    finalURL += location.getText().toString() + "&appid=" + APIKey;
                    jsonParse(finalURL, t, 3);
                }else if (type == 3){       //checks for geographical coordinates
                    List<String> elementList = Arrays.asList(location.getText().toString().split(","));
                    finalURL = "https://api.openweathermap.org/data/2.5/weather?lat=";

                    if (elementList.size() > 1) {
                        finalURL += elementList.get(0) + "&lon=" + elementList.get(1) + "&appid=" + APIKey;
                        jsonParse(finalURL, t, 4);
                    }else{
                        Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                    }
                }else if (type == 4){       //checks for cities in circle
                    List<String> elementList = Arrays.asList(location.getText().toString().split(","));
                    finalURL = "https://api.openweathermap.org/data/2.5/weather?lat=";

                    if (elementList.size() > 2) {
                        finalURL += elementList.get(0) + "&lon=" + elementList.get(1) + "&cnt=" + elementList.get(2) + "&appid=" + APIKey;
                        jsonParse(finalURL, t, 5);
                    }else{
                        Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                }
            }

        });

        mQueue.add(request);
    }
}