package com.example.daniel.jobster.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.example.daniel.jobster.DataModels.Offer;
import com.example.daniel.jobster.Functions;
import com.example.daniel.jobster.R;

import java.util.ArrayList;

public class OffersAdapter extends ArrayAdapter<Offer> implements Filterable
{
    private ArrayList<Offer> offers;
    private ArrayList<Offer> filteredOffers;
    private ItemFilter filter = new ItemFilter();

    public OffersAdapter(Context context, int resource, ArrayList<Offer> offers)
    {
        super(context, resource, offers);
        this.offers = offers;
        this.filteredOffers = offers;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        // create new row view
        if(convertView == null)
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.offers_list_row, parent, false);

        // create list row elements
        Holder holder = new Holder();
        holder.offerNameField = (TextView)convertView.findViewById(R.id.applications_list__offer_name);
        holder.cityField = (TextView)convertView.findViewById(R.id.applications_list__city);
        holder.startTimeField = (TextView)convertView.findViewById(R.id.applications_list__status);
        holder.salaryField = (TextView)convertView.findViewById(R.id.applications_list__salary);
        holder.salaryTypeField = (TextView)convertView.findViewById(R.id.applications_list__salary_type);
        holder.providerNameField = (TextView)convertView.findViewById(R.id.applications_list__application_time);

        convertView.setTag(holder);

        // get row data
        Offer rowData = getItem(position);

        // set row data
        holder.offerNameField.setText(rowData.getOfferName());
        holder.cityField.setText(rowData.getCity());
        holder.startTimeField.setText(Functions.changeTimeFormat(rowData.getStartTime()));
        holder.salaryField.setText(Functions.formatDouble(rowData.getSalary()) + "zł");
        holder.salaryTypeField.setText(rowData.getSalaryTypeString());
        if(rowData.getProviderName() == null)
            holder.providerNameField.setVisibility(View.INVISIBLE);
        else
            holder.providerNameField.setText(rowData.getProviderName());

        // return new row
        return convertView;
    }

    @Override
    public int getCount()
    {
        return filteredOffers.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public Offer getItem(int position)
    {
        return filteredOffers.get(position);
    }

    @Override
    public Filter getFilter()
    {
        return filter;
    }

    // search filter
    private class ItemFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            ArrayList<Offer> filteredOffers = new ArrayList<Offer>();

            // add all offers if there is no search query
            if(charSequence == null || charSequence.length() == 0)
                filteredOffers.addAll(offers);
            else
            {
                // get search query
                String searchQuery = charSequence.toString().toLowerCase().trim();

                // add offers matching to search query
                for(Offer offer : offers)
                    if(offer.getOfferName().toLowerCase().contains(searchQuery) || offer.getCity().toLowerCase().equals(searchQuery) || offer.getProviderName().toLowerCase().equals(searchQuery))
                        filteredOffers.add(offer);
            }

            // set filter results
            FilterResults results = new FilterResults();
            results.values = filteredOffers;
            results.count = filteredOffers.size();

            // return filtered results
            return results;
        }

        // show filtered items
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults)
        {
            filteredOffers = (ArrayList<Offer>)filterResults.values;
            notifyDataSetChanged();
        }
    }

    // data holder
    private static class Holder
    {
        private TextView offerNameField, cityField, startTimeField, salaryField, salaryTypeField, providerNameField;
    }
}

