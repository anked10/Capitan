package com.tec.bufeo.capitan.MVVM.Torneo.TabRetos.Repository;

import android.os.AsyncTask;


public class RetosDbAsync extends AsyncTask<Void, Void, Void> {

    private final RetosDao mDao;

    RetosDbAsync(RetosRoomDataBase db) {
        mDao = db.postInfoDao();
    }

    @Override
    protected Void doInBackground(final Void... params) {
        //mDao.deleteAll();
       /* User user = new User("Chandra","SW");
        mDao.insert(user);
        user = new User("Mohan","student");
        mDao.insert(user);*/
        return null;
    }
}
