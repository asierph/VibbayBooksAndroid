package com.asierph.vibbaybooks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;


public class DecodificarImagen extends AsyncTask<String, Void, Bitmap> {
    private ImageView img;

    public DecodificarImagen(ImageView im){
        this.img = im;
    }

    protected Bitmap doInBackground(String... urls){
        String url = urls[0];
        Bitmap bm = null;
        try{
            InputStream is = new java.net.URL(url).openStream();
            bm = BitmapFactory.decodeStream(is);
        } catch(Exception e){
            e.printStackTrace();
        }
        return bm;
    }
    protected void onPostExecute(Bitmap result) {
        img.setImageBitmap(result);
    }
}
