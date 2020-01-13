package com.example.daniel.jobster.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.daniel.jobster.DataModels.Rating;
import com.example.daniel.jobster.R;

import java.util.ArrayList;

public class OpinionsAdapter extends ArrayAdapter<Rating>
{
    private ArrayList<Rating> opinions;

    public OpinionsAdapter(Context context, int resource, ArrayList<Rating> opinions)
    {
        super(context, resource, opinions);
        this.opinions = opinions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        // create new row view
        if(convertView == null)
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.opinions_list_row, parent, false);

        // create list row elements
        Holder holder = new Holder();
        holder.userFromNameField = (TextView)convertView.findViewById(R.id.opinions_list__name);
        holder.commentField = (TextView)convertView.findViewById(R.id.opinions_list__comment);
        holder.ratingField = (TextView)convertView.findViewById(R.id.opinions_list__rating);

        convertView.setTag(holder);

        // get row data
        Rating rowData = getItem(position);

        // set row data
        holder.userFromNameField.setText(rowData.getUserFromName());
        if(!TextUtils.isEmpty(rowData.getComment()) || !rowData.getComment().equals("null"))
            holder.commentField.setText(rowData.getComment());
        holder.ratingField.setText(String.valueOf(rowData.getRating()));

        // return new row
        return convertView;
    }

    private static class Holder
    {
        private TextView userFromNameField, commentField, ratingField;
    }
}
