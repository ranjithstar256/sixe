package com.example.gaayathri.a6eskills.Fragments;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gaayathri.a6eskills.Activites.RedeemActivity;
import com.example.gaayathri.a6eskills.R;
import com.example.gaayathri.a6eskills.Utils.UIUtils;
import com.example.gaayathri.a6eskills.adapter.ScheduledInterviewsListAdapter;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    OkHttpClient client;
    Dialog editProfile;
    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view;
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getActivity().setTitle("My Profile");

        client = new OkHttpClient();

        sharedpreferences = getActivity().getSharedPreferences("mypref", 0); // 0 - for private mode

        String name = sharedpreferences.getString("username", "Username");
        String email = sharedpreferences.getString("primaryemail", "Email");
        String city = sharedpreferences.getString("city", "city");
        String phone = sharedpreferences.getString("phone", "Phone");
        String company = sharedpreferences.getString("company", "Company");
        String profilepicurl = sharedpreferences.getString("profilepicurl", "https://firebasestorage.googleapis.com/v0/b/seskills-master.appspot.com/o/boss.png?alt=media&token=7f1d6ae9-6d63-486a-8795-2229981b0989");
        String mainskilll = sharedpreferences.getString("mainSkill1", "skill 1");
        String mainskill2 = sharedpreferences.getString("mainSkill2", "skill 2");
        String subSkill1 = sharedpreferences.getString("subSkill1", "subskill 1");
        String subSkill2 = sharedpreferences.getString("subSkill2", "subskill 2");
        String sub2Skill1 = sharedpreferences.getString("sub2Skill1", "subskill 3");
        String sub2Skill2 = sharedpreferences.getString("sub2Skill2", "subskill 4");
        String level1 = sharedpreferences.getString("level1", "level 1");
        String level2 = sharedpreferences.getString("level2", "level 2");
        String secretkey = sharedpreferences.getString("secretkey", "secretkey");
        String textToBePlaced = sharedpreferences.getString("textToBePlaced", "-");

        TextView tvname = view.findViewById(R.id.tvname);
        TextView tvcity = view.findViewById(R.id.tvcity);
        TextView tvemail = view.findViewById(R.id.tvemail);
        TextView tvphone = view.findViewById(R.id.tvphone);
        TextView tvcompany = view.findViewById(R.id.tvcompany);
        TextView tvskill1 = view.findViewById(R.id.skill1);
        TextView tvskill2 = view.findViewById(R.id.skill2);
        TextView tvsubskill1 = view.findViewById(R.id.subskill1);
        TextView tvsubskill2 = view.findViewById(R.id.subskill2);
        TextView tvsub1skill1 = view.findViewById(R.id.sub1skill1);
        TextView tvsub1skill2 = view.findViewById(R.id.sub1skill2);
        TextView tvlevel1 = view.findViewById(R.id.level1);
        TextView tvlevel2 = view.findViewById(R.id.level2);
        TextView tvPoints = view.findViewById(R.id.tvPoints);

        CircularImageView circularImageView = view.findViewById(R.id.image);

        tvname.setText(name);
        tvcity.setText(city);
        tvemail.setText(email);
        tvphone.setText(phone);
        tvcompany.setText(company);
        tvskill1.setText(mainskilll);
        tvskill2.setText(mainskill2);
        tvsubskill1.setText(subSkill1);
        tvsubskill2.setText(subSkill2);
        tvsub1skill1.setText(sub2Skill1);
        tvsub1skill2.setText(sub2Skill2);
        tvlevel1.setText(level1);
        tvlevel2.setText(level2);
        tvPoints.setText(textToBePlaced);

        if(profilepicurl.equals("")){
            //Toast.makeText(getActivity(), "url: " + profilepicurl, Toast.LENGTH_LONG).show();
            profilepicurl = "https://firebasestorage.googleapis.com/v0/b/seskills-master.appspot.com/o/boss.png?alt=media&token=7f1d6ae9-6d63-486a-8795-2229981b0989";
        }

        final RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(getActivity()).load(profilepicurl).apply(options).into(circularImageView);

        editProfile = new Dialog(getActivity());
        editProfile.setContentView(R.layout.dialog_edit_profile);

        ImageButton btnProfileEdit = view.findViewById(R.id.btnProfileEdit);
        btnProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile.show();
                profileUpdate(secretkey);
            }
        });

        Button btnRedeem = view.findViewById(R.id.btnRedeem);
        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeintent = new Intent(getActivity(), RedeemActivity.class);
                startActivity(homeintent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });

        return view;
    }

    private int getRatingAgainstFive(int ratingaverage) {
        int mid = ratingaverage/20;
        int mid2 = Math.round(mid);
        return mid2;
    }

    private void profileUpdate(String secretkey) {

        Button btnCancel = editProfile.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile.dismiss();
            }
        });

        Button btnUpdate = editProfile.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et_name = editProfile.findViewById(R.id.et_name);
                EditText et_email = editProfile.findViewById(R.id.et_email);
                EditText et_city = editProfile.findViewById(R.id.spCity);
                EditText et_company = editProfile.findViewById(R.id.spCompany);

                String updatedName = et_name.getText().toString();
                String updatedEmail = et_email.getText().toString();
                String updatedCity = et_city.getText().toString();
                String updatedCompany = et_company.getText().toString();

                if ((updatedName.equals("")) || (updatedCity.equals("")) || (updatedCompany.equals(""))) {
                    Toast.makeText(getActivity(), "Kindly Fill All Fields !", Toast.LENGTH_LONG).show();
                } else if (!isEmailValid(updatedEmail)){
                    Toast.makeText(getActivity(), "Invalid Email ID", Toast.LENGTH_LONG).show();
                } else {
                    RequestBody formBody = new FormBody.Builder()
                            .add("name", updatedName)
                            .add("email", updatedEmail)
                            .add("city", updatedCity)
                            .add("company", updatedCompany)
                            .build();


                    final Request request = new Request.Builder()
                            .url("http://13.233.146.44:9090/skills/api/v1/user/profile/details")
                            //.url("http://137.74.157.254:8080/skills/api/v1/user/profile/details")
                            .put(formBody)
                            .addHeader("apikey", secretkey)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            String mMessage = e.getMessage();
                            Log.w("failure Response", mMessage);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Kindly check your internet", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            String mMessage = response.body().string();
                            int returnCode;
                            String returnName;
                            String returnEmail;
                            String returnCity;
                            String returnCompany;
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject json = new JSONObject(mMessage);
                                    returnCode = json.getInt("code");
                                    returnName = json.getJSONObject("data").getString("username");
                                    returnEmail = json.getJSONObject("data").getString("primaryemail");
                                    returnCity = json.getJSONObject("data").getString("city");
                                    returnCompany = json.getJSONObject("data").getString("company");

                                    if (returnCode == 1004){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_LONG).show();
                                                ProfileFragment profileFragment = new ProfileFragment();
                                                FragmentManager fragmentManager = getFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                fragmentTransaction.replace(R.id.container,profileFragment);
                                                fragmentTransaction.commit();
                                            }
                                        });
                                        sharedpreferences = getActivity().getSharedPreferences("mypref", 0); // 0 - for private mode
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("username", returnName);
                                        editor.putString("primaryemail", returnEmail);
                                        editor.putString("city", returnCity);
                                        editor.putString("company", returnCompany);
                                        editor.apply();
                                    } else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    });
                    editProfile.dismiss();
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;
        private Context mContext;
        private View rootView;

        public AsyncTaskRunner(Context context, View rootView) {
            this.mContext = context;
            this.rootView = rootView;
            progressDialog = new ProgressDialog(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {

            sharedpreferences = getActivity().getSharedPreferences("mypref", 0); // 0 - for private mode
            String apikey = sharedpreferences.getString("secretkey", "");


            // Fetching skills from DB
            Request profilerequest = new Request.Builder()
                    .url("http://13.233.146.44:9090/skills/api/v1/user/skills/selected")
                   // .url("http://137.74.157.254:8080/skills/api/v1/user/skills/selected")
                    .get()
                    .addHeader("apikey", apikey)
                    .build();

            // Getting response from the client
            Response profileresponse = null;
            String serverResponse = null;
            JSONObject jsonObject = null;
            try {
                profileresponse = client.newCall(profilerequest).execute();
                serverResponse = profileresponse.body().string();
                jsonObject = new JSONObject(serverResponse);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            // Looping through interviews and getting data
            ArrayList<String> nameList = new ArrayList<String>();
            ArrayList<String> agidList = new ArrayList<String>();
            ArrayList<String> interviewidList = new ArrayList<String>();
            ArrayList<String> titleList = new ArrayList<String>();
            ArrayList<String> companyidList = new ArrayList<String>();
            ArrayList<String> companynameList = new ArrayList<String>();
            ArrayList<String> descList = new ArrayList<String>();
            ArrayList<String> statusList = new ArrayList<String>();
            ArrayList<String> positionfromList = new ArrayList<String>();
            ArrayList<String> positiontoList = new ArrayList<String>();
            ArrayList<String> packegefromList = new ArrayList<String>();
            ArrayList<String> packegetoList = new ArrayList<String>();
            ArrayList<String> currencycodeList = new ArrayList<String>();
            ArrayList<String> montlyoryearlyList = new ArrayList<String>();
            ArrayList<String> fromdateList = new ArrayList<String>();
            ArrayList<String> todateList = new ArrayList<String>();
            ArrayList<String> skillidList = new ArrayList<String>();
            ArrayList<String> skillnameList = new ArrayList<String>();

            JSONArray jsonArray = null;

            try {

                jsonArray = new JSONArray();
                jsonArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject explrObject = jsonArray.getJSONObject(i);

                    String name = explrObject.getString("skillname");
                    String agid = explrObject.getString("agid");
                    String interviewid = explrObject.getString("interviewid");
                    String title = explrObject.getString("title");
                    String companyid = explrObject.getString("companyid");
                    String companyname = explrObject.getString("companyname");
                    String desc = explrObject.getString("desc");
                    String status = explrObject.getString("status");
                    String positionfrom = explrObject.getString("positionfrom");
                    String positionto = explrObject.getString("positionto");
                    String packegefrom = explrObject.getString("packegefrom");
                    String packegeto = explrObject.getString("packegeto");
                    String currencycode = explrObject.getString("currencycode");
                    String montlyoryearly = explrObject.getString("montlyoryearly");
                    String fromdate = explrObject.getString("fromdate");
                    String todate = explrObject.getString("todate");
                    String skillid = explrObject.getString("skillid");
                    String skillname = explrObject.getString("skillname");

                    nameList.add(name);
                    agidList.add(agid);
                    interviewidList.add(interviewid);
                    titleList.add(title);
                    companyidList.add(companyid);
                    companynameList.add(companyname);
                    descList.add(desc);
                    statusList.add(status);
                    positionfromList.add(positionfrom);
                    positiontoList.add(positionto);
                    packegefromList.add(packegefrom);
                    packegetoList.add(packegeto);
                    currencycodeList.add(currencycode);
                    montlyoryearlyList.add(montlyoryearly);
                    fromdateList.add(fromdate);
                    todateList.add(todate);
                    skillidList.add(skillid);
                    skillnameList.add(skillname);

                    Log.d(name, "Output");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Setting array adapter
            final ScheduledInterviewsListAdapter scheduledInterviewsListAdapter =
                    new ScheduledInterviewsListAdapter(getActivity(), nameList, agidList, interviewidList, titleList, companyidList, companynameList, descList, statusList, positionfromList, positiontoList, packegefromList, packegetoList, currencycodeList, montlyoryearlyList, fromdateList, todateList, skillidList, skillnameList, jsonArray);

            Log.d("fghgfjghkj",skillidList+""+skillnameList);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListView listView = rootView.findViewById(R.id.scheduled_interviews_list_view);
                    listView.setEmptyView(rootView.findViewById(R.id.empty_text_view));
                    listView.setAdapter(scheduledInterviewsListAdapter);
                    UIUtils.setListViewHeightBasedOnItems(listView);
                }
            });

            return "good";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            // progressDialog.dismiss();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }


        @Override
        protected void onPreExecute() {
            //progressDialog = ProgressDialog.show(getActivity(), "Interviews loading", "Please wait");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
