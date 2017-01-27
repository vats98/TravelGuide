package sujeet.traveller_guide;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {

    private TextView tvCity;
    private TextView tvCountry;
    private TextView tvTime;
    private TextView tvDate;
    private TextView tvLat;
    private TextView tvLon;
    private TextView tvTemperature;
    private TextView tvLastUpdate;

    private ImageView ivThermo;
    private ImageView ivWeather;

    private Button btnNearbyPlaces;
    private Button btnTravelGuide;

    private LocationManager locationManager;
    private Handler handler;
    private Thread thread;

    private boolean isRunning=false;

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar= (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        startService(new Intent(this,MyService.class));

        if(!isOnline())
            Toast.makeText(getApplicationContext(), "Internet connection is required", Toast.LENGTH_SHORT).show();

        getUI();

        setTime();

        setLocation();

        setTemperature();

        startThread();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        registerLocationListener();
    }

    private void getUI() {

        ivWeather = (ImageView) findViewById(R.id.ivWeather);
        ivThermo = (ImageView) findViewById(R.id.ivThermometer);
        tvLastUpdate = (TextView) findViewById(R.id.tvLastUpdate);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvCountry = (TextView) findViewById(R.id.tvCountry);
        tvLat = (TextView) findViewById(R.id.tvLatitude);
        tvLon = (TextView) findViewById(R.id.tvLongitude);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        btnNearbyPlaces = (Button) findViewById(R.id.btnFindNearbyPlaces);
        btnTravelGuide = (Button) findViewById(R.id.btnTravelGuide);

        Typeface type = Typeface.createFromAsset(getAssets(), "fonta.otf");
        tvLastUpdate.setTypeface(type);
        tvCity.setTypeface(type);
        tvCountry.setTypeface(type);
        tvLat.setTypeface(type);
        tvLon.setTypeface(type);
        tvTime.setTypeface(type);
        tvDate.setTypeface(type);
        tvTemperature.setTypeface(type);
        btnNearbyPlaces.setTypeface(type);
        btnTravelGuide.setTypeface(type);

        ivWeather.setOnClickListener(this);
        tvTemperature.setOnClickListener(this);
        ivThermo.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        btnNearbyPlaces.setOnClickListener(this);
        btnTravelGuide.setOnClickListener(this);
    }

    private void setTime() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMMM dd, yyyy");
        String timeText = sdfTime.format(cal.getTime());
        tvTime.setText(timeText);
        String dateText = sdfDate.format(cal.getTime());
        tvDate.setText(dateText);
    }

    private void setLocation() {

        SharedPreferences location = getSharedPreferences("location", MODE_PRIVATE);
        int lat = (int) location.getFloat("lat", 0f);
        int lon  = (int) location.getFloat("lon", 0f);
        String city = location.getString("city", "NA");
        String country = location.getString("country", "NA");

        tvLat.setText(""+lat+"°N");
        tvLon.setText(""+lon+"°E");
        tvCity.setText(city);
        tvCountry.setText(country);
    }

    private void setTemperature() {

        SharedPreferences temperature = getSharedPreferences("temperature", MODE_PRIVATE);
        int temp = temperature.getInt("temp", 0);
        String update = temperature.getString("update", "NA");
        tvTemperature.setText(""+temp+"°");
        tvLastUpdate.setText(update);

        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/saved.png");
        Picasso.with(getApplicationContext()).load(file).resize(100, 100)
                .placeholder(R.drawable.noconn).into(ivWeather);
    }

    private void startThread() {

        isRunning = true;
        handler = new Handler();
        thread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (isRunning) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setTime();
                        }
                    });
                    try {
                        Thread.sleep(3 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void registerLocationListener() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://sujeet.traveller_guide/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    private void getTemperature() {

        if(!isOnline()) {
            Toast.makeText(this, "Internet connection is required", Toast.LENGTH_SHORT).show();
            return;
        }

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

                    if(weather!=null && weather.length()>0) {

                        JSONObject jsonObject = weather.getJSONObject(0);
                        String icon = jsonObject.getString("icon");
                        String imageUrl = "http://openweathermap.org/img/w/"+icon+".png";
                        iconUrl = imageUrl;

                        saveImage(imageUrl);
                    } else {
                        Picasso.with(getApplicationContext()).load(R.drawable.noconn).
                                resize(100, 100).into(ivWeather);
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

                    setTemperature();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError || error instanceof NoConnectionError)
                    Toast.makeText(MainActivity.this, "Connection timed out", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Couldn't fetch temperature", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
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

    @Override
    public void onLocationChanged(Location location) {

        float lat = (float) location.getLatitude();
        float lon = (float) location.getLongitude();
        String city = "";
        String country = "";

        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geoCoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            city = addresses.get(0).getLocality();
            country = addresses.get(0).getCountryCode();
        }

        SharedPreferences locationPreference = getSharedPreferences("location", MODE_PRIVATE);
        SharedPreferences.Editor ed = locationPreference.edit();
        ed.putFloat("lat", lat);
        ed.putFloat("lon", lon);
        ed.putString("city", city);
        ed.putString("country", country);
        ed.commit();
        if(isRunning)
        setLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(MainActivity.this, "Connection Found!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Connection Unavailable!", Toast.LENGTH_SHORT).show();
    }

    private boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnFindNearbyPlaces:
                Intent nearbyIntent = new Intent(this, SearchListScreen.class);
                startActivity(nearbyIntent);
                break;
            case R.id.btnTravelGuide:
                Intent travelIntent = new Intent(this, Railway.class);
                startActivity(travelIntent);
                break;
        }
    }

    @Override
    protected void onResume() {

        if(thread != null)
            isRunning = true;
        super.onResume();
    }

    @Override
    protected void onPause() {

        isRunning = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        isRunning = false;
        thread = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

       // locationManager.removeUpdates(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://sujeet.traveller_guide/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();

        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.float_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings :
                return true;
            case R.id.action_refresh :
                getTemperature();
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }
}
