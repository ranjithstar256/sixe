package com.example.gaayathri.a6eskills.Activites;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gaayathri.a6eskills.R;
import com.example.gaayathri.a6eskills.SingleTon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    OkHttpClient client;

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";

    JSONObject jsonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedpreferences = LoginActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
        String loginStatus = sharedpreferences.getString("loginStatus", "0");
        //Toast.makeText(LoginActivity.this, loginStatus, Toast.LENGTH_LONG).show();
        if (loginStatus.equals("1")) {
            Intent homeintent = new Intent(LoginActivity.this, ChipsActivity.class);
            startActivity(homeintent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        } else {
            setContentView(R.layout.activity_login);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            client = new OkHttpClient();

            TextView forgotPassword = findViewById(R.id.forgotPassword);
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterPopupActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }
            });

            Button loginbtn = findViewById(R.id.btnLogin);
            loginbtn.setOnClickListener(v -> {
                Context cont = LoginActivity.this;
                EditText et_phone = findViewById(R.id.et_phone);
                EditText et_password = findViewById(R.id.et_password);
                String phone = et_phone.getText().toString();
                String password = et_password.getText().toString();

                if (phone.equals("") || password.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Toast.makeText(LoginActivity.this, "Kindly Enter Phone number and Password", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setMessage("Kindly Enter Phone number and Password..!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });

                } else {
                    LoginActivity.AsyncTaskRunner runner = new LoginActivity.AsyncTaskRunner(cont, phone, password);
                    runner.execute();
                }

            });

            TextView btnSignup = findViewById(R.id.btnSignup);
            btnSignup.setOnClickListener(v -> {
                Intent homeintent = new Intent(LoginActivity.this, UserDataActivity.class);
                startActivity(homeintent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            });

            Button aboutUs = findViewById(R.id.aboutUs);
            aboutUs.setOnClickListener(v -> {
                Intent aboutusintent = new Intent(LoginActivity.this, AboutUsActivity.class);
                startActivity(aboutusintent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            });
        }

    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;
        private Context mContext;
        private View rootView;
        private String phone;
        private String password;

        public AsyncTaskRunner(Context context, String phone, String password) {
            this.mContext = context;
            this.rootView = rootView;
            this.phone = phone;
            this.password = password;
            progressDialog = new ProgressDialog(LoginActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            // do the background process or any work that takes time to see progress dialog
            int countrycode = Integer.parseInt(GetCountryZipCode());
            //EditText et_phone = findViewById(R.id.et_phone);
            //EditText et_password = findViewById(R.id.et_password);
            //String phone = et_phone.getText().toString();
            //String password = et_password.getText().toString();

            if (phone.trim().equals("") || password.trim().equals("")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(LoginActivity.this, "Empty Phone number or Password..!", Toast.LENGTH_LONG).show();
                        new AlertDialog.Builder(LoginActivity.this)
                                .setMessage("Empty Phone number or Password..!")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });

                //return;
            }

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("phone", phone);
                jsonObject.put("password", password);
                jsonObject.put("countrycode", countrycode);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            // put your json here
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(SingleTon.BASE_URL + "api/v1/user/login/form_submit")
                    .post(body)
                    .build();

            Response response = null;
            //Toast.makeText(LoginActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
            try {
                response = client.newCall(request).execute();
                String resStr = response.body().string();

                Log.v("log-def", resStr);
                //Toast.makeText(LoginActivity.this, "response: " + resStr, Toast.LENGTH_LONG).show();

                jsonProfile = new JSONObject(resStr);
                final Integer code = jsonProfile.getInt("code");

                if (code.equals(1004)) {

                    final String secretkey = jsonProfile.getJSONObject("data").getString("secretkey");

                    //Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();

                    Request profilerequest = new Request.Builder()
                            .url(SingleTon.BASE_URL+"api/v1/user/profile/details")
                            .get()
                            .addHeader("apikey", secretkey)
                            .build();

                    Response profileresponse = client.newCall(profilerequest).execute();
                    String serverResponse = profileresponse.body().string();

                    //Toast.makeText(this, serverResponse, Toast.LENGTH_LONG).show();

                    JSONObject profilejson = new JSONObject(serverResponse);
                    final String profilecode = profilejson.getString("code");

                    if (profilecode.equals("1004")) {

                        String userid = null;
                        String username = null;
                        String primaryemail = null;
                        String profilephone = null;
                        String secondaryemail = null;
                        String profilecountrycode = null;
                        String resumeurl = null;
                        String profilepicurl = null;
                        String city = null;
                        String company = null;

                        try {
                            userid = profilejson.getJSONObject("data").getString("userid");
                            username = profilejson.getJSONObject("data").getString("username");
                            profilephone = profilejson.getJSONObject("data").getString("phone");
                            primaryemail = profilejson.getJSONObject("data").getString("primaryemail");
                            secondaryemail = profilejson.getJSONObject("data").getString("secondaryemail");
                            profilecountrycode = profilejson.getJSONObject("data").getString("countrycode");
                            resumeurl = profilejson.getJSONObject("data").getString("resumeurl");
                            profilepicurl = profilejson.getJSONObject("data").getString("profilepicurl");
                            city = profilejson.getJSONObject("data").getString("city");
                            company = profilejson.getJSONObject("data").getString("company");

                            sharedpreferences = LoginActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("secretkey", secretkey);
                            Log.i("ytdjhgj",secretkey);
                            editor.putString("userid", userid);
                            editor.putString("username", username);
                            editor.putString("phone", profilephone);
                            editor.putString("primaryemail", primaryemail);
                            editor.putString("secondaryemail", secondaryemail);
                            editor.putString("profilecountrycode", profilecountrycode);
                            editor.putString("resumeurl", resumeurl);
                            editor.putString("profilepicurl", profilepicurl);
                            editor.putString("company", company);
                            editor.putString("city", city);
                            editor.putString("loginStatus", "1");
                            editor.apply();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Intent homeintent = new Intent(LoginActivity.this, ChipsActivity.class);
                        startActivity(homeintent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                    }

                }
                if (code.equals(1001)) {

                    runOnUiThread(() ->
                           // Toast.makeText(LoginActivity.this, jsonProfile.optString("message"), Toast.LENGTH_LONG).show()
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage(jsonProfile.optString("message"))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show());
                }

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->



                        //Toast.makeText(LoginActivity.this, "Check Internet Connection..!", Toast.LENGTH_LONG).show());
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage("Check Internet Connection..!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show());




            } catch (JSONException ej) {
                ej.printStackTrace();
            }

           /* try {

            } catch (Exception e) {
                Log.e("tag",e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, e.getMessage() + "fucked", Toast.LENGTH_LONG).show();
                    }
                });
                //runOnUiThread(() -> Toast.makeText(LoginActivity.this, e.getMessage() + "fucked", Toast.LENGTH_LONG).show());
            }*/

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
            //progressDialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please wait");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }


    public String GetCountryZipCode() {

        String CountryID;
        String CountryZipCode = null;

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }

        //String CountryZipCode = "91";

        return CountryZipCode;

    }


}
