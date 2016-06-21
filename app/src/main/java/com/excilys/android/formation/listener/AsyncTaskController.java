package com.excilys.android.formation.listener;

/**
 * AsyncTask controller for encapsulation of asynctask pro and pre execute methods
 */
public interface AsyncTaskController<T> {

    void onPreExecute();
    void onPostExecute(T result);

}