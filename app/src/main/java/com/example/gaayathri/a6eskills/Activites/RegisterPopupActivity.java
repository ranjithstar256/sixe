package com.example.gaayathri.a6eskills.Activites;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gaayathri.a6eskills.R;
import com.example.gaayathri.a6eskills.SingleTon;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterPopupActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    EditText phoneNo;
    Button sendOtp;

    String countryCode;
    String phone;
    String fullPhoneNumber;

    OkHttpClient client;
    SharedPreferences sharedpreferences;

    JSONObject jsonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_popup);

        client = new OkHttpClient();

        ccp = findViewById(R.id.ccpDialog);
        phoneNo = findViewById(R.id.et_phoneno);
        sendOtp = findViewById(R.id.btnSendOtp);

        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            /*Intent homeintent = new Intent(RegisterPopupActivity.this, LoginActivity.class);
            startActivity(homeintent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);*/
            super.onBackPressed();
        });


        sendOtp.setOnClickListener(v -> {

            countryCode = ccp.getSelectedCountryCode();
            phone = phoneNo.getText().toString();
            Context cont = RegisterPopupActivity.this;


            RegisterPopupActivity.AsyncTaskRunner runner = new RegisterPopupActivity.AsyncTaskRunner(cont, countryCode, phone);
            runner.execute();

        });

    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;
        private Context mContext;
        private View rootView;
        private String phone;
        private String countryCode;
        private String password;

        public AsyncTaskRunner(Context context, String countryCode, String phone) {
            this.mContext = context;
            this.rootView = rootView;
            this.phone = phone;
            this.countryCode = countryCode;
            progressDialog = new ProgressDialog(RegisterPopupActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                String fullPhoneNumber = countryCode + phone;

                // create your json here
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("phone", phone);
                    jsonObject.put("countrycode", countryCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->

                            new AlertDialog.Builder(RegisterPopupActivity.this)
                                    .setMessage(jsonProfile.optString(SingleTon.TECHNICAL_ERROR))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show());

                }

                //OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                // put your json here
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(SingleTon.BASE_URL + "api/v1/user/login/request/changepassword")
                        .post(body)
                        .build();

                Response response = null;

                try {
                    response = client.newCall(request).execute();
                    String resStr = response.body().string();

                    Log.v("log-def", resStr);


                    jsonProfile = new JSONObject(resStr);

                    String testString = jsonProfile.toString();

                    Integer code = jsonProfile.getInt("code");
                    //String passwordchangekey = jsonProfile.getString("passwordchangekey");

                    //runOnUiThread(() -> Toast.makeText(mContext, "Code" + code, Toast.LENGTH_SHORT).show());

                    //runOnUiThread(() ->Toast.makeText(RegisterPopupActivity.this, resStr, Toast.LENGTH_LONG).show());

                    if (code.equals(1004)) {

                        String passwordchangekey = jsonProfile.getString("passwordchangekey");

                        sharedpreferences = RegisterPopupActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("passwordchangekey", passwordchangekey);
                        editor.apply();

                        Intent intent = new Intent(RegisterPopupActivity.this, ForgotPasswordOTPEntryActivity.class);
                        intent.putExtra("fullPhoneNumber", fullPhoneNumber);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);


                    } else {
                        runOnUiThread(() ->// Toast.makeText(


                                //  RegisterPopupActivity.this, "Mobile number not registered", Toast.LENGTH_LONG).show()

                                new AlertDialog.Builder(RegisterPopupActivity.this)
                                        .setMessage("Mobile Number Not Registered..!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    // runOnUiThread(() -> Toast.makeText(RegisterPopupActivity.this, "Check credentials and Internet Connection..!", Toast.LENGTH_LONG).show());
                } catch (JSONException ej) {
                    ej.printStackTrace();
                }

            } catch (Exception e) {
                Log.e("tag", e.getMessage());
                runOnUiThread(() -> Toast.makeText(RegisterPopupActivity.this, e.toString(), Toast.LENGTH_LONG).show());
            }
            return "good";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //progressDialog.dismiss();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            //progressDialog = ProgressDialog.show(RegisterPopupActivity.this, "Loading", "Please wait");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

}