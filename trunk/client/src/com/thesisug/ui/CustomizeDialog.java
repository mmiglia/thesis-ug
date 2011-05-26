package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
import com.thesisug.communication.valueobject.SingleItemLocation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class CustomizeDialog /*extends Dialog implements OnClickListener */{
    /*Button okButton;
    ListView list;
    final Context cs;
    Activity actClose;
    private static Thread downloadAssertionsThread;
    private final static Handler handler = new Handler();

    public CustomizeDialog(Context context,Activity EditTask) {
        super(context);
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file 
        setContentView(R.layout.customize_dialog_db);
        cs=context;
        actClose=EditTask;
        okButton = (Button) findViewById(R.id.OkButton);
        okButton.setOnClickListener(this);
        list = (ListView) findViewById(R.id.list);
       
        
        ListviewContent.add("sadas");
        ListviewCount.add("dasasd");
        ListviewContent.add("sadas");
        ListviewCount.add("hfhh");
        ListviewContent.add("sadas");
        ListviewCount.add("fhfhfh");
        ListviewContent.add("sadas");
        ListviewCount.add("fhfhf");
        ListviewContent.add("sadas");
        ListviewCount.add("dasatytytsd");
        downloadAssertionsThread = AssertionsResource.getAssertions(handler, cs);
        
        list.setAdapter(new ListViewAdapter(cs));
        
        
    }

    
    static public void afterAssertionsListLoaded(final List<SingleItemLocation> itemLocationList){
    
    	
    	String item1 = itemLocationList.get(0).item;
    
    	
    }
    
    @Override
    public void onClick(View v) {
        /** When OK Button is clicked, dismiss the dialog 
        if (v == okButton){
        	actClose.finish();
        }

    }
    
    public void message(String stringa)
    {
        
    	Toast.makeText(getContext(), R.string.noAssertions, Toast.LENGTH_LONG).show();

    }
    
    
    private static ArrayList<String> ListviewContent = new ArrayList<String>();
    private static ArrayList<String> ListviewCount = new ArrayList<String>();

    
    
    private static class ListViewAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public ListViewAdapter(Context context) {

            mInflater = LayoutInflater.from(context);

        }

        public int getCount() {
            return ListviewContent.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public String getCount(int position) {
            return ListviewCount.get(position);
        }

        public String[] getSizeType(int position) {
            String[] str = new String[2];
            str[0] = ListviewContent.get(position);
            str[1] = ListviewCount.get(position);
            return str;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ListContent holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listviewinflate, null);
                holder = new ListContent();
                holder.text = (TextView) convertView.findViewById(R.id.TextView01);
                holder.text.setCompoundDrawables(null, null, null, null);
                holder.count = (TextView) convertView.findViewById(R.id.TextView02);
                holder.count.setCompoundDrawables(null, null, null, null);
                convertView.setTag(holder);
            } else {

                holder = (ListContent) convertView.getTag();
            }

            holder.text.setText(ListviewContent.get(position));
            holder.count.setText(ListviewCount.get(position));
            return convertView;
        }

        static class ListContent {

            TextView text;
            TextView count;
        }
    }
*/
}

