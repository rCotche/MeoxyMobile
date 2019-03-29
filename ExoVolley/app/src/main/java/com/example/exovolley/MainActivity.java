package com.example.exovolley;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String JSON_URL = "https://10.0.2.2:44303/api/category";
    private static final String PREFERENCES = "Prefs";
    private static final String KEY_THEME = "keyTheme";

    private URL url;

    private List<Category> categories;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;


    private SharedPreferences sharedPreferences;

    private SensorManager sensorManager;
    private Sensor sensor;

    private float lux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPrefs();
        if (sharedPreferences.getBoolean(KEY_THEME, false)) {
            setTheme(R.style.NightTheme);
        }

        setContentView(R.layout.activity_main);

        init();
        setAdapterManager();
        checkPermission();
        checkHttps();
        dontCheckCert();
        sendAndRequestResponse();
    }
    private void initPrefs() {
        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    private void init(){
        recyclerView = findViewById(R.id.recyJson);
        categories = new ArrayList<>();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        try {
            url = new URL(JSON_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setAdapterManager() {

        adapter = new CategoryAdapter(categories, this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        rvClickListener();
    }

    private void rvClickListener(){
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(MainActivity.this, CategoryChoosed.class);
                TextView title = view.findViewById(R.id.txtTitre);
                TextView id = view.findViewById(R.id.txtId);

                i.putExtra("TITLE", title.getText().toString());
                i.putExtra("STRING_I_NEED", id.getText().toString());
                startActivity(i);
            }
        }));
    }
    private void checkPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    private void checkHttps(){
        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            HttpsURLConnection https = null;
            try {
                https = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (https != null) {
                https.setHostnameVerifier(DO_NOT_VERIFY);
            }
        }
    }

    private void dontCheckCert(){
        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            if (context != null) {
                context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        if (context != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        }
    }

    private void sendAndRequestResponse() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
            (Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response != null) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                //récupère la réponse et le transforme en objet
                                JSONObject json_data = response.getJSONObject(i);
                                int id = json_data.getInt("id");
                                String name = json_data.getString("name");
                                String description = json_data.getString("description");
                                String picture = json_data.getString("picture");
                                //crée un objet catégorie
                                Category uneCategory = new Category();
                                uneCategory.setId(id);
                                uneCategory.setName(name);
                                uneCategory.setDescription(description);
                                uneCategory.setPicture(picture);

                                //ajout d'une catégorie a la liste des catégories
                                categories.add(uneCategory);
                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Log.i("PersoRequest", "Error :" + error.toString());
                }
            });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @SuppressLint("BadHostnameVerifier")
        public boolean verify(String hostname, SSLSession session) { return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        lux = event.values[0];
        changeTheme();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void changeTheme() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean tmpTheme;
        tmpTheme = lux < 100;

        if (tmpTheme != sharedPreferences.getBoolean(KEY_THEME, false)) {
            editor.putBoolean(KEY_THEME, tmpTheme);
            editor.apply();
            recreate();
        }
    }
}
