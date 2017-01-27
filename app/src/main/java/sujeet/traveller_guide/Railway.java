package sujeet.traveller_guide;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Railway extends AppCompatActivity implements View.OnClickListener {

    private TextView tvPNR,tvStnCode,tvTrainNo;
    private LinearLayout pnrLayout,liveLayout,layoutStnCode,layoutTrNo;
    private TextView tvTBS,tvStatus;
    private LinearLayout TBSLayout;
    private EditText pnr,stn1,stn2,trainName_No,stnCode,TrNo;
    private Button getPnr,findTrains,spot,stCode,TrainNo;
    private String url="http://api.railwayapi.com/pnr_status/pnr/";
    private String key="h62q77e2";
    private String url1="http://api.railwayapi.com/suggest_station/name/";
    private String url2="http://api.railwayapi.com/between/source/";
    private String url_spot1="http://api.railwayapi.com/suggest_train/trains/";
    private String url_spot2="http://api.railwayapi.com/live/train/";
    private long  pnrNO;
    private TextView pnr_result,tbsResult,liveResult,code_result,No_result;
    String trains[];
    String final1;
    String final2;
    String final3,final4,final5;
    String station1[];
    String station2;
    String code1[],code2;
    String trainNumber[],trainName[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_railway);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar_2);
        setSupportActionBar(toolbar);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        pnr=(EditText) findViewById(R.id.pnr);
        getPnr= (Button) findViewById(R.id.btnPNR);
        getPnr.setOnClickListener(this);
        pnr_result= (TextView) findViewById(R.id.tvPNRresult);

        stnCode= (EditText) findViewById(R.id.stCode);
        stCode= (Button) findViewById(R.id.btnStncode);
        stCode.setOnClickListener(this);
        code_result= (TextView) findViewById(R.id.tvStnResult);

        TrNo= (EditText) findViewById(R.id.Trno);
        TrainNo= (Button) findViewById(R.id.btnTrno);
        TrainNo.setOnClickListener(this);
        No_result= (TextView) findViewById(R.id.tvTrResult);


        stn1= (EditText) findViewById(R.id.station1);
        stn2= (EditText) findViewById(R.id.station2);
        findTrains= (Button) findViewById(R.id.btnTBS);
        findTrains.setOnClickListener(this);
        tbsResult= (TextView) findViewById(R.id.tvTBSresult);

        trainName_No= (EditText) findViewById(R.id.live);
        spot= (Button) findViewById(R.id.btnLIVE);
        liveResult = (TextView) findViewById(R.id.tvLIVEresult);
        spot.setOnClickListener(this);

        tvPNR = (TextView) findViewById(R.id.tvPNR);
        pnrLayout = (LinearLayout) findViewById(R.id.layoutPNR);
        tvPNR.setOnClickListener(this);

        tvStnCode= (TextView) findViewById(R.id.stationcode);
        layoutStnCode= (LinearLayout) findViewById(R.id.layoutstnCode);
        tvStnCode.setOnClickListener(this);

        tvTrainNo= (TextView) findViewById(R.id.trainNo);
        layoutTrNo= (LinearLayout) findViewById(R.id.layouttrainNo);
        tvTrainNo.setOnClickListener(this);

        tvTBS = (TextView) findViewById(R.id.tvTBS);
        TBSLayout = (LinearLayout) findViewById(R.id.layoutTBS);
        tvTBS.setOnClickListener(this);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        liveLayout = (LinearLayout) findViewById(R.id.layoutLIVE);
        tvStatus.setOnClickListener(this);
    }

    private void getPNRstatus() {

        pnrNO = Long.parseLong((pnr.getText()).toString());

        StringBuilder sb = new StringBuilder("");
        sb.append(url);
        sb.append(pnrNO);
        sb.append("/apikey/"+key+"/");

        JsonObjectRequest jsonobjectRequest=new JsonObjectRequest(Request.Method.GET, sb.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String trainNo = response.getString("train_name");
                    String pnr_no = response.getString("pnr");
                    String cLass = response.getString("class");
                    String dateOfj = response.getString("doj");
                    String totalPass = response.getString("total_passengers");
                    String chart = response.getString("chart_prepared");
                    JSONArray passArray = response.getJSONArray("passengers");
                    StringBuilder sbb = new StringBuilder("");
                    sbb.append("Train No:-" + trainNo + "\n");
                    sbb.append("PNR:-" + pnr_no + "\n");
                    sbb.append("Date of Journey:-" + dateOfj + "\n");
                    sbb.append("Class:-" + cLass + "\n");
                    sbb.append("Chart Prepared:-" + chart + "\n");
                    sbb.append("No. of Passengers:-" + totalPass + "\n");
                    sbb.append("Details of passengers:-\n");
                    for(int i=0;i<passArray.length();i++)
                    {
                        sbb.append("no:-" + passArray.getJSONObject(i).getInt("no")+"\n");
                        sbb.append("Booking status:-" + passArray.getJSONObject(i).getString("booking_status")+"\n");
                        sbb.append("Current Status" + passArray.getJSONObject(i).getString( "current_status")+"\n");
                    }
                    pnr_result.setText(sbb.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Railway.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonobjectRequest);
    }

    private void changeVisibility(View view) {

        if(view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvPNR:
                changeVisibility(pnrLayout);
                TBSLayout.setVisibility(View.GONE);
                liveLayout.setVisibility(View.GONE);
                layoutStnCode.setVisibility(View.GONE);
                layoutTrNo.setVisibility(View.GONE);
                break;
            case R.id.tvTBS:
                changeVisibility(TBSLayout);
                 pnrLayout.setVisibility(View.GONE);
                liveLayout.setVisibility(View.GONE);
                layoutStnCode.setVisibility(View.GONE);
                layoutTrNo.setVisibility(View.GONE);
                break;
            case R.id.tvStatus:
                changeVisibility(liveLayout);
                pnrLayout.setVisibility(View.GONE);
                TBSLayout.setVisibility(View.GONE);
                layoutStnCode.setVisibility(View.GONE);
                layoutTrNo.setVisibility(View.GONE);

                break;
            case R.id.stationcode :
                changeVisibility(layoutStnCode);
                pnrLayout.setVisibility(View.GONE);
                TBSLayout.setVisibility(View.GONE);
                liveLayout.setVisibility(View.GONE);
                layoutTrNo.setVisibility(View.GONE);
                break;
            case R.id.trainNo :
                changeVisibility(layoutTrNo);
                pnrLayout.setVisibility(View.GONE);
                TBSLayout.setVisibility(View.GONE);
                layoutStnCode.setVisibility(View.GONE);
                liveLayout.setVisibility(View.GONE);
                break;
            case R.id.btnPNR:
                getPNRstatus();
                break;
            case R.id.btnTBS:
                getTBS();
                break;
            case R.id.btnLIVE :
                getLiveStatus();
                break;
            case R.id.btnStncode :
                getStationCode();
                break;
            case R.id.btnTrno :
                getTrainNo();

        }
    }

    private void finalUrl1_2() {

        String stationName=(stnCode.getText()).toString();
        StringBuilder sb=new StringBuilder("");
        sb.append(url1);
        sb.append(stationName);
        sb.append("/apikey/");
        sb.append(key);
        sb.append("/");
        final1=sb.toString();

    }

    private void  finalUrl_3() {

        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdfDate=new SimpleDateFormat("dd-MM");
        String currentDate= sdfDate.format(cal.getTime());
        StringBuilder sb3=new StringBuilder("");
        sb3.append(url2);
        sb3.append(stn1.getText().toString());
        sb3.append("/dest/");
        sb3.append(stn2.getText().toString());
        sb3.append("/date/");
        sb3.append(currentDate);
        sb3.append("/apikey/");
        sb3.append(key);
        sb3.append("/");
        final3=sb3.toString();
        //tbsResult.setText(currentDate);
    }
    private void finalspot1() {

        String tr_N_N=(TrNo.getText()).toString();
        StringBuilder sb= new StringBuilder("");
        sb.append(url_spot1);
        sb.append(tr_N_N);
        sb.append("/apikey/");
        sb.append(key);
        sb.append("/");
        final4=sb.toString();
    }
    private void finalspot2() {

        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdfDate=new SimpleDateFormat("yyyyMMdd");
        String currentDate=sdfDate.format(cal.getTime());
        StringBuilder sb= new StringBuilder("");
        sb.append(url_spot2);
        sb.append(trainName_No.getText().toString());
        sb.append("/doj/");
        sb.append(currentDate);
        sb.append("/apikey/");
        sb.append(key);
        sb.append("/");
        final5=sb.toString();
    }
    private void getStationCode()
    {
        finalUrl1_2();

        JsonObjectRequest jsobj1=new JsonObjectRequest(Request.Method.GET, final1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsarray = response.getJSONArray("station");
                            station1=new String[jsarray.length()];
                            code1=new String[jsarray.length()];
                            for(int i=0;i<jsarray.length();i++)
                            {
                                station1[i] = (jsarray.getJSONObject(i)).getString("fullname");
                                code1[i]=(jsarray.getJSONObject(i)).getString("code").toLowerCase();
                            }
                            StringBuilder sb =new StringBuilder("");
                            for(int i=0;i<jsarray.length();i++)
                            {
                                sb.append(station1[i]);
                                sb.append(System.getProperty("line.separator"));
                                sb.append(code1[i]);
                                sb.append(System.getProperty("line.separator"));
                            }
                            code_result.setText(sb.toString());
                            Toast.makeText(Railway.this, "response1", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsobj1);
    }
    private void getTrainNo()
    {
        finalspot1();

        JsonObjectRequest jsobj1 = new JsonObjectRequest(Request.Method.GET, final4, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsarray = response.getJSONArray("trains");
                            trainName=new String[jsarray.length()];
                            trainNumber=new String[jsarray.length()];

                            for(int i=0;i<jsarray.length();i++)
                            {
                                trainNumber[i] = (jsarray.getJSONObject(i)).getString("number");
                                trainName[i]=(jsarray.getJSONObject(i)).getString("name");
                            }
                            StringBuilder sb=new StringBuilder("");
                            for(int i=0;i<jsarray.length();i++)
                            {
                                sb.append(trainName[i]);
                                sb.append(System.getProperty("line.separator"));
                                sb.append(trainNumber[i]);
                                sb.append(System.getProperty("line.separator"));
                            }
                            No_result.setText(sb.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsobj1);
    }

    private void getTBS()
    {
        finalUrl_3();
        final JsonObjectRequest jsobj3=new JsonObjectRequest(Request.Method.GET, final3, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(Railway.this, "response3", Toast.LENGTH_SHORT).show();
                            JSONArray jarray = response.getJSONArray("train");
                            trains = new String[jarray.length()];
                            for (int i=0 ; i < jarray.length(); i++)
                            {
                                JSONObject train=jarray.getJSONObject(i);
                                String trainName=train.getString("name");
                                trains[i]=trainName;
                            }
                            StringBuilder sb=new StringBuilder("");
                            for (int i=0;i<jarray.length();i++)
                            {
                                sb.append(trains[i]);
                                sb.append(System.getProperty("line.separator"));
                            }
                            //sb.append(" "+jarray.length());
                            tbsResult.setText(sb.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Railway.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsobj3);
    }






        private void getLiveStatus() {
            finalspot2();
            final JsonObjectRequest jsobj2 = new JsonObjectRequest(Request.Method.GET, final5, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String position = response.getString("position");
                                liveResult.setText(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            MySingleton.getInstance(this).addToRequestQueue(jsobj2);
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
