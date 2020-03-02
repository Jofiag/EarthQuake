package com.jofiagtech.earthquake.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jofiagtech.earthquake.R;
import com.jofiagtech.earthquake.model.EarthQuake;
import com.jofiagtech.earthquake.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayAdapter mArrayAdapter;
    private ArrayList<EarthQuake> mQuakeList;
    private ArrayList<String> mPlaceQuakeList;
    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = findViewById(R.id.list_view);
        mQuakeList = new ArrayList<>();
        mPlaceQuakeList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);

        getAllQuakes(Constants.URL);

    }

    private void getAllQuakes(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray feature = response.getJSONArray("features");

                            for (int i = 0; i < Constants.LIMIT; i++){
                                JSONObject properties = feature.getJSONObject(i).getJSONObject("properties");
                                String place = properties.getString("place");
                                mPlaceQuakeList.add(place);
                            }

                            mArrayAdapter = new ArrayAdapter(ListActivity.this, android.R.layout.simple_list_item_1,
                                    android.R.id.text1, mPlaceQuakeList);
                            mListView.setAdapter(mArrayAdapter);
                            mArrayAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }
}
