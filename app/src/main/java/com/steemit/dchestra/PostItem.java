package com.steemit.dchestra;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PostItem {

    private Context context;

    private String username;
    private String profilePictureURL;

    private ProfilePictureDownloader profilePictureDownloader;
    private Bitmap profilePicture;

    public PostItem(Context context, String username, String profilePictureURL) {
        this.context = context;
        this.username = username;
        this.profilePictureURL = profilePictureURL;

        Drawable mask = context.getResources().getDrawable(R.drawable.circle);
        mask.setTint(0x000);
        mask.setTintMode(PorterDuff.Mode.ADD);

        profilePictureDownloader = ((ProfilePictureDownloader)new ProfilePictureDownloader().maskImage(mask).execute(this.profilePictureURL)).addCallback(new BitmapCallback() {
            @Override
            public void onDone(Bitmap bitmap) {
                profilePicture = bitmap;
            }
        });


    }

    public Context getContext() {
        return context;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void getProfilePicture(BitmapCallback cb) {
        if(profilePicture != null) {
            cb.onDone(profilePicture);
        }
        profilePictureDownloader.addCallback(cb);
    }

    private static class ProfilePictureDownloader extends AsyncTask<String, Void, Bitmap> {

        private ArrayList<BitmapCallback> callbacks = new ArrayList<>();
        private Bitmap bitmapMask;
        private Drawable drawableMask;

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                Bitmap org = BitmapFactory.decodeStream((InputStream)(new URL(urls[0]).getContent()));
                if(this.drawableMask != null) {
                    // Convert Drawable to Bitmap
                    int w = org.getWidth(), h = org.getHeight();
                    Bitmap mutableBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(mutableBitmap);
                    drawableMask.setBounds(0, 0, w, h);
                    drawableMask.draw(canvas);

                    this.bitmapMask = mutableBitmap;
                }
                if(this.bitmapMask != null) {
                    // Mask Image
                    Bitmap result = Bitmap.createBitmap(bitmapMask.getWidth(), bitmapMask.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas mCanvas = new Canvas(result);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                    mCanvas.drawBitmap(org, 0, 0, null);
                    mCanvas.drawBitmap(bitmapMask, 0, 0, paint);
                    paint.setXfermode(null);

                    return result;
                }
                return org;
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private ProfilePictureDownloader maskImage(Bitmap bitmapMask) {
            this.bitmapMask = bitmapMask;
            return this;
        }

        private ProfilePictureDownloader maskImage(Drawable drawableMask) {
            this.drawableMask = drawableMask;
            return this;
        }

        private ProfilePictureDownloader addCallback(BitmapCallback cb) {
            callbacks.add(cb);
            return this;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            for(BitmapCallback cb : callbacks) {
                cb.onDone(bitmap);
            }
        }
    }

    public interface BitmapCallback {
        void onDone(Bitmap bitmap);
    }

}
