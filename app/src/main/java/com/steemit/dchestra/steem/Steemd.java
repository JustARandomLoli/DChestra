package com.steemit.dchestra.steem;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Steemd {

    public static void getNewest(Context context, Receiver receiver) {

    }

    public static void getUser(Context context, String username) {

    }

    public static void sendAPIRequest(Context context, String endpoint, String method, Object params, final Receiver receiver) {
        JSONObject body = new JSONObject();
        try {
            body.put("jsonrpc", "2.0");
            body.put("id", 1);
            body.put("method", method);
            body.put("params", params);
            System.out.println(body.toString(2));
        } catch(JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonRequest<JSONObject> request = new JsonRequest<JSONObject>(endpoint, body.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
                if(receiver != null) receiver.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    return Response.success(new JSONObject(new String(response.data)), HttpHeaderParser.parseCacheHeaders(response));
                } catch(JSONException e) {
                    return Response.error(new VolleyError(e.getMessage()));
                }
            }
        };

        queue.add(request);

    }

    public interface Receiver {
        void onResponse(JSONObject result);
    }

}
