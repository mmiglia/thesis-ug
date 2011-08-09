package com.thesisug.ui;

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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.thesisug.R;
import com.thesisug.communication.PlacesResource;
import com.thesisug.communication.valueobject.PlaceClient;


public class ListPlacesFound extends Activity{
	
	private static final String TAG = "thesisug - ListPlacesFound";
	private static final int VOTE=0;	
	
	private static final int SEARCH=1;
	
	private static final int INFO=2;
	private static final int BACK=3;
	protected final Handler handler = new Handler();
	ListView l1;
	Intent intent;
	private PlaceListAdapter adapter;
	Thread searchPlace;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_places_from_search);
		
		
		  intent=getIntent(); // l'intent di questa activity
		  
         
          String title=intent.getStringExtra("title");
          
          String streetAddress=intent.getStringExtra("streetAddress");
          String streetNumber=intent.getStringExtra("streetNumber");
         
          String cap=intent.getStringExtra("cap");
          String city=intent.getStringExtra("city");
          String category=intent.getStringExtra("category");
          
          PlaceClient place = new PlaceClient(title,"","",streetAddress,streetNumber,cap,city,category);
          
          
          searchPlace = PlacesResource.searchPublicPlace(place, handler, ListPlacesFound.this);
	}  
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,VOTE,0,R.string.newPlace).setIcon(R.drawable.user_group_add);
		menu.add(0,SEARCH,0,R.string.search).setIcon(R.drawable.sync);	
		menu.add(0,INFO,0,R.string.infoPlaces).setIcon(R.drawable.info);	
		menu.add(0,BACK,0,R.string.back).setIcon(R.drawable.go_previous_black);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case VOTE:
			
			
			break;
			
		case SEARCH:
			intent = new Intent(getApplicationContext(), SearchPublicPlace.class);
			startActivity(intent);
			
			break;	
			
			
		case INFO:
			intent = new Intent(getApplicationContext(), InfoListPlaceFound.class);
			startActivity(intent);
			break;	
			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	
	public void afterPublicPlacesLoaded(final List<PlaceClient> placeList){
		
		//Toast.makeText(ListPlacesFound.this, "afterPublicPlacesLoaded", Toast.LENGTH_SHORT).show();
		
		if(placeList==null || placeList.size()==0){
			Toast.makeText(ListPlacesFound.this, R.string.no_Places, Toast.LENGTH_LONG).show();
		}

		
		
		//Toast.makeText(PrivatePlaces.this, "Dimensione lista:" + placeList.size(), Toast.LENGTH_LONG).show();
		l1 = (ListView) findViewById(R.id.publicplacelist);
		 
		//Toast.makeText(ListPlacesFound.this, "Prima adapter", Toast.LENGTH_SHORT).show();
		 adapter = new PlaceListAdapter(this,placeList);
		 
		 //Toast.makeText(ListPlacesFound.this, "Fine Adapter", Toast.LENGTH_SHORT).show();
				 
		l1.setAdapter(adapter);
		
		//Toast.makeText(ListPlacesFound.this, "Fine Adapter", Toast.LENGTH_LONG).show();
		
		adapter.notifyDataSetChanged();
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				
					//LinkedHashMap item = (LinkedHashMap) (arg0.getItemAtPosition(arg2));
					//SingleItemLocation itemLocation = (SingleItemLocation) item.get(ITEM_DATA);
					//Toast.makeText(getBaseContext(), "Details for "+itemLocationList.get(arg2).item, Toast.LENGTH_LONG).show();
					
					intent = new Intent(getApplicationContext(), DetailsPlaceToVote.class);
					intent.putExtra("title", placeList.get(arg2).title);
					intent.putExtra("streetAddress", placeList.get(arg2).streetAddress);
					intent.putExtra("streetNumber", placeList.get(arg2).streetNumber);
					intent.putExtra("cap", placeList.get(arg2).cap);
					intent.putExtra("city", placeList.get(arg2).city);
					intent.putExtra("lat", placeList.get(arg2).lat);
					intent.putExtra("lng", placeList.get(arg2).lng);
					intent.putExtra("category", placeList.get(arg2).category);
					
					//Log.i(TAG,"details_private_place:" + placeList.get(arg2).title);	
					startActivity(intent);
			
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
				 
				 holder.txt_address.setText(placeList.get(position).streetAddress + "," + placeList.get(position).streetNumber + " " + placeList.get(position).city);
				 
				 
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			 
			 holder.txt_title.setText("'" +  placeList.get(position).title +"'");
			
			 holder.txt_address.setText(placeList.get(position).streetAddress + "," + placeList.get(position).streetNumber + " " + placeList.get(position).city);
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
	
	

}
