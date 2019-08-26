package com.example.gaayathri.a6eskills.Activites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.concurrent.TimeUnit;

public class ForgotPasswordOTPEntryActivity extends AppCompatActivity {

    FirebaseAuth auth;

    private String verificationCode;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    private Pinview pinview;

    String fullPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password_otpentry);

        StartFirebaseLogin();

        Button btnVerify = findViewById(R.id.btnVerify);
        TextView resend = findViewById(R.id.resend);

        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            /*Intent homeintent = new Intent(RegisterPopupActivity.this, LoginActivity.class);
            startActivity(homeintent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);*/
            super.onBackPressed();
        });

        String fullPhoneNumber = getIntent().getExtras().getString("fullPhoneNumber");

        //fullPhoneNumber = "918124547908";

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                fullPhoneNumber,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                ForgotPasswordOTPEntryActivity.this,        // Activity (for callback binding)
                mCallback);                      // OnVerificationStateChangedCallbacks

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinview = findViewById(R.id.txt_pin_entry);
                String Otp = pinview.getValue();

                if (pinview.getValue().equals("000000")) {

                    Intent intent = new Intent(ForgotPasswordOTPEntryActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);

                } else {
                    new AlertDialog.Builder(ForgotPasswordOTPEntryActivity.this)
                            .setMessage("Invalid OTP..!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }


            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        fullPhoneNumber,                     // Phone number to verify
                        60,                           // Timeout duration
                        TimeUnit.SECONDS,                // Unit of timeout
                        ForgotPasswordOTPEntryActivity.this,        // Activity (for callback binding)
                        mCallback);                      // OnVerificationStateChangedCallbacks
            }
        });


    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //startActivity(new Intent(VerificationActivity.this,SignedIn.class));
                            //finish();
                            // Toast.makeText(ForgotPasswordOTPEntryActivity.this,"Verification Successful",Toast.LENGTH_LONG).show();

                            new AlertDialog.Builder(ForgotPasswordOTPEntryActivity.this)
                                    .setMessage("Success..!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();


                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(ForgotPasswordOTPEntryActivity.this, ChangePasswordActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                        } else {
                            //Toast.makeText(ForgotPasswordOTPEntryActivity.this,"Incorrect OTP",Toast.LENGTH_LONG).show();


                            new AlertDialog.Builder(ForgotPasswordOTPEntryActivity.this)
                                    .setMessage("Invalid OTP..!")
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

    private void StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                /*Toast.makeText(ForgotPasswordOTPEntryActivity.this,"OTP Verified !",Toast.LENGTH_LONG).show();

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ForgotPasswordOTPEntryActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);*/

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                //    Toast.makeText(ForgotPasswordOTPEntryActivity.this,"Verification Failed: " + e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                // Toast.makeText(ForgotPasswordOTPEntryActivity.this,"OTP Sent !",Toast.LENGTH_LONG).show();


            }
        };
    }
}
