package com.appspan.appspan;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Calendar;
import java.util.List;

public class AppsListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.apps_list_fragment, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //works w frag layout not in main __________________
        //TextView frag=(TextView)getView().findViewById(R.id.frag_text);
        //frag.setText("FFFFFFF frag");
        //ListView listView;
        //String[] months=new String[] {"Janaury","Feb","March","April","May","June","July","August","September","Octomber","November","December"};
        //listView= (ListView)getView().findViewById(R.id.fragment_apps_list);
        //ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, android.R.id.text1, months);
        //listView.setAdapter(arrayAdapter);
        //__________________________________________________________________

    }
}
