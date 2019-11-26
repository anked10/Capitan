package com.tec.bufeo.capitan.Activity;

import android.app.Application;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;


import com.tec.bufeo.capitan.MVVM.Foro.publicaciones.Repository.FeedWebServiceRepository;
import com.tec.bufeo.capitan.MVVM.Foro.publicaciones.ViewModels.FeedListViewModel;
import com.tec.bufeo.capitan.R;
import com.tec.bufeo.capitan.Util.Preferences;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    public TimerTask task;
    private static final long TIEMPO = 1;
    SharedPreferences preferencesUser;
    Preferences preferences;
    FeedListViewModel feedListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        preferences =  new Preferences(this);
       //Obtenemos los datos del sharetpreferense, si el usuario se logueo antes, nops manda directo al menu principal, caso contrario denbe loguearse
        preferencesUser = getSharedPreferences("User", Context.MODE_PRIVATE);
        feedListViewModel = ViewModelProviders.of(this).get(FeedListViewModel.class);
        cargarFeed();
        Tarea tarea = new Tarea();
        tarea.execute();

        //Especificamos la tarea que debe hacer
        task = new TimerTask() {
            @Override
            public void run() {

               if(!preferencesUser.getString("idusuario", "").equals("")){
                    Intent i= new Intent(getApplicationContext(),MenuPrincipal.class);
                    startActivity(i);
                    //finish();
                }
                else{
                    Intent i1= new Intent(getApplicationContext(),Login.class);
                    startActivity(i1);
                    //finish();
                }


            }
        };


    }

    public class Tarea extends AsyncTask<Void,Integer,Void> {

        int progreso;

        @Override
        protected void onPreExecute() {
            progreso = 0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (progreso < 100){

                progreso++;
                publishProgress(progreso);
                SystemClock.sleep(1);
            }
            //Inicia la tarea
            Timer timer = new Timer();
            timer.schedule(task,TIEMPO);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    Application application;
    public void cargarFeed(){
        FeedWebServiceRepository feedTorneoWebServiceRepository = new FeedWebServiceRepository(application);
        feedTorneoWebServiceRepository.providesWebService(preferences.getIdUsuarioPref(),"0","0","0");
    }

}
