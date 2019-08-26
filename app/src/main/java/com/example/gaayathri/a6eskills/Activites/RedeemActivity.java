package com.example.gaayathri.a6eskills.Activites;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaayathri.a6eskills.R;
import com.example.gaayathri.a6eskills.SingleTon;
import com.example.gaayathri.a6eskills.adapter.CustomExpandableListAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RedeemActivity extends AppCompatActivity {

    Dialog redeemDialog;
    OkHttpClient client;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    JSONArray expandableListDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        Window window = RedeemActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(RedeemActivity.this, R.color.colorPrimary));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Redeem");

        redeemDialog = new Dialog(RedeemActivity.this);
        redeemDialog.setContentView(R.layout.dialog_redeem);

        expandableListView = (ExpandableListView) findViewById(R.id.el_expendable_list);

        TextView earnedPoints = findViewById(R.id.earnedPoints);
        int totalEarnedPoints = Integer.valueOf(earnedPoints.getText().toString());

        Button btnNewRequest = findViewById(R.id.btnNewRequest);
        btnNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemDialog.show();
            }
        });


        Button btnRedeem = redeemDialog.findViewById(R.id.btnRedeem);
        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et_pointsToRedeem = redeemDialog.findViewById(R.id.et_pointsToRedeem);
                Double pointsToRedeem = Double.valueOf(et_pointsToRedeem.getText().toString());

                client = new OkHttpClient();

                new Thread() {

                    public void run() {

                        try {
                            JSONObject jsonobject = new JSONObject();

                            try {
                                jsonobject.put("amount", pointsToRedeem);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            SharedPreferences sharedpreferences = getSharedPreferences("mypref", 0); // 0 - for private mode
                            String secretkey = sharedpreferences.getString("secretkey", "");

                            //OkHttpClient client = new OkHttpClient();
                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            // put your json here
                            RequestBody body = RequestBody.create(JSON, jsonobject.toString());
                            Request profilerequest = new Request.Builder()
                                    .url(SingleTon.BASE_URL + "/api/v1/user/redeem/new/request")
                                    .get()
                                    .addHeader("apikey", secretkey).post(body)
                                    .build();

                            Response profileresponse = client.newCall(profilerequest).execute();
                            String serverResponse = profileresponse.body().string();

                            JSONObject profilejson = new JSONObject(serverResponse);
                            final Integer profilecode = profilejson.getInt("code");

                            if (profilecode == 1001) {
                                runOnUiThread(() -> {
                                    try {
                                        Toast.makeText(RedeemActivity.this, profilejson.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                });
                            }

                            if (profilecode == 1004) {

                                runOnUiThread(() -> {
                                    try {
                                        Toast.makeText(RedeemActivity.this, profilejson.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                });
                                /*Intent homeintent = new Intent(RedeemActivity.this, LoginActivity.class);
                                startActivity(homeintent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();*/
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            //runOnUiThread(() -> Toast.makeText(RedeemActivity.this, e.toString(), Toast.LENGTH_LONG).show());
                        }

                    }
                }.start();

                /*if (pointsToRedeem > totalEarnedPoints) {
                    Toast.makeText(RedeemActivity.this, "Requested amount cannot be greater than Earned Points", Toast.LENGTH_LONG).show();
                }*/
            }
        });

        Button btnCancel = redeemDialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemDialog.dismiss();
            }
        });

//Get Existing Transaction
        getTxhistory();

    }

    private void getTxhistory() {

        OkHttpClient client = new OkHttpClient();

        new Thread() {

            public void run() {

                try {
                    JSONObject jsonobject = new JSONObject();

                    SharedPreferences sharedpreferences = getSharedPreferences("mypref", 0); // 0 - for private mode
                    String secretkey = sharedpreferences.getString("secretkey", "");

                    //OkHttpClient client = new OkHttpClient();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    // put your json here
                    RequestBody body = RequestBody.create(JSON, jsonobject.toString());
                    Request profilerequest = new Request.Builder()
                            .url(SingleTon.BASE_URL + "/api/v1/user/redeem/list")
                            .get()
                            .addHeader("apikey", secretkey)
                            .build();

                    Response profileresponse = client.newCall(profilerequest).execute();
                    String serverResponse = profileresponse.body().string();

                    JSONObject txlist = new JSONObject(serverResponse);
                    final Integer profilecode = txlist.getInt("code");

                    if (profilecode == 1001) {
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(RedeemActivity.this, txlist.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    if (profilecode == 1004) {

                        /*runOnUiThread(() -> {
                            try {
                                Toast.makeText(RedeemActivity.this, txlist.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });*/

                        runOnUiThread(() -> {
                            populateExpendablelist(txlist);
                        });

                        //Create List Here
                        Log.v("cvar-logger", txlist.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(RedeemActivity.this, "Technical Error.Contact Support.!", Toast.LENGTH_LONG).show());
                }
            }
        }.start();
    }


    void populateExpendablelist(JSONObject obj)  {


        //ExpendableList
        try {
            expandableListDetail = obj.getJSONArray("data");

        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setEmptyView(findViewById(R.id.emptyView));
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        groupPosition  + "- List Expanded.",
//                        Toast.LENGTH_LONG).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        groupPosition + " List Collapsed.",
//                        Toast.LENGTH_LONG).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

               // Toast.makeText(RedeemActivity.this, "clciked", Toast.LENGTH_LONG).show();
                return false;
            }
        });

    } catch (JSONException e) {
        e.printStackTrace();
    }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}





