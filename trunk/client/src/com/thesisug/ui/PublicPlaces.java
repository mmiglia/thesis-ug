package com.thesisug.ui;

import java.util.List;

import com.thesisug.R;
import com.thesisug.communication.PlacesResource;
import com.thesisug.communication.valueobject.PlaceClient;


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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PublicPlaces extends Activity{
	
	private static final String TAG ="thesisug - PublicPlaces";
	private static final int NEW_PLACES=0;	
	private static final int UPDATE_LIST=1;
	private static final int SEARCH=2;
	
	private static final int INFO=3;
	private static final int BACK=4;
	
	private static int currentDialog;	
	private static Thread downloadPublicPlacesThread;
	private final static Handler handler = new Handler();
	private PlaceListAdapter adapter;
	Intent intent;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.public_places);
		
		downloadPublicPlacesThread = PlacesResource.getPublicPlaces(handler, PublicPlaces.this);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,NEW_PLACES,0,R.string.newPlace).setIcon(R.drawable.user_group_add);
		menu.add(0,UPDATE_LIST,0,R.string.updateList).setIcon(R.drawable.sync);	
		menu.add(0,SEARCH,0,R.string.search).setIcon(R.drawable.sync);	
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
			startActivityForResult(intent,0);
			break;
			

		case UPDATE_LIST:
			downloadPublicPlacesThread = PlacesResource.getPublicPlaces(handler, PublicPlaces.this);
			adapter.notifyDataSetChanged();
			break;	
			
		case SEARCH:
			intent = new Intent(getApplicationContext(), SearchPublicPlace.class);
			startActivityForResult(intent,1);
			
			break;	
			
			
		case INFO:
			intent = new Intent(getApplicationContext(), InfoPublicPlaces.class);
			startActivity(intent);
			break;	
			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	
	 protected void onActivityResult(int requestCode, int resultCode,Intent data) {
         if (requestCode == 1) {
             if (resultCode == RESULT_OK) {
                 
            	 
					
             }
         }
	 }
	 
	 protected void onResume() {
	        super.onResume();
	        downloadPublicPlacesThread = PlacesResource.getPublicPlaces(handler, PublicPlaces.this);
	 }
	
	public void afterPublicPlacesLoaded(final List<PlaceClient> placeList){
		
		//Toast.makeText(getBaseContext(), itemLocationList.toString(), Toast.LENGTH_LONG).show();
		    	
				if(placeList==null || placeList.size()==0){
					Toast.makeText(getApplicationContext(), R.string.no_Places, Toast.LENGTH_LONG).show();
				}

				
				ListView l1 = (ListView) findViewById(R.id.publicplacelist);
				 
				 adapter = new PlaceListAdapter(this,placeList);
				 

				 
				l1.setAdapter(adapter);
				
				adapter.notifyDataSetChanged();
				 
				 l1.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						
						
						intent = new Intent(getApplicationContext(), DetailsPlace.class);
						intent.putExtra("title", placeList.get(arg2).title);
						intent.putExtra("streetAddress", placeList.get(arg2).streetAddress);
						intent.putExtra("streetNumber", placeList.get(arg2).streetNumber);
						intent.putExtra("cap", placeList.get(arg2).cap);
						intent.putExtra("city", placeList.get(arg2).city);
						intent.putExtra("lat", placeList.get(arg2).lat);
						intent.putExtra("lng", placeList.get(arg2).lng);
						intent.putExtra("type", "Public");
						intent.putExtra("category", placeList.get(arg2).category);
						//Log.i(TAG,"details_public_place:" + placeList.get(arg2).title);	
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
						 
						 convertView = mInflater.inflate(R.layout.public_place_item_list, null);
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

	
	
}
