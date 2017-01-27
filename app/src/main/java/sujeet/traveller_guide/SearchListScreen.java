package sujeet.traveller_guide;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchListScreen extends AppCompatActivity implements View.OnClickListener {

    private TextView tvRestaurant;
    private TextView tvPolice;
    private TextView tvRailway;
    private TextView tvMovie;
    private TextView tvPark;
    private TextView tvAtm;
    private TextView tvAirport;
    private TextView tvHospital;

    private LinearLayout lvRestaurant;
    private LinearLayout lvPolice;
    private LinearLayout lvRailway;
    private LinearLayout lvMovie;
    private LinearLayout lvPark;
    private LinearLayout lvAtm;
    private LinearLayout lvAirport;
    private LinearLayout lvHospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar_2);
        setSupportActionBar(toolbar);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        tvRestaurant = (TextView) findViewById(R.id.tRe);
        tvPolice = (TextView) findViewById(R.id.tPo);
        tvAirport = (TextView) findViewById(R.id.tAi);
        tvAtm = (TextView) findViewById(R.id.tAt);
        tvMovie = (TextView) findViewById(R.id.tMo);
        tvHospital = (TextView) findViewById(R.id.tHo);
        tvPark = (TextView) findViewById(R.id.tPa);
        tvRailway = (TextView) findViewById(R.id.tRa);

        lvRestaurant = (LinearLayout) findViewById(R.id.restaurant);
        lvPolice = (LinearLayout) findViewById(R.id.police);
        lvAirport = (LinearLayout) findViewById(R.id.airport);
        lvAtm = (LinearLayout) findViewById(R.id.atm);
        lvMovie = (LinearLayout) findViewById(R.id.movie);
        lvHospital = (LinearLayout) findViewById(R.id.hospital);
        lvPark = (LinearLayout) findViewById(R.id.park);
        lvRailway = (LinearLayout) findViewById(R.id.railway);

        lvRailway.setOnClickListener(this);
        lvPark.setOnClickListener(this);
        lvAirport.setOnClickListener(this);
        lvPolice.setOnClickListener(this);
        lvRestaurant.setOnClickListener(this);
        lvAtm.setOnClickListener(this);
        lvHospital.setOnClickListener(this);
        lvMovie.setOnClickListener(this);

        Typeface type = Typeface.createFromAsset(getAssets(), "fonta.otf");
        tvRestaurant.setTypeface(type);
        tvRailway.setTypeface(type);
        tvMovie.setTypeface(type);
        tvAirport.setTypeface(type);
        tvPark.setTypeface(type);
        tvHospital.setTypeface(type);
        tvAtm.setTypeface(type);
        tvPolice.setTypeface(type);
    }

    @Override
    public void onClick(View v) {

        String s = "";
        Intent intent;
        boolean flag = false;

        switch (v.getId()) {
            case R.id.railway:
                s = "train_station";
                flag = true;
                break;
            case R.id.restaurant:
                s = "restaurant";
                flag = true;
                break;
            case R.id.park:
                s = "park";
                flag = true;
                break;
            case R.id.atm:
                s = "atm";
                flag = true;
                break;
            case R.id.hospital:
                s = "hospital";
                flag = true;
                break;
            case R.id.police:
                s = "police";
                flag = true;
                break;
            case R.id.movie:
                s = "movie_theater";
                flag = true;
                break;
            case R.id.airport:
                s = "airport";
                flag = true;
                break;
        }

        if(!flag)
            return;

        intent = new Intent(this, Places.class);
        intent.putExtra("type", s);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.float_menu_2,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings :
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }
}
