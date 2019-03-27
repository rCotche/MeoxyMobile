package com.example.exovolley;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
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

    //Déclaration des variables globaux
    private static final String JSON_URL = "https://10.0.2.2:44303/api/category";
    URL url;
    private TextView txtResponse;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    ListView datas;

    private List<Category> categories;
    private Category category;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private CategoryAdapter adapter;

    private static final String PREFERENCES = "Prefs" ;
    private static final  String KEY_THEME = "keyTheme";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SensorManager sensorManager;
    private Sensor sensor;

    private float lux;
    private Boolean tmpTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialisationVariable();

        //Vérifie si il y a un changement de thème
        if(sharedPreferences.getBoolean(KEY_THEME, false)){
            setTheme(R.style.NightTheme);
        }
        setContentView(R.layout.activity_main);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        recyclerView = (RecyclerView) findViewById(R.id.recyJson);

        categories = new ArrayList<>();

        setAdapterManager();

        //instancie une variable de type URL
        try {
            url = new URL(JSON_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        checkPermission();
        HttpURLConnection http = null;

        //vérifie si le protocole est : " https "
        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            HttpsURLConnection https = null;
            try {
                //ouvre la connexion
                https = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            https.setHostnameVerifier(DO_NOT_VERIFY);
            http = https;
        } else {
            try {
                //ouvre la connexion
                http = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

        sendAndRequestResponse();
    }

    public void sendAndRequestResponse() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response != null) {
                            for (int i=0;i<response.length();i++){
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

    //Méthode qui vérifie si les permissions sont accordés
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
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

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
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

    //configure l'adaptateur pour le recyclerview
    private void  setAdapterManager(){

        adapter = new CategoryAdapter(categories, this);
        manager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Selon la catégorie choisie l'application redirige l'utilisateur vers le contenu correspondant
                Intent i = new Intent(MainActivity.this, CategoryChoosed.class);
                TextView letitre = (TextView) view.findViewById(R.id.txtTitre);
                TextView id = (TextView) view.findViewById(R.id.txtId);

                //passe les paramètres à la page suivante
                i.putExtra("letitre", letitre.getText().toString());
                i.putExtra("STRING_I_NEED", id.getText().toString());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void initialisationVariable(){
        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    }

    private void changeTheme(){
        if(lux < 100){
            tmpTheme = true;
        } else {
            tmpTheme = false;
        }

        if(tmpTheme != sharedPreferences.getBoolean(KEY_THEME, false)){
            editor.putBoolean(KEY_THEME, tmpTheme);
            editor.commit();
            recreate();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        lux = event.values[0];
        changeTheme();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void test(View v){
        Intent i =new Intent(MainActivity.this, Profile_user.class);
        startActivity(i);
    }
}
