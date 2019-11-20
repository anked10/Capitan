package com.tec.bufeo.capitan.MVVM.Foro.publicaciones.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;


import com.tec.bufeo.capitan.MVVM.Foro.publicaciones.Models.ModelFeed;
import com.tec.bufeo.capitan.MVVM.Foro.publicaciones.Views.ForoFragment;

import java.util.List;



public class FeedRoomDBRepository {
    private FeedDao feedDao;
    LiveData<List<ModelFeed>> mAllPosts;

    public FeedRoomDBRepository(Application application){
         FeedRoomDataBase db = FeedRoomDataBase.getDatabase(application);
        feedDao = db.postInfoDao();
        //emAllPosts = feedDao.getAllPosts();
     }



    public LiveData<List<ModelFeed>> getAllPosts() {


        mAllPosts = feedDao.getAllPosts();
        return mAllPosts;
    }

    public LiveData<List<ModelFeed>> getSearch(String query){
        mAllPosts = feedDao.searchFeeds(query);
        return mAllPosts;

    }



    public void deleteAllFeed() {
        new DeleteAllFeedAsyncTask(feedDao).execute();
    }

    private static class DeleteAllFeedAsyncTask extends AsyncTask<Void, Void, Void> {
        private FeedDao feedDao;

        private DeleteAllFeedAsyncTask(FeedDao feedDao) {
            this.feedDao = feedDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            feedDao.deleteAll();
            Log.i("eliminado", "doInBackground: eliminado");
            return null;
        }
    }

    public void deleteOneFeed(String foro_id) {
        new DeleteOneFeedAsyncTask(feedDao).execute(foro_id);
    }


    public void insert(ModelFeed modelFeed){
        new insertAsyncTask(feedDao).execute(modelFeed);
    }

    private static class insertAsyncTask extends AsyncTask<ModelFeed, Void, Void> {

        private FeedDao mAsyncTaskDao;

        insertAsyncTask(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ModelFeed... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
    public void insertPosts (List<ModelFeed> modelFeed) {
        new insertPostsAsyncTask(feedDao).execute(modelFeed);
    }

    private static class insertPostsAsyncTask extends AsyncTask<List<ModelFeed>, Void, Void> {

        private FeedDao mAsyncTaskDao;

        insertPostsAsyncTask(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<ModelFeed>... params) {
            mAsyncTaskDao.insertPosts(params[0]);
            return null;
        }
    }




    private static class DeleteOneFeedAsyncTask extends AsyncTask<String, Void, Void> {
        private FeedDao feedDao;

        private DeleteOneFeedAsyncTask(FeedDao feedDao) {
            this.feedDao = feedDao;
        }

        @Override
        protected Void doInBackground(String... modelFeeds) {
            feedDao.deleteOneFeed(modelFeeds[0]);

            return null;
        }
    }
}
