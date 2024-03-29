package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.PlacesResource;
import com.thesisug.communication.valueobject.PlaceClient;

public class SelectCategory extends Activity{
	
	
	private static final String TAG = "thesisug - SelectCategory";
	private Handler handler;
	
	Intent intent;
	PlaceClient newPlace;
	Thread creationNewPlace;
	String name;
	String streetAddress;
	String streetNumber;
	String cap;
	String city;
	String type;
	String category="";
	
	Button addPlace;
	CheckBox checkBoxItem;
	
	private static final int CREATE_PLACE=0;	
	
	private CategoryListAdapter adapter;
	
	private List<String> listCateg = new ArrayList<String>();
	private List<String> listCategPlace = new ArrayList<String>();;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		intent=getIntent(); // l'intent di questa activity
		
		handler = PrivatePlaces.handler;
		setContentView(R.layout.select_category);
		
		addPlace = (Button) findViewById(R.id.add_button);
		
		addPlace.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				//Toast.makeText(SelectCategory.this,listCategPlace.toString(),Toast.LENGTH_LONG).show();
				
				ListIterator<String> iterator = listCategPlace.listIterator();
				if (iterator.hasNext())
					category = iterator.next().toString() ;
				//crea l'iteratore
				while (iterator.hasNext()) {
					category = category +  "," + iterator.next().toString() ;
				}
				//Toast.makeText(SelectCategory.this,"String categoria finale:"+  category,Toast.LENGTH_LONG).show();
				
				if (!(category.equals("")))
				{
					/*if (type.equals("Private"))
					{
						creationNewPlace = PlacesResource.createPrivatePlace(newPlace, handler, SelectCategory.this);
				
					}else if (type.equals("Private")){
						//creationNewPlace = PlacesResource.createPublicPlace(newPlace,handler, SelectCategory.this);
			 		}*/

					intent.putExtra("category", category);
					//Toast.makeText(SelectCategory.this, "Invio risultati!",Toast.LENGTH_LONG).show();
					setResult(RESULT_OK, intent);
					finish();
			
				}else 
					Toast.makeText(SelectCategory.this, "Select almost a category for the place!",Toast.LENGTH_LONG).show();
				
				}
		});
		
		
        
       /* name=intent.getStringExtra("name");
        streetAddress=intent.getStringExtra("streetAddress");
        streetNumber=intent.getStringExtra("streetNumber");
        cap=intent.getStringExtra("cap");
        city=intent.getStringExtra("city");
        type=intent.getStringExtra("type");*/
        
        
        listCateg.add(new String("abitazione"));
        listCateg.add(new String("supermercato"));
        listCateg.add("alimentari");
        listCateg.add("bar");
        listCateg.add("enoteca");
        listCateg.add("iper");
        listCateg.add("officina");
        listCateg.add("pizzeria");
        listCateg.add("articoli bambino");
        
        listCateg.add("abbigliamento");
        listCateg.add("cinema");
        listCateg.add("fastfood");
        listCateg.add("banca");
        listCateg.add("panetteria");
        listCateg.add("macelleria");
        listCateg.add("pasticceria");
        listCateg.add("scarpe");
        listCateg.add("parucchiere");
        
        listCateg.add("teatro");
        listCateg.add("ufficio");
        listCateg.add("poste");
        listCateg.add("frutta e verdura");
        listCateg.add("sport e fitness");
        listCateg.add("auto e moto");
        listCateg.add("casa e giardino");
        listCateg.add("cellulari e telefonia");
        listCateg.add("giochi e console");
        listCateg.add("elettrodomestici");
        listCateg.add("elettronica");
        listCateg.add("finanza e assicurazioni");
        listCateg.add("farmacia");
        listCateg.add("ospedale");
        listCateg.add("scuola");
        listCateg.add("gioielleria");
        listCateg.add("informatica");
        listCateg.add("libreria");
        listCateg.add("biblioteca");
        listCateg.add("giornalaio");
        listCateg.add("ottica");
        listCateg.add("agenzia turistica");
        listCateg.add("piscina");
        listCateg.add("centro benessere");
        
        adapter = new CategoryListAdapter(this,listCateg);
		 
        ListView l1 = (ListView) findViewById(R.id.categoryList);
		 
		l1.setAdapter(adapter);
		

		
		l1.setOnItemClickListener(new OnItemClickListener(){
			
		
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			
		
			checkBoxItem =(CheckBox) arg1.findViewById(R.id.checkbox);	
			
			
			
			if (!checkBoxItem.isChecked())
			{
				checkBoxItem.setChecked(true);	
				listCategPlace.add(listCateg.get(arg2).toString());	
				
			}
			else
			{
				listCategPlace.remove(listCateg.get(arg2).toString());
				checkBoxItem.setChecked(false);
				
			}	
			
			//Toast.makeText(SelectCategory.this, listCategPlace.toString(),Toast.LENGTH_LONG).show();
			
		}
			
						});
	 
	 
		 
	 }
	
	private static class CategoryListAdapter extends BaseAdapter {
		 
		private LayoutInflater mInflater;

		 private List<String> listCategory;
		 
		 public CategoryListAdapter(Context context,List<String> list) {
			// mInflater = LayoutInflater.from(context);
			 mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 listCategory=list;

		 }

		 public View getView(int position, View convertView, ViewGroup parent) 
		 {
		
			 final ViewHolder holder;
			 if (convertView == null) {
				 
				 convertView = mInflater.inflate(R.layout.select_category_item, null);
				 holder = new ViewHolder();
				 
				 
				 holder.category = listCategory.get(position);
				 
				 holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);

				 holder.txt_title.setText( listCategory.get(position).toString() );
				 
				 holder.img_category = (ImageView) convertView.findViewById(R.id.img_category);
				 
				 holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
	
				 holder.checkbox.setClickable(false);
				 
				 

				 /*if	 (listCategory.get(position).toString().equals("abitazione"))
				 		holder.img_category.setImageResource(R.drawable.home1);
				 else if (listCategory.get(position).toString().equals("supermercato"))
				 		holder.img_category.setImageResource(R.drawable.cart_shop);
				 else if (listCategory.get(position).toString().equals("abbigliamento"))
				 		holder.img_category.setImageResource(R.drawable.abbigliamento);
				 else if (listCategory.get(position).toString().equals("articolibambini"))
				 		holder.img_category.setImageResource(R.drawable.articolibambini);
				 else if (listCategory.get(position).toString().equals("bambino"))
						holder.img_category.setImageResource(R.drawable.bambini);
				 else if (listCategory.get(position).toString().equals("bar"))
						holder.img_category.setImageResource(R.drawable.bar);
				 else if (listCategory.get(position).toString().equals("cinema"))
						holder.img_category.setImageResource(R.drawable.cinema);
				 else if (listCategory.get(position).toString().equals("enoteca"))
						holder.img_category.setImageResource(R.drawable.enoteca);
				 else if (listCategory.get(position).toString().equals("fastfood"))
						holder.img_category.setImageResource(R.drawable.fastfood);
				 else if (listCategory.get(position).toString().equals("officina"))
						holder.img_category.setImageResource(R.drawable.officina);
				 else if (listCategory.get(position).toString().equals("paneteria"))
						holder.img_category.setImageResource(R.drawable.panetteria);
				 else if (listCategory.get(position).toString().equals("pasticceria"))
						holder.img_category.setImageResource(R.drawable.pasticceria);
				 else if (listCategory.get(position).toString().equals("ristorante"))
						holder.img_category.setImageResource(R.drawable.ristorante);
				 else if (listCategory.get(position).toString().equals("scarpe"))
						holder.img_category.setImageResource(R.drawable.scarpe);
				 else if (listCategory.get(position).toString().equals("teatro"))
						holder.img_category.setImageResource(R.drawable.teatro);
				 else if (listCategory.get(position).toString().equals("ufficio"))
						holder.img_category.setImageResource(R.drawable.ufficio);
				 else if (listCategory.get(position).toString().equals("cellulari e telefonia"))
						holder.img_category.setImageResource(R.drawable.cell); 
				 else if (listCategory.get(position).toString().equals("console e videogiochi"))
						holder.img_category.setImageResource(R.drawable.games); 
				 else if (listCategory.get(position).toString().equals("musica"))
						holder.img_category.setImageResource(R.drawable.music); 
				 else if (listCategory.get(position).toString().equals("giornalaio"))
						holder.img_category.setImageResource(R.drawable.book); 
				 else if (listCategory.get(position).toString().equals("biblioteca"))
						holder.img_category.setImageResource(R.drawable.book2); 
				 else if (listCategory.get(position).toString().equals("pizzeria"))
						holder.img_category.setImageResource(R.drawable.pizza);
				 else if (listCategory.get(position).toString().equals("alimentari"))
						holder.img_category.setImageResource(R.drawable.pizza);
				 */
				 
				 convertView.setTag(holder);
			 
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			 
			 holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);

			 holder.txt_title.setText(" " +  listCategory.get(position).toString() );
			 
			 holder.img_category = (ImageView) convertView.findViewById(R.id.img_category);
			 
			 holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
				
			 holder.checkbox.setClickable(false);
			 
			 if	 (listCategory.get(position).toString().contains("abitazione"))
			 		holder.img_category.setImageResource(R.drawable.home1);
			 else if (listCategory.get(position).toString().contains("supermercato"))
			 		holder.img_category.setImageResource(R.drawable.cart_shop);
			 else if (listCategory.get(position).toString().contains("abbigliamento"))
			 		holder.img_category.setImageResource(R.drawable.abbigliamento);
			 else if (listCategory.get(position).toString().contains("articoli bambino"))
					holder.img_category.setImageResource(R.drawable.artbambini);
			 else if (listCategory.get(position).toString().contains("bar"))
					holder.img_category.setImageResource(R.drawable.bar);
			 else if (listCategory.get(position).toString().contains("cinema"))
					holder.img_category.setImageResource(R.drawable.cinema);
			 else if (listCategory.get(position).toString().contains("enoteca"))
					holder.img_category.setImageResource(R.drawable.enoteca);
			 else if (listCategory.get(position).toString().contains("fastfood"))
					holder.img_category.setImageResource(R.drawable.fastfood);
			 else if (listCategory.get(position).toString().contains("officina"))
					holder.img_category.setImageResource(R.drawable.officina);
			 else if (listCategory.get(position).toString().contains("panetteria"))
					holder.img_category.setImageResource(R.drawable.panetteria);
			 else if (listCategory.get(position).toString().contains("pasticceria"))
					holder.img_category.setImageResource(R.drawable.pasticceria);
			 else if (listCategory.get(position).toString().contains("ristorante"))
					holder.img_category.setImageResource(R.drawable.ristorante);
			 else if (listCategory.get(position).toString().contains("scarpe"))
					holder.img_category.setImageResource(R.drawable.scarpe);
			 else if (listCategory.get(position).toString().contains("teatro"))
					holder.img_category.setImageResource(R.drawable.teatro);
			 else if (listCategory.get(position).toString().contains("ufficio"))
					holder.img_category.setImageResource(R.drawable.ufficio);
			 else if (listCategory.get(position).toString().contains("cellulari e telefonia"))
					holder.img_category.setImageResource(R.drawable.cell); 
			 else if (listCategory.get(position).toString().contains("giochi e console"))
					holder.img_category.setImageResource(R.drawable.games); 
			 else if (listCategory.get(position).toString().contains("musica"))
					holder.img_category.setImageResource(R.drawable.music); 
			 else if (listCategory.get(position).toString().contains("giornalaio"))
					holder.img_category.setImageResource(R.drawable.book); 
			 else if (listCategory.get(position).toString().contains("biblioteca"))
					holder.img_category.setImageResource(R.drawable.book2); 
			 else if (listCategory.get(position).toString().contains("pizzeria"))
					holder.img_category.setImageResource(R.drawable.pizza);
			 else if (listCategory.get(position).toString().contains("alimentari"))
					holder.img_category.setImageResource(R.drawable.alim);
			 else if (listCategory.get(position).toString().contains("iper"))
					holder.img_category.setImageResource(R.drawable.iper);
			 else if (listCategory.get(position).toString().contains("auto e moto"))
					holder.img_category.setImageResource(R.drawable.car);
			 else if (listCategory.get(position).toString().contains("informatica"))
					holder.img_category.setImageResource(R.drawable.computer); 
			 else if (listCategory.get(position).toString().contains("agenzia turistica"))
					holder.img_category.setImageResource(R.drawable.world); 
			 else if (listCategory.get(position).toString().contains("frutta e verdura"))
					holder.img_category.setImageResource(R.drawable.frutta1); 
			 else if (listCategory.get(position).toString().contains("sport e fitness"))
					holder.img_category.setImageResource(R.drawable.calcio); 
			 else if (listCategory.get(position).toString().contains("gioielleria"))
					holder.img_category.setImageResource(R.drawable.gioielleria); 
			 else if (listCategory.get(position).toString().contains("casa e giardino"))
					holder.img_category.setImageResource(R.drawable.garden); 
			 else if (listCategory.get(position).toString().contains("elettronica"))
					holder.img_category.setImageResource(R.drawable.elettro); 
			 else if (listCategory.get(position).toString().contains("libreria"))
					holder.img_category.setImageResource(R.drawable.libreria); 
			 else if (listCategory.get(position).toString().contains("piscina"))
					holder.img_category.setImageResource(R.drawable.piscina); 
			 else if (listCategory.get(position).toString().contains("ottica"))
					holder.img_category.setImageResource(R.drawable.ottica);
			 else if (listCategory.get(position).toString().contains("banca"))
					holder.img_category.setImageResource(R.drawable.banca);
			 else if (listCategory.get(position).toString().contains("elettrodomestici"))
					holder.img_category.setImageResource(R.drawable.elettrodomestici);
			 else if (listCategory.get(position).toString().contains("parucchiere"))
					holder.img_category.setImageResource(R.drawable.parucchiere);
			 else if (listCategory.get(position).toString().contains("finanza e assicurazioni"))
					holder.img_category.setImageResource(R.drawable.assicurazione);
			 else if (listCategory.get(position).toString().contains("centro benessere"))
					holder.img_category.setImageResource(R.drawable.benessere);
			 else if (listCategory.get(position).toString().contains("farmacia"))
					holder.img_category.setImageResource(R.drawable.farmacia);
			 else if (listCategory.get(position).toString().contains("ospedale"))
					holder.img_category.setImageResource(R.drawable.ospedale);
			 else if (listCategory.get(position).toString().contains("macelleria"))
					holder.img_category.setImageResource(R.drawable.meat);
			 else if (listCategory.get(position).toString().contains("poste"))
					holder.img_category.setImageResource(R.drawable.poste);
			 else if (listCategory.get(position).toString().contains("scuola"))
					holder.img_category.setImageResource(R.drawable.scuola);
			 
			 return convertView;
		 }

			 static class ViewHolder {
				 TextView txt_title;
				 ImageView img_category;
				 CheckBox checkbox;
				 String category;
				 
				 //SingleItemLocation itemlocation;
			 }

			@Override
			public int getCount() {
				
				return listCategory.size();
			}

			@Override
			public Object getItem(int position) {
				return listCategory.get(position);
			}

			@Override
			public long getItemId(int position) {
				
				return Long.parseLong(Integer.toString(listCategory.get(position).hashCode()));
			}


		 }
	
	/*public void finishSaveToAdd (boolean result, PlaceClient placeAdded) {
		 
		 if (result)
		 { 
			 
			 Toast.makeText(SelectCategory.this, "Place added!", Toast.LENGTH_LONG).show();
			 //PlaceClient place = new PlaceClient(placeAdded.title,placeAdded.streetAddress,placeAdded.streetNumber,placeAdded.cap,placeAdded.city,placeAdded.category);
			 //PrivatePlaces.placeListbackup.add(place);
			 //PrivatePlaces.addPrivate(placeAdded.title,placeAdded.streetAddress,placeAdded.streetNumber,placeAdded.cap,placeAdded.city,placeAdded.category);
			 /*intent = new Intent(getApplicationContext(), PlacesTab.class);
			 intent.putExtra("name", placeAdded.title);
			 intent.putExtra("streetAddress",placeAdded.streetAddress);
			 intent.putExtra("streetNumber", placeAdded.streetNumber);
			 intent.putExtra("cap", placeAdded.cap);
			 intent.putExtra("city", placeAdded.city);
			 intent.putExtra("category", placeAdded.category);
			 //startActivityForResult(intent,0);
				finish();
				
			 
		 }else
			 Toast.makeText(SelectCategory.this, R.string.saving_error, Toast.LENGTH_LONG).show();
			 
	}*/
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,CREATE_PLACE,0,R.string.createAssertion).setIcon(R.drawable.user_group_add);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case CREATE_PLACE:
			
			newPlace = new PlaceClient(name,streetAddress,streetNumber,cap,city,category);

			if (type.equals("Private"))
			{
				//creationNewPlace = PlacesResource.createPrivatePlace(newPlace,handler, SelectCategory.this);
			
			}else if (type.equals("Private")){
				//creationNewPlace = PlacesResource.createPublicPlace(newPlace,handler, SelectCategory.this);
		 	}
			
			finish();
	
		}
		return true;
	}*/
	
	
}


