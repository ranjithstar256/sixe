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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gaayathri.a6eskills.R;
import com.example.gaayathri.a6eskills.SingleTon;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhoneNumberActivity extends AppCompatActivity {

//    CountryCodePicker ccp;
//    EditText phoneNo;
//    Button sendOtp;
   //String countryCode;
    //String phone; PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
////    FirebaseAuth auth;
    String fullNumber;
    OkHttpClient client;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    String whatsappFlag;
    int countryCode;
    JSONObject jsonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_phone_number);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneNumberActivity.super.onBackPressed();
            }
        });

        client = new OkHttpClient();

       Button sendOtp = findViewById(R.id.btnSendOtp);

       sendOtp.setOnClickListener(v -> {

            CountryCodePicker ccpDialog = findViewById(R.id.ccpDialog);
            EditText et_phoneno = findViewById(R.id.et_phoneno);
            String phoneNumber = et_phoneno.getText().toString();

            if (!phoneNumber.equals("")){
                CheckBox whatsapp = findViewById(R.id.whatsappFlag);
                if (whatsapp.isChecked()) {
                    whatsappFlag = "Y";
                } else {
                    whatsappFlag = "N";
                }

                sharedpreferences = PhoneNumberActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("whatsappFlag", whatsappFlag);
                editor.apply();

                countryCode = ccpDialog.getSelectedCountryCodeAsInt();
                fullNumber = countryCode + phoneNumber;

                Context cont = PhoneNumberActivity.this;
                PhoneNumberActivity.AsyncTaskRunner runner = new PhoneNumberActivity.AsyncTaskRunner(cont, countryCode, phoneNumber);
                runner.execute();
                //checknum();


            } else {
               // Toast.makeText(PhoneNumberActivity.this, "Kindly Enter Phone Number !", Toast.LENGTH_LONG).show();

                new AlertDialog.Builder(PhoneNumberActivity.this)
                        .setMessage("Invalid Phone Number..!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void checknum(){
            ProgressDialog progressDialog = new ProgressDialog(PhoneNumberActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ///progressDialog.dismiss();
//                }
//            }, 3000);
            new Thread() {
                public void run() {
                    try {
                        RequestBody formBody = new FormBody.Builder()
                                .add("name", "th")
                                .add("email", "fgh")
                                .add("city", "fgh")
                                .add("phone", fullNumber)
                                .add("whatsappFlag", "true")
                                .add("notinterviewfor", "rg")
                                .add("skills", "29,28,39,41")
                                .add("resume", "hfgghfv")
                                .add("password", "dkjhjkfgh")
                                .add("countrycode", String.valueOf("91"))
                                .add("resumeurl", "jjhjyhlkjlkk")
                                .add("profilePicDownloadUrl", "")
                                .add("company", "jht")
                                .add("profilepicurl", "")
                                .add("blackCompanies", "")
                                .add("level", "srdev,srdev")
                                .add("profilepicfilename", "")
                                .build();

                        final Request request = new Request.Builder()
                                .url(SingleTon.BASE_URL + "api/v1/user/signup/submit")
                                .post(formBody)
                                .addHeader("cache-control", "no-cache")
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                String mMessage = e.getMessage();
                                Log.w("failure Response", mMessage);
                                PhoneNumberActivity.this.runOnUiThread(() -> Toast.makeText(PhoneNumberActivity.this, "Technical Error.Kindly,Contact support !", Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {


                                String mMessage = response.body().string();
                                Log.i("acyutalkh",mMessage);
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject json = new JSONObject(mMessage);
                                        if (json.getInt("code") == 1001) {
                                            //Alert here

                                            PhoneNumberActivity.this.runOnUiThread(() ->
                                                    new AlertDialog.Builder(PhoneNumberActivity.this)
                                                            .setMessage(json.optString("message"))
                                                            .setCancelable(false)
                                                            .setPositiveButton("Home", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                    PhoneNumberActivity.this.runOnUiThread(() -> Toast.makeText(PhoneNumberActivity.this, "Number Already Exists!", Toast.LENGTH_SHORT).show());
                                                                    Intent homeintent = new Intent(PhoneNumberActivity.this, LoginActivity.class);
                                                                    startActivity(homeintent);
                                                                    PhoneNumberActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                                                }
                                                            })
                                                            .show());
                                        } else {
                                            Intent intent = new Intent(PhoneNumberActivity.this, OTPEntryActivity.class);
                                            intent.putExtra("fullPhoneNumber", fullNumber);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                        }
                                   } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });

                    } catch (Exception e) {
                        Log.e("tag", e.getMessage());
                    }
                    // dismiss the progress dialog
                }
            }.start();
        }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;
        Context mContext;
        View rootView;
        String phone;
        int countryCode;


        public AsyncTaskRunner(Context context, int countryCode, String phone) {
            this.mContext = context;
            this.rootView = rootView;
            this.phone = phone;
            this.countryCode = countryCode;
            progressDialog = new ProgressDialog(PhoneNumberActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                //String fullPhoneNumber = countryCode + phone;
                // create your json here
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("phone", phone);
                    jsonObject.put("countrycode", countryCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->

                            new AlertDialog.Builder(PhoneNumberActivity.this)
                                    .setMessage(jsonProfile.optString(SingleTon.TECHNICAL_ERROR))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show());

                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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
                    Integer code = jsonProfile.getInt("code");
                    //String passwordchangekey = jsonProfile.getString("passwordchangekey");

                    //runOnUiThread(() -> Toast.makeText(mContext, "Code" + code, Toast.LENGTH_SHORT).show());

                    //runOnUiThread(() ->Toast.makeText(PhoneNumberActivity.this, resStr, Toast.LENGTH_LONG).show());                    String testString = jsonProfile.toString();
                    if (code==1004) {
                        //                        String passwordchangekey = jsonProfile.getString("passwordchangekey");
//                        sharedpreferences = PhoneNumberActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putString("passwordchangekey", passwordchangekey);
//                        editor.apply();
//
//                        Intent intent = new Intent(PhoneNumberActivity.this, ForgotPasswordOTPEntryActivity.class);
//                        intent.putExtra("fullPhoneNumber", fullPhoneNumber);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        new AlertDialog.Builder(PhoneNumberActivity.this)
                                .setMessage("Mobile Number Already Registered..!")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //PhoneNumberActivity.this.runOnUiThread(() -> Toast.makeText(PhoneNumberActivity.this, "Number Already Exists!", Toast.LENGTH_SHORT).show());
                                        Intent homeintent = new Intent(PhoneNumberActivity.this, LoginActivity.class);
                                        startActivity(homeintent);
                                        PhoneNumberActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                    }
                                })
                                .show();

                    } else {
                        runOnUiThread(() ->{// Toast.makeText(

                        //  PhoneNumberActivity.this, "Mobile number not registered", Toast.LENGTH_LONG).show()
                        Intent intent = new Intent(PhoneNumberActivity.this, OTPEntryActivity.class);
                        intent.putExtra("fullPhoneNumber", fullNumber);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);


                    });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    // runOnUiThread(() -> Toast.makeText(PhoneNumberActivity.this, "Check credentials and Internet Connection..!", Toast.LENGTH_LONG).show());
                } catch (JSONException ej) {
                    ej.printStackTrace();
                }

            } catch (Exception e) {
                Log.e("tag", e.getMessage());
                runOnUiThread(() -> Toast.makeText(PhoneNumberActivity.this, e.toString(), Toast.LENGTH_LONG).show());
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
            //progressDialog = ProgressDialog.show(PhoneNumberActivity.this, "Loading", "Please wait");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}
