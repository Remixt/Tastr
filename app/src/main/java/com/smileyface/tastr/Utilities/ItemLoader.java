package com.smileyface.tastr.Utilities;

import android.os.AsyncTask;
import android.util.Log;

import com.smileyface.tastr.Other.TastrItem;

import java.util.ArrayList;

/**
 * Created by Remixt on 11/26/2016.
 */

public class ItemLoader extends AsyncTask<String, Void, String> {
    private int counter = 0;
    private FirebaseHandler firebase;
    private YelpDataExecutor yelp;
    private ArrayList<TastrItem> itemList = new ArrayList<>();
    private boolean ready = false;

    public ItemLoader(YelpDataExecutor yelpContext) {
        yelp = yelpContext;
        Log.i("Item Loader ","Starting Database Operations...");
    }

    public TastrItem getNextItem() {
        if (counter == 0 && !itemList.isEmpty()) {
            counter++;
            return itemList.get(0);
        } else if (counter >= itemList.size() - 1 && !itemList.isEmpty()) {
            counter = 0;
            return itemList.get(0);
        } else if (!itemList.isEmpty()) {
            counter++;
            return itemList.get(counter);
        }
        Log.i("Item Loader ","Item List is null");
        return null; // no items in the list
        // test
    }

    public boolean checkIfReady() {
        if (ready)
            return true;
        else
            return false;
    }

    // Everything you want to happen OUTSIDE of the GUI thread. This is a background process.
    protected String doInBackground(String... params) {
        // Add all the Tastr Items to the Array list using the list of restaurants found by yelp.
        for (int i = 0; i < yelp.getRestaurants().size(); i++) {

            TastrItem temp;
            // loop through each restaurant and add all the menu items from each one. I think we need to randomize this list later on to provide variety in the app.
            firebase = new FirebaseHandler("Tastr Items/" + yelp.getRestaurants().get(i)); //Change where in the database we want to search for information.
            firebase.readKeyFromDatabase();
            while (!firebase.isReaderDone()) {

                }
            if (firebase.getTastrItem() != null) {
                if (firebase.getTastrItem().getMenu().get(0).getImagePath() != "Unknown") {
                    temp = firebase.getTastrItem();
                    itemList.add(temp);
                }
            }
        }
        ready = true;
        return null;
    }//doInBackground

    // Everything you want to happen AFTER the doInBackground function is executed. Use this method to make changes to the GUI.
    @Override
    protected void onPostExecute(String results) {
        Log.i("Item Loader ","Finished Database Operations, closing firebase connection...");
        ready = true;
    }//On Post Execute
}//loader class
