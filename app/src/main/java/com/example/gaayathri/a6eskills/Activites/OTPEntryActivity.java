package com.example.gaayathri.a6eskills.Activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaayathri.a6eskills.R;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


public class OTPEntryActivity extends AppCompatActivity {

    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    String fullPhoneNumber;

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";

    private Pinview pinview;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otpentry);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTPEntryActivity.super.onBackPressed();
            }
        });

        pinview= findViewById(R.id.txt_pin_entry);

       // StartFirebaseLogin();

        fullPhoneNumber = getIntent().getExtras().getString("fullPhoneNumber");
        //fullPhoneNumber = "+915812447908";
        Button btnVerify = findViewById(R.id.btnVerify);

  /*      PhoneAuthProvider.getInstance().verifyPhoneNumber(
                fullPhoneNumber,                 // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                OTPEntryActivity.this,    // Activity (for callback binding)
                mCallback);   */                   // OnVerificationStateChangedCallbacks


        TextView tvPhoneNumberInfo = findViewById(R.id.tvPhoneNumberInfo);

        tvPhoneNumberInfo.setText("We have sent OTP to " + fullPhoneNumber);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context cont = OTPEntryActivity.this;
                String Otp = pinview.getValue();

                if (Otp.equals("000000")){

                    sharedpreferences = OTPEntryActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("phoneNo", fullPhoneNumber.substring(2));
                    editor.apply();

                    Intent intent = new Intent(OTPEntryActivity.this, UserMoreDataActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                } else {

                    OTPEntryActivity.AsyncTaskRunner runner = new OTPEntryActivity.AsyncTaskRunner(cont, Otp);
                    runner.execute();

                }

            }
        });

        TextView resend = findViewById(R.id.resend);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OTPEntryActivity.this, "in progress", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //Toast.makeText(OTPEntryActivity.this,"OTP Verified !",Toast.LENGTH_LONG).show();

                sharedpreferences = OTPEntryActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("phoneNo", fullPhoneNumber.substring(2));
                editor.apply();

                /*FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(OTPEntryActivity.this, UserMoreDataActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);*/

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
              ///  Toast.makeText(OTPEntryActivity.this,e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
              //  Toast.makeText(OTPEntryActivity.this,"OTP Sent !",Toast.LENGTH_LONG).show();
            }
        };
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //startActivity(new Intent(VerificationActivity.this,SignedIn.class));
                            //finish();
                            Toast.makeText(OTPEntryActivity.this,"Verification Successful",Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();

                            sharedpreferences = OTPEntryActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("phoneNo", fullPhoneNumber.substring(2));
                            editor.apply();

                            Intent intent = new Intent(OTPEntryActivity.this, UserMoreDataActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);


                        } else {
                            Toast.makeText(OTPEntryActivity.this,"Incorrect OTP",Toast.LENGTH_LONG).show();
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
        private String otp;

        public AsyncTaskRunner(Context context, String otp) {
            this.mContext = context;
            this.rootView = rootView;
            this.phone = phone;
            this.password =password;
            this.otp = otp;
            progressDialog = new ProgressDialog(OTPEntryActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                SigninWithPhone(credential);
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OTPEntryActivity.this, "Check your OTP", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return "good";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            //progressDialog = ProgressDialog.show(OTPEntryActivity.this, "Please wait");
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
