package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.google.android.maps.GeoPoint;
import com.thesisug.R;
import com.thesisug.communication.PlacesResource;
import com.thesisug.communication.valueobject.PlaceClient;




public class PrivatePlaces extends Activity implements LocationListener{
	
	private static final String TAG ="thesisug - PrivatePlaces";
	private static final int NEW_PLACES=0;	
	private static final int UPDATE_LIST=1;
	private static final int TAKE_HERE=2;
	private static final int INFO=3;
	private static final int BACK=4;
	private PlaceListAdapter adapter;
	Intent intent;
	ListView l1;
	public List<PlaceClient> placeListbackup = new ArrayList<PlaceClient>();

	private LocationManager lm;
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
		menu.add(0,NEW_PLACES,0,R.string.newPlace).setIcon(R.drawable.addplaces);
		menu.add(0,TAKE_HERE,0,R.string.takehere).setIcon(R.drawable.mapgps);
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
			intent.putExtra("type", "Private");
			startActivityForResult(intent,0);
			
			
			break;
			
		case TAKE_HERE:
			
			
			try
			{
			// get the GPS positions
	        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, 0.0f, this);
	        Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			String provider = lm.getBestProvider(criteria, true);
	        Location gpslocation = lm.getLastKnownLocation(provider);
			GeoPoint point=new GeoPoint((int)Math.floor (gpslocation.getLatitude()*1e6), (int) Math.floor(gpslocation.getLongitude()*1e6));
		        	
			intent = new Intent(getApplicationContext(), Create_new_place_gps.class);
			String lat = Integer.toString(point.getLatitudeE6());
			String lng = Integer.toString(point.getLongitudeE6());
			lat = lat.subSequence(0, lat.length()-6) + "." + lat.subSequence(lat.length()-6, lat.length()-1);
			lng = lng.subSequence(0, lng.length()-6) + "." + lng.subSequence(lng.length()-6, lng.length()-1);
			intent.putExtra("lat", lat);
			intent.putExtra("lng", lng);
			intent.putExtra("type", "Private");
			startActivityForResult(intent,0);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Gps Signal not available!", Toast.LENGTH_SHORT).show();
			}
			
			break;
			

		case UPDATE_LIST:
			Thread downloadPrivatePlacesThread = PlacesResource.getPrivatePlaces(handler, PrivatePlaces.this);
			adapter.notifyDataSetChanged();
			break;	
			
		case INFO:
			intent = new Intent(getApplicationContext(), InfoPrivatePlaces.class);
			startActivity(intent);
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
        Thread downloadPrivatePlacesThread = PlacesResource.getPrivatePlaces(handler, PrivatePlaces.this);
    	
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
			Toast.makeText(getApplicationContext(), R.string.no_Places, Toast.LENGTH_SHORT).show();
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
					
					intent = new Intent(getApplicationContext(), DetailsPlace.class);
					intent.putExtra("title", placeList.get(arg2).title);
					intent.putExtra("streetAddress", placeList.get(arg2).streetAddress);
					intent.putExtra("streetNumber", placeList.get(arg2).streetNumber);
					intent.putExtra("cap", placeList.get(arg2).cap);
					intent.putExtra("city", placeList.get(arg2).city);
					intent.putExtra("lat", placeList.get(arg2).lat);
					intent.putExtra("lng", placeList.get(arg2).lng);
					intent.putExtra("type", "Private");
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
				 
		
				 
				 
				 if	 (placeList.get(position).category.contains("abitazione"))
				 		holder.img_category.setImageResource(R.drawable.home1); 
				 else if (placeList.get(position).category.contains("abbigliamento"))
				 		holder.img_category.setImageResource(R.drawable.abbigliamento);
				 else if (placeList.get(position).category.contains("articoli bambino"))
						holder.img_category.setImageResource(R.drawable.artbambini); 
				 else if (placeList.get(position).category.contains("cinema"))
						holder.img_category.setImageResource(R.drawable.cinema);
				 else if (placeList.get(position).category.contains("enoteca"))
				 		holder.img_category.setImageResource(R.drawable.enoteca);
				 else if (placeList.get(position).category.contains("fastfood"))
						holder.img_category.setImageResource(R.drawable.fastfood);
				 else if (placeList.get(position).category.contains("officina"))
						holder.img_category.setImageResource(R.drawable.officina);
				 else if (placeList.get(position).category.contains("panetteria"))
						holder.img_category.setImageResource(R.drawable.panetteria);
				 else if (placeList.get(position).category.contains("pasticceria"))
						holder.img_category.setImageResource(R.drawable.pasticceria);
				 else if (placeList.get(position).category.contains("ristorante"))
						holder.img_category.setImageResource(R.drawable.ristorante);
				 else if (placeList.get(position).category.contains("scarpe"))
						holder.img_category.setImageResource(R.drawable.scarpe);
				 else if (placeList.get(position).category.contains("teatro"))
						holder.img_category.setImageResource(R.drawable.teatro);
				 else if (placeList.get(position).category.contains("ufficio"))
						holder.img_category.setImageResource(R.drawable.ufficio);
				 else if (placeList.get(position).category.contains("cellulari e telefonia"))
						holder.img_category.setImageResource(R.drawable.cell); 
				 else if (placeList.get(position).category.contains("giochi e console"))
						holder.img_category.setImageResource(R.drawable.games); 
				 else if (placeList.get(position).category.contains("musica"))
						holder.img_category.setImageResource(R.drawable.music); 
				 else if (placeList.get(position).category.contains("giornalaio"))
						holder.img_category.setImageResource(R.drawable.book); 
				 else if (placeList.get(position).category.contains("biblioteca"))
						holder.img_category.setImageResource(R.drawable.book2); 
				 else if (placeList.get(position).category.contains("pizzeria"))
						holder.img_category.setImageResource(R.drawable.pizza);
				 else if (placeList.get(position).category.contains("alimentari"))
						holder.img_category.setImageResource(R.drawable.alim);
				 else if (placeList.get(position).category.contains("iper"))
						holder.img_category.setImageResource(R.drawable.iper);
				 else if (placeList.get(position).category.contains("auto e moto"))
						holder.img_category.setImageResource(R.drawable.car);
				 else if (placeList.get(position).category.contains("informatica"))
						holder.img_category.setImageResource(R.drawable.computer); 
				 else if (placeList.get(position).category.contains("agenzia turistica"))
						holder.img_category.setImageResource(R.drawable.world); 
				 else if (placeList.get(position).category.contains("frutta e verdura"))
						holder.img_category.setImageResource(R.drawable.frutta1); 
				 else if (placeList.get(position).category.contains("sport e fitness"))
						holder.img_category.setImageResource(R.drawable.calcio); 
				 else if (placeList.get(position).category.contains("gioielleria"))
						holder.img_category.setImageResource(R.drawable.gioielleria); 
				 else if (placeList.get(position).category.contains("casa e giardino"))
						holder.img_category.setImageResource(R.drawable.garden); 
				 else if (placeList.get(position).category.contains("gioielleria"))
						holder.img_category.setImageResource(R.drawable.gioielleria); 
				 else if (placeList.get(position).category.contains("casa e giardino"))
						holder.img_category.setImageResource(R.drawable.garden); 
				 else if (placeList.get(position).category.contains("elettronica"))
						holder.img_category.setImageResource(R.drawable.elettro); 
				 else if (placeList.get(position).category.contains("libreria"))
						holder.img_category.setImageResource(R.drawable.libreria); 
				 else if (placeList.get(position).category.contains("piscina"))
						holder.img_category.setImageResource(R.drawable.piscina); 
				 else if (placeList.get(position).category.contains("ottica"))
						holder.img_category.setImageResource(R.drawable.ottica);
				 else if (placeList.get(position).category.contains("banca"))
						holder.img_category.setImageResource(R.drawable.banca);
				 else if (placeList.get(position).category.contains("elettrodomestici"))
						holder.img_category.setImageResource(R.drawable.elettrodomestici);
				 else if (placeList.get(position).category.contains("parucchiere"))
						holder.img_category.setImageResource(R.drawable.parucchiere);
				 else if (placeList.get(position).category.contains("finanza e assicurazioni"))
						holder.img_category.setImageResource(R.drawable.assicurazione);
				 else if (placeList.get(position).category.contains("centro benessere"))
						holder.img_category.setImageResource(R.drawable.benessere);
				 else if (placeList.get(position).category.contains("bar"))
						holder.img_category.setImageResource(R.drawable.bar); 
				 else if (placeList.get(position).category.contains("supermercato"))
				 		holder.img_category.setImageResource(R.drawable.cart_shop);
				 else if (placeList.get(position).category.contains("farmacia"))
						holder.img_category.setImageResource(R.drawable.farmacia);
				 else if (placeList.get(position).category.contains("ospedale"))
						holder.img_category.setImageResource(R.drawable.ospedale);
				 else if (placeList.get(position).category.contains("macelleria"))
						holder.img_category.setImageResource(R.drawable.meat);
				 else if (placeList.get(position).category.contains("poste"))
						holder.img_category.setImageResource(R.drawable.poste);
				 else if (placeList.get(position).category.contains("scuola"))
						holder.img_category.setImageResource(R.drawable.scuola);
				 
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
	
	@Override
	public void onLocationChanged(Location location) {}

	@Override
	public void onProviderDisabled(String provider) {
		
		Toast.makeText(getApplicationContext(),"Gps Disabled",Toast.LENGTH_SHORT ).show();
	}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}


}
