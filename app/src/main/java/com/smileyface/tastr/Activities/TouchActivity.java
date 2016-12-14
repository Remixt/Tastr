package com.smileyface.Tastr.Activities;

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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.smileyface.Tastr.Other.TastrItem;
import com.smileyface.Tastr.R;
import com.smileyface.Tastr.Utilities.CallHandler;
import com.smileyface.Tastr.Utilities.ItemLoader;
import com.smileyface.Tastr.Utilities.LocationHandler;
import com.smileyface.Tastr.Utilities.YelpDataExecutor;
import com.squareup.picasso.Picasso;

import static java.lang.Thread.sleep;


public class TouchActivity extends Activity {
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
    //private DownloadImageTask imageLoader;
    ItemLoader itemLoader;
    TastrItem currentItem;

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
                //startActivity(new Intent(TouchActivity.this, MainActivity.class));
            }
        });
        // Instantiate background process, connect to firebase and fill the Tastr Item queue

        // Wait for an item to be added before trying to load an image into the gui.
        TastrSync sync = new TastrSync();
        sync.execute();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
        loadSpinner = (ProgressBar) findViewById(R.id.loadingSpinner);
        loadSpinner.setVisibility(View.VISIBLE);

    }


    public void setDragProps(ImageView image, ImageView green, ImageView red) {
        image.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    default:
                        return true;
                }
            }
        });

        green.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        Log.d(msg, "Drag ended");
                        if (dropEventNotHandled(event)) {
                            v.setVisibility(View.VISIBLE);
                        }
                        CharSequence options[] = new CharSequence[]{"Menu Item: " + currentItem.getMenu().get(itemCounter - 1).getName(), "Address: "+ currentItem.getAddress(), "Phone: " + currentItem.getPhone(), "Rating: "+ currentItem.getRating()};

                        // create a google maps buffer in case the user wants to go to the restaurant found.
                        final Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=+" + currentItem.getAddress()));

                        // create an internet link buffer in case they want to read the reviews on yelp
                        final Intent webIntent = new Intent(Intent.ACTION_VIEW);
                        // experimental url, might not always work, we can just set it to do a google search for the restaurant or something if needed.
                        webIntent.setData(Uri.parse("http://www.google.com/#q=" + currentItem.getName() + "," + currentItem.getCity()));

                        AlertDialog.Builder builder = new AlertDialog.Builder(TouchActivity.this);
                        builder.setTitle("Restaurant: " + currentItem.getName());
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 1:
                                        startActivity(mapIntent);
                                        break;
                                    case 2:
                                        CallHandler call = new CallHandler(TouchActivity.this, currentItem.getPhone());
                                        call.makePhoneCall();
                                        break;
                                    case 3:
                                        startActivity(webIntent);
                                        break;
                                }
                            }
                        });
                        builder.show();
                        break;
                    default:
                        return true;
                }
                return false;
            }
        });


        red.setOnDragListener(new View.OnDragListener() {
            int i = 0;
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        showNextImage();
                        v.setVisibility(View.VISIBLE);
                        Log.d(msg, "Drag ended");
                        if (dropEventNotHandled(event)) {
                            v.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
                        return true;
                }
                return false;
            }
        });

        gdt = new GestureDetector(new GestureListener());
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override

            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("Touch Listener", "Action down triggered");
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                            v.startDrag(data, shadowBuilder, v, 0);


                    default:
                        Log.i("Touch Listener", "default triggered");
                        v.setVisibility(View.VISIBLE);

                }


                return true;
                }


        });
    }


    private boolean dropEventNotHandled(DragEvent dragEvent) {
        return !dragEvent.getResult();
    }

    private Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Touch Activity") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("https://console.firebase.google.com/project/unt-team-project/overview"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    int totalItems = 0;
    int itemCounter = 0;
    int loadingError = 0;

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
        setDragProps(img, yum, yuck);
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


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }
            return false;
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getCurrentTastrItem() {

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