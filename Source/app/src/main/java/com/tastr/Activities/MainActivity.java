package com.tastr.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lorentzos.flingswipe.*;
import com.tastr.Other.TastrItem;
import com.smileyface.tastr.R;
import com.tastr.Utilities.CallHandler;
import com.tastr.Utilities.ItemLoader;
import com.tastr.Utilities.LocationHandler;
import com.tastr.Utilities.YelpDataExecutor;
import com.squareup.picasso.Picasso;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.smileyface.tastr.R.id.frame;
import static java.lang.Thread.sleep;


public class MainActivity extends Activity {
    private String msg = "Drag Listener";
    private ProgressBar loadSpinner;
    //private RelativeLayout.LayoutParams layoutParams;
    final static private int minHeight = 500;
    final static private int minWidth = 600;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    YelpDataExecutor yelp = new YelpDataExecutor();

    LocationHandler curLoc = new LocationHandler(this);
    GestureDetector gdt;

    private GoogleApiClient client;
    private ArrayAdapter<TastrItem> arrayAdapter;

    ItemLoader itemLoader;
    TastrItem currentItem;

    @BindView(R.id.img) ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);
        loadSpinner = (ProgressBar) findViewById(R.id.loadingSpinner);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        curLoc.askForlocation();
        yelp.execute(curLoc.getCurrentLat(), curLoc.getCurrentLong());
        itemLoader = new ItemLoader(yelp);
        itemLoader.execute();
        ImageView goToSettings = (ImageView) findViewById(R.id.settingsIcon);
        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
        // Instantiate background process, connect to firebase and fill the Tastr Item queue

        // Wait for an item to be added before trying to load an image into the gui.
        TastrSync sync = new TastrSync();
        sync.execute();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        loadSpinner = (ProgressBar) findViewById(R.id.loadingSpinner);
        loadSpinner.setVisibility(View.VISIBLE);
        //arrayAdapter = new ArrayAdapter<TastrItem>(this,R.id.img,itemLoader.getItemList());
       // SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) (itemLoader.getItemList().);

    }






    @Override
    public void onStart() {
        super.onStart();
    }

    int totalItems = 0;
    int itemCounter = 0;

    public void getNextRestaurant() {
        itemCounter = 0;
        if (itemLoader.getNextItem() != null) {
            currentItem = itemLoader.getNextItem();
            totalItems = currentItem.getMenu().size() - 1;
            showNextImage();
        }
    }

    public void showNextImage() {
        ImageView yuck = (ImageView) findViewById(R.id.yuck);
        ImageView yum = (ImageView) findViewById(R.id.yum);
        ImageView img = (ImageView) findViewById(R.id.img);
        loadSpinner = (ProgressBar) findViewById(R.id.loadingSpinner);
        loadSpinner.setVisibility(View.VISIBLE);
        //DownloadImageTask imageLoader = new DownloadImageTask(img);
        // if (currentItem != null) {
        if (!currentItem.getMenu().isEmpty() && currentItem.getMenu().size() > itemCounter) {
            Picasso.with(this).load(currentItem.getMenu().get(itemCounter).getImagePath()).into(img);
            loadSpinner.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);

            Log.i("Touch Activity ", "Loading New Image ---> " + currentItem.getMenu().get(itemCounter).getImagePath() + " From " + currentItem.getName());
            itemCounter++;

        } else {
            Log.i("Touch Activity ", "Can't find any more images to download from " + currentItem.getName() + ". Loading next restaurant... ");
            getNextRestaurant();
        }
        }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // wait for the first Tastr Item to be added
    private class TastrSync extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {

            while (!itemLoader.checkIfReady()) try {
                sleep(100); // wait 100 ms before checking again, saves cpu
            } catch (InterruptedException e) {
                e.printStackTrace(); // if there is a problem while sleeping, print out the errors encountered.
            }
            if (curLoc.getCurrentLat() == null) {
                try {
                    sleep(1000); // wait 1000 ms before checking again, saves cpu
                } catch (InterruptedException e) {
                    e.printStackTrace(); // if there is a problem while sleeping, print out the errors encountered.
                }
            }

            return null;
        }//doInBackground

        // Everything you want to happen AFTER the doInBackground function is executed. Use this method to make changes to the GUI.
        @Override
        protected void onPostExecute(String results) {
            getNextRestaurant();
        }
    }
}