package com.example.exovolley;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Profile_user extends AppCompatActivity implements SensorEventListener {

    //Déclaration des variables globaux
    private static final String PREFERENCES = "Prefs" ;
    private static final  String KEY_THEME = "keyTheme";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private List<Category> categories;
    private Category category;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProfileUserAdapter adapter;

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

        setContentView(R.layout.activity_profile_user);
        setAdapter();
        addCategory();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    //Méthode pour initialiser les variables
    private void initialisationVariable(){
        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //instanciation de la liste des catégories
        categories = new ArrayList<>();
    }

    private void changeTheme(){
        //vérifie la valeur de " lux " qui est l'intensité de la lumière
        if(lux < 100){
            tmpTheme = true;
            Log.d("Perso", ""+tmpTheme);
        } else {
            tmpTheme = false;
            Log.d("Perso", ""+tmpTheme);
        }

        if(tmpTheme != sharedPreferences.getBoolean(KEY_THEME, false)){
            editor.putBoolean(KEY_THEME, tmpTheme);
            editor.commit();
            recreate();
        }
    }

    public void addCategory(){
        //instancie un objet catégorie de test
        category = new Category(1, "Test", "My description", "category1");
        categories.add(category);
        category = new Category(2  , "Test", "My ", "category1");
        categories.add(category);
        //refresh l'adaptateur
        adapter.notifyDataSetChanged();
    }

    private void  setAdapter(){
        recyclerView = findViewById(R.id.rvCategoryUser);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProfileUserAdapter(categories, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        lux = event.values[0];

        changeTheme();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
