package com.onyx.android.dr.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.onyx.android.dr.R;
import com.onyx.android.sdk.dict.data.DictionaryQueryResult;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by solskjaer49 on 15/12/17 19:35.
 */
public class SearchWordAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList = new ArrayList<String>();
    private int limit = 0;

    public SearchWordAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        limit = context.getResources().getInteger(R.integer.word_list_limit);
    }
    public void setHeadwordHistory(ArrayList<String> headwordHistory){
        if(headwordHistory.size() > 0){
            resultList.clear();
            resultList.addAll(headwordHistory);
        }
    }
    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return (resultList.size() <= index) ? "" : resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {
                    // Retrieve the autoComplete results.
                    resultList.clear();
                    resultList = autoComplete(constraint.toString(),limit);
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    public static ArrayList<String> autoComplete(String input,int limit) {
        ArrayList<String> resultList = new ArrayList<String>();

        return resultList;
    }

    public static class ComparatorListSort implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {

            if (((DictionaryQueryResult) object1).entryIndex > ((DictionaryQueryResult) object2).entryIndex) {
                return 1;
            } else if (((DictionaryQueryResult) object1).entryIndex < ((DictionaryQueryResult) object2).entryIndex) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
