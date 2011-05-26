package com.thesisug.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
import com.thesisug.communication.valueobject.Item;
import com.thesisug.communication.valueobject.SingleActionLocation;
import com.thesisug.communication.valueobject.SingleItemLocation;


public class Vote_ont_db extends Activity {
	
	private static Thread downloadAssertionsThread;
	TextView Object;
	private final static Handler handler = new Handler();
	private List<SingleActionLocation> listActionlocatin;
	
	String title;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vote_ont_db);
		 
		 Intent intent=getIntent(); // l'intent di questa activity
		 title=intent.getStringExtra("title");
		 //Object = (TextView) findViewById(R.id.obj);
		 title="pane";
		
		 
		 /*Button button1 = new Button(this);
		 button1.setText("Bottone 1");
		 Button button2 = new Button(this);
		 button2.setText("Bottone 2");
		 Button button3 = new Button(this);
		 button3.setText("Bottone 3");
		 
		 TextView tv = new TextView(this);
	     tv.setText("Info sul task inserito!");
	     tv.setId(1);
	
	     
	     TextView tv1 = new TextView(this);
	     tv1.setText("L'oggetto:");
	     tv1.setId(2);

		 
		 
		 LinearLayout layout = new LinearLayout(this);
		 layout.setOrientation(LinearLayout.HORIZONTAL);
		 layout.setGravity(Gravity.CENTER_HORIZONTAL);
		 layout.addView(tv);
		 layout.addView(tv1);
		 setContentView(layout);
		 
		 
		 */
	     
		 
		 
		//downloadAssertionsThread = AssertionsResource.getAllAssertions(handler, this);
		//Get join to group request list 
		downloadAssertionsThread = AssertionsResource.checkInOntologyDb(title, handler, Vote_ont_db.this);
	}
	
	
public void afterAssertionsListLoaded(final List<Item> itemList){
	
	
	//setContentView(R.layout.vote_ont_db);
	//Toast.makeText(Vote_ont_db.this,"OK",Toast.LENGTH_LONG).show();
	for (Item o : itemList) 
		{
			Toast.makeText(Vote_ont_db.this,o.name,Toast.LENGTH_LONG).show();
			
			//Object.append(o.name);
		}
	/*			
	
	
		if(itemLocationList==null || itemLocationList.size()==0){
			Toast.makeText(getApplicationContext(), R.string.noAssertions, Toast.LENGTH_LONG).show();
		}

		
		ListView l1 = (ListView) findViewById(R.id.list_db);
		 l1.setAdapter(new assertionsListAdapter(this,itemLocationList));
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				
					//LinkedHashMap item = (LinkedHashMap) (arg0.getItemAtPosition(arg2));
					//SingleItemLocation itemLocation = (SingleItemLocation) item.get(ITEM_DATA);
					//Toast.makeText(getBaseContext(), "Details for "+itemLocationList.get(arg2).item, Toast.LENGTH_LONG).show();
					
				
			
			}
			 
		 });
		
		*/
		 
		 }
	
	private static class assertionsListAdapter extends BaseAdapter {
		 
		private LayoutInflater mInflater;

		 private List<SingleItemLocation> itemLocationList;
		 
		 public assertionsListAdapter(Context context,List<SingleItemLocation> list) {
			 mInflater = LayoutInflater.from(context);
			 itemLocationList=list;

		 }

		 public View getView(int position, View convertView, ViewGroup parent) 
		 {
		
			 final ViewHolder holder;
			 if (convertView == null) {
				 
				 convertView = mInflater.inflate(R.layout.list_item_db, null);
				 holder = new ViewHolder();
				 
				 
				 holder.itemlocation = itemLocationList.get(position);
				 
				 holder.txt_item = (TextView) convertView.findViewById(R.id.txt_item);

				 holder.txt_item.setText("'" +  itemLocationList.get(position).item +"'");
				 
				 holder.txt_location = (TextView) convertView.findViewById(R.id.txt_location);
				 
				 holder.txt_location.setText(itemLocationList.get(position).location);
				 
				 
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			
			 return convertView;
		 }

			 static class ViewHolder {
				 TextView txt_item;
				 TextView txt_location;
				 SingleItemLocation itemlocation;
			 }

			@Override
			public int getCount() {
				
				return itemLocationList.size();
			}

			@Override
			public Object getItem(int position) {
				return itemLocationList.get(position);
			}

			@Override
			public long getItemId(int position) {
				
				return Long.parseLong(Integer.toString(itemLocationList.get(position).hashCode()));
			}


		 }
	
	
	
	
}
