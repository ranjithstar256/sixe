package com.example.gaayathri.a6eskills.Activites;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaayathri.a6eskills.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UserDataActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth auth;
    String verificationCode;
    String name, email, password, phoneNo, whatsappFlag;
    int countryCode;
    public static final int MY_REQUEST_READ_GALLERY = 13;
    public static final int MY_REQUEST_WRITE_GALLERY = 14;
    public static final int MY_REQUEST_GALLERY = 15;
    TextView verify;
    String verified;
    Uri uriObject;
    public File filen = null;
    Dialog phoneVerificationDialog;
    CircularImageView profilePic,ivcam;
    StorageReference mStorageReference;
    String downloadUrl;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_data);

        EditText et_name = findViewById(R.id.et_name);
        EditText et_email = findViewById(R.id.et_email);
        EditText et_password = findViewById(R.id.et_password);
        progressDialog = new ProgressDialog(UserDataActivity.this);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        Button btnNext = findViewById(R.id.btnNext);
        profilePic = findViewById(R.id.profilePic);
        ivcam = findViewById(R.id.iv_camera);
        profilePic.setOnClickListener(v -> checkPermissionRG());
        ivcam.setOnClickListener(v -> checkPermissionRG());
        sharedpreferences = UserDataActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
        verified = sharedpreferences.getString("verified", "0");
        name = sharedpreferences.getString("name", "");
        email = sharedpreferences.getString("email", "");
        password = sharedpreferences.getString("password", "");
        phoneNo = sharedpreferences.getString("phoneNo", "");
        countryCode = sharedpreferences.getInt("countryCode", 91);

        btnNext.setOnClickListener(v -> {

            String nameSP = et_name.getText().toString();
            String emailSP = et_email.getText().toString();
            String passwordSP = et_password.getText().toString();

            if ((nameSP.equals("")) || (emailSP.equals("")) || (passwordSP.equals(""))) {
                opendialog("Input fields are empty..!");
            } else if (!isEmailValid(emailSP)) {
                opendialog("Invalid Email ID..!Kindly,enter Valid Email Id..!");
            } else {
                int nameLen = nameSP.length();
                int emailLen = emailSP.length();
                int passwordLen = passwordSP.length();

                if (nameLen > 30) {


                    opendialog("Name Should be within 30 characters..!");

                    //   Toast.makeText(this, "Maximum 30 Characters are allowed !", Toast.LENGTH_LONG).show();


                } else if (emailLen > 50) {

                    opendialog("Email Should be within 50 characters..!");

                    //Toast.makeText(this, "Maximum 30 Characters are allowed !", Toast.LENGTH_LONG).show();


                } else if (passwordLen < 6) {

                    opendialog("Password Should be 6 to 12 Character Length..!");


                   // Toast.makeText(this, "Password Should Be 6 to 12 Character Length !", Toast.LENGTH_LONG).show();
                } else if (passwordLen > 12) {

                    opendialog("Password Should Be 6 to 12 Character Length..");

                   // Toast.makeText(this, "Password Should Be 6 to 12 Character Length !", Toast.LENGTH_LONG).show();

                } else {

                    sharedpreferences = UserDataActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("name", nameSP);
                    editor.putString("email", emailSP);
                    editor.putString("password", passwordSP);
                    editor.apply();

                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();

                    if (uriObject != null) {
                        uploadFile(uriObject);
                    }

                    Intent homeintent = new Intent(UserDataActivity.this, PhoneNumberActivity.class);
                    startActivity(homeintent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                }
            }
        });

    }

    private void opendialog(String msg) {
        new AlertDialog.Builder(UserDataActivity.this)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void checkPermissionRG() {

        int permissionCheck = ContextCompat.checkSelfPermission(UserDataActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    UserDataActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_READ_GALLERY);
        } else {
            checkPermissionWG();
        }

    }

    private void checkPermissionWG() {
        int permissionCheck = ContextCompat.checkSelfPermission(UserDataActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // int permissionCheck2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    UserDataActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_WRITE_GALLERY);
        } else {
            getPhotos();
        }
    }

    private void getPhotos() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, MY_REQUEST_GALLERY);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case MY_REQUEST_READ_GALLERY:
                checkPermissionWG();
                break;
            case MY_REQUEST_WRITE_GALLERY:
                getPhotos();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {

            case MY_REQUEST_GALLERY:
                try {

                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    filen = getFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(filen);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    fileOutputStream.close();
                    inputStream.close();

                    profilePic.setImageURI(Uri.parse("file:///" + filen));//fresco library
                    uriObject = Uri.parse("file:///" + filen);

                    /*progressDialog.setMessage("Please wait...");
                    progressDialog.show();

                    uploadFile(uriObject);*/

                } catch (Exception e) {

                    Log.e("", "Error while creating temp file", e);
                }
                break;

        }
    }

    public File getFile() {

        File fileDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return null;
            }
        }

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        File mediaFile = new File(fileDir.getPath() + File.separator + ts + ".jpg");
        return mediaFile;
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //startActivity(new Intent(VerificationActivity.this,SignedIn.class));
                            //finish();
                            Toast.makeText(UserDataActivity.this, "OTP Verified Successfully !", Toast.LENGTH_LONG).show();

                            ImageView iv = phoneVerificationDialog.findViewById(R.id.iv);
                            iv.setImageDrawable(getDrawable(R.drawable.tick));

                            Button btnBack = phoneVerificationDialog.findViewById(R.id.btnBack);
                            btnBack.setVisibility(View.VISIBLE);

                            sharedpreferences = UserDataActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("verified", "1");
                            editor.apply();

                            btnBack.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseAuth.getInstance().signOut();
                                    phoneVerificationDialog.dismiss();

                                    EditText et_phone = findViewById(R.id.et_phone);
                                    et_phone.setEnabled(false);

                                }
                            });

                            Button btnVerify = phoneVerificationDialog.findViewById(R.id.btnVerify);
                            Button btnResend = phoneVerificationDialog.findViewById(R.id.btnResend);

                            btnVerify.setVisibility(View.GONE);
                            btnResend.setVisibility(View.GONE);

                            verify.setText("Verified");
                            verify.setTextColor(getResources().getColor(R.color.green_400));

                        } else {
                            Toast.makeText(UserDataActivity.this, "Incorrect OTP", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(UserDataActivity.this, "OTP Verified Successfully !", Toast.LENGTH_LONG).show();

                ImageView iv = phoneVerificationDialog.findViewById(R.id.iv);
                iv.setImageDrawable(getDrawable(R.drawable.tick));

                LinearLayout otpET = phoneVerificationDialog.findViewById(R.id.otpET);
                otpET.setVisibility(View.GONE);

                Button btnVerify = phoneVerificationDialog.findViewById(R.id.btnVerify);
                Button btnResend = phoneVerificationDialog.findViewById(R.id.btnResend);

                btnVerify.setVisibility(View.GONE);
                btnResend.setVisibility(View.GONE);

                verify.setText("Verified");
                verify.setTextColor(getResources().getColor(R.color.green_400));

                Button btnBack = phoneVerificationDialog.findViewById(R.id.btnBack);
                btnBack.setVisibility(View.VISIBLE);

                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        phoneVerificationDialog.dismiss();

                        EditText et_phone = findViewById(R.id.et_phone);
                        et_phone.setEnabled(false);
                    }
                });

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(UserDataActivity.this, "Kindly check the number entered !", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(UserDataActivity.this, "OTP Sent !", Toast.LENGTH_LONG).show();
            }
        };
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void uploadFile(Uri data) {
        StorageReference sRef = mStorageReference.child("profilepics/" + email);
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl = taskSnapshot.getDownloadUrl().toString();
                        Log.i("gdfdfsfgvhgbjh",downloadUrl);
                        sharedpreferences = UserDataActivity.this.getSharedPreferences("mypref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("profilepicurl", downloadUrl);
                        editor.apply();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(UserDataActivity.this, "Profile Pic Uploaded", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(UserDataActivity.this, " Profile Pic Failed To Upload", Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
