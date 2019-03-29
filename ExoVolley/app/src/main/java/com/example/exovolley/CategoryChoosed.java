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

public class CategoryChoosed extends AppCompatActivity implements SensorEventListener {
    private static final String PREFERENCES = "Prefs" ;
    private static final  String KEY_THEME = "keyTheme";

    private URL url;
    private List<Post> posts;
    private PostAdapter adapter;
    private RecyclerView recyclerView;

    private String newString;
    private String titre;


    private SharedPreferences sharedPreferences;
    private RecyclerView.LayoutManager manager;

    private SensorManager sensorManager;
    private Sensor sensor;

    private float lux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPrefs();
        if(sharedPreferences.getBoolean(KEY_THEME, false)){
            setTheme(R.style.NightTheme);
        }

        setContentView(R.layout.activity_category_choosed);

        init();
        setAdapterManager();
        getExtra();
        changeTitre();
        getUrl();
        checkPermission();
        checkHttps();
        dontCheckCert();
        sendAndRequestResponse();
    }

    private void initPrefs(){
        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    private void init(){
        adapter = new PostAdapter(posts, this);
        manager = new LinearLayoutManager(getApplicationContext());

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        recyclerView = findViewById(R.id.rcvCategoryChoosed);
        posts = new ArrayList<>();
    }

    private void setAdapterManager() {
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void getExtra(){
        Bundle extras = getIntent().getExtras();
        newString = extras != null ? extras.getString("STRING_I_NEED") : null;
        titre = extras != null ? extras.getString("TITLE") : null;
    }

    private void changeTitre() {
        TextView t = findViewById(R.id.txtTitreCategoryChoosed);
        t.setText(titre);
    }

    private void getUrl(){
        try {
            url = new URL("https://10.0.2.2:44303/api/post/cat" + newString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void checkPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
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
            assert https != null;
            https.setHostnameVerifier(DO_NOT_VERIFY);
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
            assert context != null;
            context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    public void sendAndRequestResponse() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, "https://10.0.2.2:44303/api/post/cat/" + newString, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response != null) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject json_data = response.getJSONObject(i);
                                    int id = json_data.getInt("id");
                                    String name = json_data.getString("name");
                                    String description = json_data.getString("description");
                                    String picture = json_data.getString("picture");
                                    Post post = new Post();
                                    post.setId(id);
                                    post.setName(name);
                                    post.setDescription(description);
                                    post.setPicture(picture);

                                    posts.add(post);
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
        public boolean verify(String hostname, SSLSession session) {
            return true;
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
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void changeTheme(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean tmpTheme;
        tmpTheme = lux < 100;

        if(tmpTheme != sharedPreferences.getBoolean(KEY_THEME, false)){
            editor.putBoolean(KEY_THEME, tmpTheme);
            editor.apply();
            recreate();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        lux = event.values[0];
        changeTheme();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
