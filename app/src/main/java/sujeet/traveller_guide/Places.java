package sujeet.traveller_guide;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Places extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private String finalUrl = "";
    private String names[];
    private double lati[];
    private double longi[];
    private String add [];

    private ListView listView;
    private Typeface ttf;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        Toolbar myToolbar= (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab= getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        getUI();

        setPlaces();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void getUI() {


        listView = (ListView) findViewById(R.id.lvPlace);

        listView.setOnItemClickListener(this);

        ttf = Typeface.createFromAsset(getAssets(), "fonta.otf");
    }

    private void setPlaces() {

        DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 0);
        ArrayList<ContentValues> places = dbHandler.getRows(getIntent().getStringExtra("type"));

        names = new String[places.size()];
        add = new String[places.size()];
        lati = new double[places.size()];
        longi = new double[places.size()];

        for(int i=0; i<places.size(); i++) {
            ContentValues cv = places.get(i);
            names[i] = cv.getAsString(DBHandler.COL1);
            add[i] = cv.getAsString(DBHandler.COL2);
            lati[i] = cv.getAsDouble(DBHandler.COL4);
            longi[i] = cv.getAsDouble(DBHandler.COL5);
        }

        //Toast.makeText(this, getIntent().getStringExtra("type"), Toast.LENGTH_SHORT).show();

        Custom custom=new Custom();
        listView.setAdapter(custom);
    }

    private void getPlaces() {

        if(!isOnline()) {
            Toast.makeText(this, "Internet connection required", Toast.LENGTH_SHORT).show();
            return;
        }

        deleteOld();

        getFinalUrl();

        addNew();
    }

    private boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void deleteOld() {

        DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 0);
        dbHandler.deleteRow(getIntent().getStringExtra("type"));
    }

    private void getFinalUrl() {

        String url = "https://maps.googleapis.com/maps/api/place/search/json?location=";
        String key = "AIzaSyAjl_BlNKjkIR-9XM53R2b6XUTkIMZig10";
        String type = getIntent().getStringExtra("type");
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

                            //Toast.makeText(Places.this, "Response Received", Toast.LENGTH_SHORT).show();

                            JSONArray jsArray = response.getJSONArray("results");
                            DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null,
                                    0);

                            //Toast.makeText(Places.this, ""+jsArray.length(), Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < jsArray.length(); ++i) {

                                ContentValues cv = new ContentValues();

                                JSONObject jsObject = jsArray.getJSONObject(i);
                                cv.put(DBHandler.COL1, jsObject.getString("name"));
                                cv.put(DBHandler.COL2, jsObject.getString("vicinity"));
                                cv.put(DBHandler.COL3, getIntent().getStringExtra("type"));

                                JSONObject jsonObject1 = jsObject.getJSONObject("geometry");
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("location");
                                cv.put(DBHandler.COL4, jsonObject2.getDouble("lat"));
                                cv.put(DBHandler.COL5, jsonObject2.getDouble("lng"));

                                dbHandler.addRow(cv);
                            }

                            setPlaces();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null)
                            Toast.makeText(Places.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest1);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        double x = lati[position];
        double y = longi[position];
        String s = names[position];
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("lati", x);
        intent.putExtra("longi",y);
        intent.putExtra("title", s);
        startActivity(intent);
    }

    public class Custom extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length ;
        }

        @Override
        public Object getItem(int position) {
            return "hello";
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.activity_custom, parent, false);
            TextView namesOf = (TextView) convertView.findViewById(R.id.namesOf);
            TextView addressOf = (TextView) convertView.findViewById(R.id.addressOf);

            namesOf.setTypeface(ttf);
            addressOf.setTypeface(ttf);

            namesOf.setText(names[position]);
            addressOf.setText(add[position]);
            return convertView;
        }
    }

    @Override
    public void onStart() {

        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Places Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://sujeet.traveller_guide/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {

        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Places Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://sujeet.traveller_guide/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.float_menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.action_settings :
                return true;
            case R.id.action_refresh :
                getPlaces();
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }
}
