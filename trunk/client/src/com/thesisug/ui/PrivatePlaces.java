package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.PlacesResource;
import com.thesisug.communication.valueobject.PlaceClient;




public class PrivatePlaces extends Activity{
	
	private static final String TAG ="thesisug - PrivatePlaces";
	private static final int NEW_PLACES=0;	
	private static final int UPDATE_LIST=1;
	private static final int INFO=2;
	private static final int BACK=3;
	private PlaceListAdapter adapter;
	Intent intent;
	ListView l1;
	public List<PlaceClient> placeListbackup = new ArrayList<PlaceClient>();

	
	private static int currentDialog;	
	//private static Thread downloadPrivatePlacesThread;
	
	public final static Handler handler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_places);
		
		Thread downloadPrivatePlacesThread = PlacesResource.getPrivatePlaces(handler, PrivatePlaces.this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,NEW_PLACES,0,R.string.newPlace).setIcon(R.drawable.user_group_add);
		menu.add(0,UPDATE_LIST,0,R.string.updateList).setIcon(R.drawable.sync);	
		menu.add(0,INFO,0,R.string.infoPlaces).setIcon(R.drawable.info);	
		menu.add(0,BACK,0,R.string.back).setIcon(R.drawable.go_previous_black);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case NEW_PLACES:
			
			intent = new Intent(getApplicationContext(), Create_new_place.class);
			intent.putExtra("qual", "private");
			startActivityForResult(intent,0);
			
			
			break;
			

		case UPDATE_LIST:
			Thread downloadPrivatePlacesThread = PlacesResource.getPrivatePlaces(handler, PrivatePlaces.this);
			adapter.notifyDataSetChanged();
			break;	
			
		case INFO:
			
			break;	
			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
            	/*intent=getIntent();
            	String name=intent.getStringExtra("title");
                String streetAddress=intent.getStringExtra("streetAddress");
                String streetNumber=intent.getStringExtra("streetNumber");
                String cap=intent.getStringExtra("cap");
                String city=intent.getStringExtra("city");
                String category = intent.getStringExtra("category");
                
                PlaceClient newPlace = new PlaceClient(name,streetAddress,streetNumber,cap,city,category);
                Toast.makeText(PrivatePlaces.this, "sto aggiungendo nuovo posto alla lista", Toast.LENGTH_LONG).show();
                placeListbackup.add(newPlace);
                adapter = new PlaceListAdapter(this,placeListbackup);
    			l1.setAdapter(adapter);*/
            	
            	
            	Thread downloadPrivatePlacesThread = PlacesResource.getPrivatePlaces(handler, PrivatePlaces.this);
    	
    			

       		 	
       		 	
            }
        }
	 }
	
	
	protected void onResume() {
        super.onResume();
        //Toast.makeText(PrivatePlaces.this, "--onResumePrivatePlace--", Toast.LENGTH_LONG).show();
        //Thread downloadPrivatePlacesThread = PlacesResource.getPrivatePlaces(handler, PrivatePlaces.this);
        
        //Toast.makeText(PrivatePlaces.this, "--fine download--", Toast.LENGTH_LONG).show();

	 
		//l1.setAdapter(adapter);
		
        
		/*if (!(intent == null))
		{
		intent = getIntent();
		String name=intent.getStringExtra("name");
        String streetAddress=intent.getStringExtra("streetAddress");
        String streetNumber=intent.getStringExtra("streetNumber");
        String cap=intent.getStringExtra("cap");
        String city=intent.getStringExtra("city");
        String category=intent.getStringExtra("type");
        
        PlaceClient newPlace = new PlaceClient(name,streetAddress,streetNumber,cap,city,category);
        
        placeListbackup.add(newPlace);
		 Toast.makeText(PrivatePlaces.this, "Dimensione lista:" + placeListbackup.size(), Toast.LENGTH_LONG).show();
		 Toast.makeText(PrivatePlaces.this, "Luogo aggiunto a placeList!", Toast.LENGTH_LONG).show();
		 
		 adapter = new PlaceListAdapter(this,placeListbackup);
		 

		 
			l1.setAdapter(adapter);
			
			adapter.notifyDataSetChanged();
			 
	}*/
	}	
	
	public void afterPrivatePlacesLoaded(final List<PlaceClient> placeList){
	

		if(placeList==null || placeList.size()==0){
			Toast.makeText(getApplicationContext(), R.string.no_Places, Toast.LENGTH_LONG).show();
		}

		placeListbackup= placeList;
		
		//Toast.makeText(PrivatePlaces.this, "Dimensione lista:" + placeList.size(), Toast.LENGTH_LONG).show();
		l1 = (ListView) findViewById(R.id.privateplacelist);
		 
		 adapter = new PlaceListAdapter(this,placeList);
		 
		//Toast.makeText(PrivatePlaces.this, "Fine Adapter", Toast.LENGTH_LONG).show();
				 
		l1.setAdapter(adapter);
		
		
		
		adapter.notifyDataSetChanged();
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				
					//LinkedHashMap item = (LinkedHashMap) (arg0.getItemAtPosition(arg2));
					//SingleItemLocation itemLocation = (SingleItemLocation) item.get(ITEM_DATA);
					//Toast.makeText(getBaseContext(), "Details for "+itemLocationList.get(arg2).item, Toast.LENGTH_LONG).show();
					
					/*intent = new Intent(getApplicationContext(), Details_assertion_item.class);
					intent.putExtra("item", itemLocationList.get(arg2).item);
					intent.putExtra("location", itemLocationList.get(arg2).location);
					intent.putExtra("username", itemLocationList.get(arg2).username);
					intent.putExtra("n_views", itemLocationList.get(arg2).n_views);
					intent.putExtra("n_votes", itemLocationList.get(arg2).n_votes);
					intent.putExtra("vote", itemLocationList.get(arg2).vote);
					intent.putExtra("description", itemLocationList.get(arg2).item + " si può trovare in " + itemLocationList.get(arg2).location); 
					Log.i(TAG,"details_assertion_item:"+ itemLocationList.get(arg2).item + "->" + itemLocationList.get(arg2).location);	
					startActivityForResult(intent, 0);*/
			
			}
			 
		 });

	}
	
	private static class PlaceListAdapter extends BaseAdapter {
		 
		private LayoutInflater mInflater;

		 private List<PlaceClient> placeList;
		 
		 public PlaceListAdapter(Context context,List<PlaceClient> list) {
			// mInflater = LayoutInflater.from(context);
			 mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 placeList=list;

		 }

		 public View getView(int position, View convertView, ViewGroup parent) 
		 {
		
			 final ViewHolder holder;
			 if (convertView == null) {
				 
				 convertView = mInflater.inflate(R.layout.private_place_item_list, null);
				 holder = new ViewHolder();
				 
				 
				 holder.place = placeList.get(position);
				 
				 holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);

				 holder.txt_title.setText("'" +  placeList.get(position).title +"'");
				 
				 holder.img_category = (ImageView) convertView.findViewById(R.id.img_category);
				 
				 if	 (placeList.get(position).category.equals("abitazione"))
				 		holder.img_category.setImageResource(R.drawable.home1);
				 else if (placeList.get(position).category.equals("supermercato"))
				 		holder.img_category.setImageResource(R.drawable.cart_shop);
				 
				 holder.txt_address = (TextView) convertView.findViewById(R.id.txt_address);
				 
				 holder.txt_address.setText(placeList.get(position).streetAddress + "," + placeList.get(position).streetNumber + "," + placeList.get(position).city);
				 
				 
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			 
			 holder.txt_title.setText("'" +  placeList.get(position).title +"'");
			
			 holder.txt_address.setText(placeList.get(position).streetAddress + "," + placeList.get(position).streetNumber + "," + placeList.get(position).city);
			 return convertView;
		 }

			 static class ViewHolder {
				 TextView txt_title;
				 TextView txt_address;
				 ImageView img_category;
				 PlaceClient place;
			 }

			@Override
			public int getCount() {
				
				return placeList.size();
			}

			@Override
			public Object getItem(int position) {
				return placeList.get(position);
			}

			@Override
			public long getItemId(int position) {
				
				return Long.parseLong(Integer.toString(placeList.get(position).hashCode()));
			}


		 }
	
	
	
	public void addPrivate(String title, String streetAddress, String streetNumber, String cap, String city, String category)
	{
		PlaceClient place = new PlaceClient(title,streetAddress,streetNumber,cap,city,category);
		placeListbackup.add(place);
	}
	
	public void finishSaveToAdd (boolean result, PlaceClient placeAdded) {
		 
		 if (result)
		 { 
			 
			 Toast.makeText(PrivatePlaces.this, "Place added!", Toast.LENGTH_LONG).show();
			 PlaceClient place = new PlaceClient(placeAdded.title,placeAdded.streetAddress,placeAdded.streetNumber,placeAdded.cap,placeAdded.city,placeAdded.category);
			 placeListbackup.add(place);
			 //PrivatePlaces.addPrivate(placeAdded.title,placeAdded.streetAddress,placeAdded.streetNumber,placeAdded.cap,placeAdded.city,placeAdded.category);
			 afterPrivatePlacesLoaded(placeListbackup);
			 
			 /*intent = new Intent(getApplicationContext(), PlacesTab.class);
			 intent.putExtra("name", placeAdded.title);
			 intent.putExtra("streetAddress",placeAdded.streetAddress);
			 intent.putExtra("streetNumber", placeAdded.streetNumber);
			 intent.putExtra("cap", placeAdded.cap);
			 intent.putExtra("city", placeAdded.city);
			 intent.putExtra("category", placeAdded.category);*/
			 //startActivityForResult(intent,0);
				finish();
				
			 
		 }else
			 Toast.makeText(PrivatePlaces.this, R.string.saving_error, Toast.LENGTH_LONG).show();
			 
	}


}
