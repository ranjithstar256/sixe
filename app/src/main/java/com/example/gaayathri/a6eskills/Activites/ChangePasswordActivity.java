package com.example.gaayathri.a6eskills.Activites;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_password);

        client = new OkHttpClient();

        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            /*Intent homeintent = new Intent(ChangePasswordActivity.this, ForgotPasswordOTPEntryActivity.class);
            startActivity(homeintent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);*/
            super.onBackPressed();
        });

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context cont = ChangePasswordActivity.this;

                EditText et_password = findViewById(R.id.et_password);
                EditText et_passwordconfirm = findViewById(R.id.et_passwordconfirm);

                String password = et_password.getText().toString();
                String confirmPassword = et_passwordconfirm.getText().toString();

                if (password.equals(confirmPassword)) {

                    if(password.equals("") || confirmPassword.equals("") ) {
                        Toast.makeText(ChangePasswordActivity.this, password, Toast.LENGTH_LONG).show();
                    } else {
                        ChangePasswordActivity.AsyncTaskRunner runner = new ChangePasswordActivity.AsyncTaskRunner(cont, password);
                        runner.execute();
                    }
                } else {
                  //  Toast.makeText(ChangePasswordActivity.this, "Password Mismatch", Toast.LENGTH_LONG).show();

                    new AlertDialog.Builder(ChangePasswordActivity.this)
                            .setMessage("Password Mismatch..!")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }
            }
        });
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;
        private Context mContext;
        private View rootView;
        private String phone;
        private String password;

        public AsyncTaskRunner(Context context, String password) {
            this.mContext = context;
            this.rootView = rootView;
            this.phone = phone;
            this.password =password;
            progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                sharedpreferences = ChangePasswordActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                String passwordchangekey = sharedpreferences.getString("passwordchangekey", "");

                // create your json here
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("key", passwordchangekey);
                    jsonObject.put("password", password);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(SingleTon.BASE_URL +"api/v1/user/login/request/changepassword")
                        .put(body)
                        .build();

                Response response = null;

                try {
                    response = client.newCall(request).execute();
                    String resStr = response.body().string();

                    Log.v("log-def",resStr);

                    JSONObject jsonProfile = new JSONObject(resStr);
                    final Integer code = jsonProfile.getInt("code");

                    if (code.equals(1004)) {

                        runOnUiThread(() ->

                                Toast.makeText(ChangePasswordActivity.this, "Password Changed. Kindly Login", Toast.LENGTH_LONG).show());
//
//                        new AlertDialog.Builder(ChangePasswordActivity.this)
//                            .setMessage("New Password Updated.Login Now..!")
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                               public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                           })
//                           .show();
//





                        Intent homeintent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(homeintent);
                        ChangePasswordActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        finish();

                    }
                    if (code.equals(1001)) {


                        runOnUiThread(() ->// Toast.makeText(ChangePasswordActivity.this,jsonProfile.optString("message"), Toast.LENGTH_LONG).show());

                        new AlertDialog.Builder(ChangePasswordActivity.this)
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
                   // runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this, "Check credentials and Internet Connection..!", Toast.LENGTH_LONG).show());
                } catch (JSONException ej) {
                    ej.printStackTrace();
                }
            } catch (Exception e) {
                Log.e("tag",e.getMessage());
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
            //progressDialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please wait");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
