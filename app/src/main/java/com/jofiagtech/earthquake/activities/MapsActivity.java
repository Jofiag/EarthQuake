package com.jofiagtech.earthquake.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jofiagtech.earthquake.R;
import com.jofiagtech.earthquake.model.EarthQuake;
import com.jofiagtech.earthquake.ui.CustomInfoWindow;
import com.jofiagtech.earthquake.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private RequestQueue mQueue;

    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;

    private BitmapDescriptor[] iconColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button showListButton = findViewById(R.id.show_list_btn);
        showListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        iconColors = new BitmapDescriptor[]{
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
          //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
        };

        mQueue = Volley.newRequestQueue(this);

        getEarthQuakes();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            getAndShowPhoneLocation();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (!marker.getTitle().equals("My Location"))
            getQuakeDetails(marker.getTag().toString());

        //Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void getEarthQuakes() {
        final EarthQuake earthQuake = new EarthQuake();
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, Constants.URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray feature = response.getJSONArray("features");
                            for (int i = 0; i < Constants.LIMIT; i++) {
                                //Get property
                                JSONObject properties = feature.getJSONObject(i).getJSONObject("properties");

                                //Get geometry
                                JSONObject geometry = feature.getJSONObject(i).getJSONObject("geometry");

                                //Get Coordinates
                                JSONArray coordinates = geometry.getJSONArray("coordinates");

                                double lat = coordinates.getDouble(0);
                                double lon = coordinates.getDouble(1);
                                LatLng latLng = new LatLng(lat, lon);

                                //Log.d("JSON", "Coordinates : " + latLng.latitude + ", " + latLng.longitude);

                                //Setting up the earthQuake
                                DateFormat dateFormat = DateFormat.getDateInstance();
                                String time = dateFormat.format(new Date(properties.getLong("time")).getTime());
                                earthQuake.setTime(time);
                                earthQuake.setPlace(properties.getString("place"));
                                earthQuake.setDetailsLink(properties.getString("detail"));
                                earthQuake.setMagnitude(properties.getDouble("mag"));
                                earthQuake.setLatitude(lat);
                                earthQuake.setLongitude(lon);
                                earthQuake.setType(properties.getString("type"));

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(earthQuake.getPlace())
                                        .icon(iconColors[Constants.randomInt(iconColors.length, 0)])
                                        .snippet("Magnitude : " + earthQuake.getMagnitude() + "\n"
                                                + "Date : " + earthQuake.getTime()))
                                        .setTag(earthQuake.getDetailsLink());

                                //Add circle to marker that have magnitude > x
                                if (earthQuake.getMagnitude() >= 2.0){
                                    CircleOptions circleOptions = new CircleOptions();
                                    circleOptions.center(new LatLng(earthQuake.getLatitude(), earthQuake.getLongitude()));
                                    circleOptions.radius(30000);
                                    circleOptions.strokeWidth(2.5f);
                                    circleOptions.fillColor(Color.RED);
                                    /*mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));*/

                                    mMap.addCircle(circleOptions);
                                }

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 1));
                            }
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

        mQueue.add(jsonObject);
    }

    private void getQuakeDetails(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String detailsUrl = "";
                        try {
                            JSONObject properties = response.getJSONObject("properties");
                            JSONObject products = properties.getJSONObject("products");
                            JSONArray geoserve = products.getJSONArray("geoserve");

                            for (int i = 0; i < geoserve.length(); i++){
                                JSONObject geoserveObj = geoserve.getJSONObject(i);
                                JSONObject contents = geoserveObj.getJSONObject("contents");
                                JSONObject geoserveJson = contents.getJSONObject("geoserve.json");

                                detailsUrl = geoserveJson.getString("url");
                            }
                            Log.d("JSON", "onResponse: " + detailsUrl);
                            getMoreDetails(detailsUrl);

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

        mQueue.add(jsonObjectRequest);
    }

    private void getAndShowPhoneLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, mLocationListener);

        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
            //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
        }
        else
            Toast.makeText(getApplicationContext(), "Finding location failed !", Toast.LENGTH_LONG);

    }

    private void getMoreDetails(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mBuilder = new AlertDialog.Builder(MapsActivity.this);
                        View view = getLayoutInflater().inflate(R.layout.popup, null);

                        Button dismissPopupTop = view.findViewById(R.id.dismiss_popup_top);
                        Button dismissPopup = view.findViewById(R.id.dismiss_popup);
                        TextView listOfCities = view.findViewById(R.id.list_of_cities);
                        WebView htmlPopup = view.findViewById(R.id.html_web_view);

                        StringBuilder stringBuilder =  new StringBuilder();

                        try {

                            if (response.has("tectonicSummary") && response.getString("tectonicSummary") != null){
                                JSONObject tectonicSummary = response.getJSONObject("tectonicSummary");

                                if (tectonicSummary.has("text") && tectonicSummary.getString("text") != null){
                                    String text = tectonicSummary.getString("text");
                                    htmlPopup.loadDataWithBaseURL(null, text, "text/html", "UTF_8", null);
                                }
                            }

                            JSONArray cities = response.getJSONArray("cities");

                            for (int i = 0; i < cities.length(); i++){
                                JSONObject city = cities.getJSONObject(i);

                                stringBuilder.append("City : " + city.getString("name") + "\n"
                                                    +"Distance : " + city.getString("distance") + "\n"
                                                    +"Population : " + city.getString("population") + "\n");

                                stringBuilder.append("\n\n");

                                listOfCities.setText(stringBuilder);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dismissPopupTop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });

                        dismissPopup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });

                        mBuilder.setView(view);
                        mDialog = mBuilder.create();
                        mDialog.show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        mQueue.add(jsonObjectRequest);
    }
}