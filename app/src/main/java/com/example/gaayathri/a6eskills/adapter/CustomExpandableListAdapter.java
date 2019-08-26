package com.example.gaayathri.a6eskills.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gaayathri.a6eskills.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private JSONArray jsonArray;

    public CustomExpandableListAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        try {
            return this.jsonArray.getJSONObject(listPosition).getJSONArray("activity").getString(expandedListPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

         JSONObject expandedListDate= new JSONObject();
        try {
            expandedListDate = new JSONObject((String) getChild(listPosition, expandedListPosition));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.redeem_list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);

        TextView expandedListItemDate = (TextView) convertView
                .findViewById(R.id.expandedListItemDate);
        expandedListTextView.setText(expandedListDate.optString("desc"));
        try {
            expandedListItemDate.setText(expandedListDate.optString("date"));

            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(expandedListDate.optString("date"));

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm a");
            String strDate = dateFormat.format(date);

            expandedListItemDate.setText(strDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {

        try {
            return this.jsonArray.getJSONObject(listPosition).getJSONArray("activity").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return 0;
    }

    @Override
    public Object getGroup(int listPosition) {
        try {
            return this.jsonArray.getJSONObject(listPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;

    }

    @Override
    public int getGroupCount() {
        return this.jsonArray.length();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.redeem_list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
              .findViewById(R.id.listTitle);
        TextView listtitledate = (TextView) convertView.findViewById(R.id.listtitledate);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(jsonArray.optJSONObject(listPosition).optString("amount") +"USD");

        String StrDate = jsonArray.optJSONObject(listPosition).optString("createdon");

       //listtitledate.setText(jsonArray.optJSONObject(listPosition).optString("createdon") );


        try {

            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(StrDate);

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm a");
            String strDate = dateFormat.format(date);

            listtitledate.setText(strDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v("cvar-log",listtitledate.getText().toString());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

}