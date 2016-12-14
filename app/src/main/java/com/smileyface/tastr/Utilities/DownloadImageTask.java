package com.smileyface.Tastr.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;


// This class takes a URL and downloads the image at the url.
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView tempImage;
    public boolean doneLoading = false;


    public DownloadImageTask(ImageView bmImage) {
        this.tempImage = bmImage;
        tempImage.setScaleType(ImageView.ScaleType.FIT_XY);

    }

    protected Bitmap doInBackground(String... urls) {
        String inputURL = urls[0];
        Bitmap tempBmap = null;

        try {
            InputStream in = new java.net.URL(inputURL).openStream();
            tempBmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return tempBmap;
    }

    protected void onPostExecute(Bitmap result) {
        doneLoading = true;
        tempImage.setImageBitmap(result);

        tempImage.setVisibility(View.VISIBLE);
    }
}
