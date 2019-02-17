package com.steemit.dchestra.steem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;

import com.steemit.dchestra.MainActivity;
import com.steemit.dchestra.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class SteemUser {

    private static ArrayList<SteemUser> steemUsers = new ArrayList<>();

    private int index;

    private Context context;

    private String username;
    private String displayName;
    private String profilePictureURL;

    private String about;

    private ProfilePictureDownloader profilePictureDownloader;
    private Bitmap profilePicture;

    public SteemUser(Context context, String username, String displayName, String profilePictureURL) {
        this.context = context;
        this.username = username;
        this.displayName = displayName;
        this.profilePictureURL = profilePictureURL;
        this.about = "";

        if(profilePictureURL.length() > 0) {
            profilePictureDownloader = ((ProfilePictureDownloader)new ProfilePictureDownloader().maskImage(context.getResources().getDrawable(R.drawable.circle)).execute(this.profilePictureURL)).addCallback(new BitmapCallback() {
                @Override
                public void onDone(Bitmap bitmap) {
                    profilePicture = bitmap;
                }
            });
        }

        this.index = steemUsers.size();
        steemUsers.add(this);
    }

    public SteemUser(Context context, String username, String displayName, String profilePictureURL, String about) {
        this(context, username, displayName, profilePictureURL);
        this.about = about;
    }

    public static SteemUser getSteemUser(int index) {
        return steemUsers.get(index);
    }

    public static void getSteemUser(final Context context, final SteemUserCallback callback, String... usernames) {
        ArrayList<String> notFound = new ArrayList<>();
        final ArrayList<SteemUser> out = new ArrayList<>();
        for(String username : usernames) {
            notFound.add(username);
        }
        for(SteemUser user : steemUsers) {
            if(notFound.contains(user.getUsername())) {
                notFound.remove(user.getUsername());
                out.add(user);
            }
        }
        if(notFound.size() > 0) {
            JSONArray parms = new JSONArray();
            JSONArray pa = new JSONArray();
            for(String username : notFound) {
                pa.put(username);
            }
            parms.put(pa);

            Steemd.sendAPIRequest(context, "https://api.steemit.com", "condenser_api.lookup_account_names", parms, new Steemd.Receiver() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray result = response.getJSONArray("result");
                        for(int i = 0; i < result.length(); i++) {
                            try {
                                JSONObject jsonObject = result.getJSONObject(i);

                                String username = jsonObject.getString("name");
                                String displayName = "";
                                String profileImage = "";
                                String about = "";
                                try {
                                    if(jsonObject.has("json_metadata")) {
                                        JSONObject jsonMetadata = new JSONObject(jsonObject.getString("json_metadata"));
                                        if(jsonMetadata.has("profile")) {
                                            JSONObject profile = jsonMetadata.getJSONObject("profile");
                                            if(profile.has("name"))
                                                displayName = profile.getString("name");
                                            if(profile.has("profile_image"))
                                                profileImage = profile.optString("profile_image");
                                        }
                                    }
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    System.out.println("finally - " + username);
                                    if(displayName.length() == 0)
                                        displayName = "@" + username;
                                    out.add(new SteemUser(context, username, displayName, profileImage, about));
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    } finally {
                        callback.onResult(out.toArray(new SteemUser[0]));
                    }
                }
            });
        } else {
            callback.onResult(out.toArray(new SteemUser[0]));
        }
    }

    public int getIndex() {
        return index;
    }

    public String getAbout() {
        return about;
    }

    public Context getContext() {
        return context;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void getProfilePicture(BitmapCallback cb) {
        if(profilePicture != null) {
            cb.onDone(profilePicture);
        }
        if(profilePictureDownloader != null)
            profilePictureDownloader.addCallback(cb);
    }

    private static class ProfilePictureDownloader extends AsyncTask<String, Void, Bitmap> {

        private ArrayList<BitmapCallback> callbacks = new ArrayList<>();
        private Drawable drawableMask;

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                // Download Bitmap
                Bitmap org = BitmapFactory.decodeStream(new URL(urls[0]).openStream());

                // Resize & Center Bitmap
                int dimensions = Math.min(Math.min(org.getWidth(), org.getHeight()), 512);
                Bitmap dstBmp = ThumbnailUtils.extractThumbnail(org, dimensions, dimensions, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

                if(this.drawableMask != null) {
                    // Convert Drawable to Bitmap
                    int w = dstBmp.getWidth(), h = dstBmp.getHeight();
                    Bitmap mutableBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(mutableBitmap);
                    drawableMask.setBounds(0, 0, w, h);
                    drawableMask.draw(canvas);

                    // Mask Image
                    Bitmap result = Bitmap.createBitmap(dstBmp.getWidth(), dstBmp.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas mCanvas = new Canvas(result);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                    mCanvas.drawBitmap(dstBmp, 0, 0, null);
                    mCanvas.drawBitmap(mutableBitmap, 0, 0, paint);
                    paint.setXfermode(null);

                    // Recycle Bitmaps that we don't need anymore
                    dstBmp.recycle();
                    mutableBitmap.recycle();

                    return result;
                }

                return dstBmp;
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
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

    public interface SteemUserCallback {
        void onResult(SteemUser[] users);
    }

}
