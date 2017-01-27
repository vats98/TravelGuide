package sujeet.traveller_guide;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyService extends Service {

    int time = 60 * 60 * 000;
    boolean isRunning=true;
    String finalUrl,type;
    String types[]={"restaurant", "atm", "hospital", "train_station", "airport",
            "movie_theater","police","park"};
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning)
                {
                    getTemperature();
                    getPlaces();
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getTemperature() {

        if(!isOnline())
            return;

        SharedPreferences location = getSharedPreferences("location", MODE_PRIVATE);
        float lat = location.getFloat("lat", 0f);
        float lon = location.getFloat("lon", 0f);

        String url = "http://api.openweathermap.org/data/2.5/weather?";
        String key = "049675cf1c4e1d89c234f02424e73070";

        StringBuilder sbb = new StringBuilder("");
        sbb.append(url);
        sbb.append("lat=" + lat);
        sbb.append("&lon=" + lon);
        sbb.append("&APPID=");
        sbb.append(key);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, sbb.toString(),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response == null)
                        return;

                    String iconUrl = "";
                    JSONArray weather = response.getJSONArray("weather");

                    if(weather != null && weather.length() > 0) {
                        JSONObject jsonObject = weather.getJSONObject(0);
                        String icon = jsonObject.getString("icon");
                        String imageUrl = "http://openweathermap.org/img/w/"+icon+".png";
                        iconUrl = imageUrl;

                        saveImage(imageUrl);
                    }

                    JSONObject main = response.getJSONObject("main");
                    int temp = (int) main.getDouble("temp");
                    temp = temp - 273;

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
                    String s = "Last Updated: "+sdf.format(cal.getTime());

                    SharedPreferences temperature = getSharedPreferences("temperature",
                            MODE_PRIVATE);
                    SharedPreferences.Editor ed = temperature.edit();
                    ed.putInt("temp", temp);
                    ed.putString("update", s);
                    ed.putString("icon", iconUrl);
                    ed.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null)
                    error.printStackTrace();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void saveImage(String imageUrl) {

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File file = new File(
                                Environment.getExternalStorageDirectory().getPath()
                                        + "/saved.png");
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG,100,ostream);
                            ostream.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };

        Picasso.with(getApplicationContext()).load(imageUrl).into(target);
    }
    private void getPlaces() {

        if(!isOnline())
            return;

        for(int i = 0; i < types.length; i++) {

            type = types[i];

            deleteOld();

            getFinalUrl();

            addNew();
        }
    }

    private void deleteOld() {

        DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 0);
        dbHandler.deleteRow(type);
    }

    private void getFinalUrl() {

        String url = "https://maps.googleapis.com/maps/api/place/search/json?location=";
        String key = "AIzaSyAjl_BlNKjkIR-9XM53R2b6XUTkIMZig10";
        SharedPreferences spLocation = getSharedPreferences("location", MODE_PRIVATE);
        float lat = spLocation.getFloat("lat", 0f);
        float lon = spLocation.getFloat("lon", 0f);
        String radius = "5000";

        StringBuilder sb = new StringBuilder("");
        sb.append(url);
        sb.append(lat);
        sb.append(",");
        sb.append(lon);
        sb.append("&radius=");
        sb.append(radius);
        sb.append("&type=");
        sb.append(type);
        sb.append("&key=");
        sb.append(key);

        finalUrl = sb.toString();
    }

    private void addNew() {

        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, finalUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray jsArray = response.getJSONArray("results");
                            DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null,
                                    0);

                            for (int i = 0; i < jsArray.length(); ++i) {

                                ContentValues cv = new ContentValues();

                                JSONObject jsObject = jsArray.getJSONObject(i);
                                cv.put(DBHandler.COL1, jsObject.getString("name"));
                                cv.put(DBHandler.COL2, jsObject.getString("vicinity"));
                                cv.put(DBHandler.COL3, type);

                                JSONObject jsonObject1 = jsObject.getJSONObject("geometry");
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("location");
                                cv.put(DBHandler.COL4, jsonObject2.getDouble("lat"));
                                cv.put(DBHandler.COL5, jsonObject2.getDouble("lng"));

                                dbHandler.addRow(cv);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null)
                            error.printStackTrace();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest1);
    }
}
