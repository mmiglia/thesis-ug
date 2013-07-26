package com.thesisug.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
import com.thesisug.communication.valueobject.ActionLocationList;
import com.thesisug.communication.valueobject.Item;
import com.thesisug.communication.valueobject.ItemLocationList;
import com.thesisug.communication.valueobject.SingleActionLocation;
import com.thesisug.communication.valueobject.SingleItemLocation;


public class Vote_ont_db extends Activity {
	
	private static final String TAG = new String("thesisug - Vote_ont_db");
	private static Thread downloadAssertionsThread;
	TextView txt_Object;
	TextView txt_Ont;

	
	private final static Handler handler = new Handler();
	private List<SingleActionLocation> listActionlocatin;
	 Intent intent;
	String title;
	Button add_button;
	Button next_button;
	Button vote_button;
	TextView title2;
	TextView titleText;
	CheckBox checkBoxItem;
	TextView find;
	TextView text_ont;
	TextView user_voted;
	List<Item> item_List;
	ListIterator<Item> it;
	String location_for_location;
	ListView l1;
	List<String> locations;
	Item o;
	String l;
	List<String> locationsVoted;
	String loc_to_add;
	List<String> list_vote = new LinkedList<String>();
	List<String> list_vote_negative = new LinkedList<String>();
	List<String> list_dup = new LinkedList<String>();
	Thread VoteAssertion;

	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 
		 Intent intent=getIntent(); // l'intent di questa activity
		 title=intent.getStringExtra("title");
		
		 //Toast.makeText(Vote_ont_db.this,title,Toast.LENGTH_LONG).show();
		 //title="pane";

			setContentView(R.layout.vote_ont_db);
			 txt_Object = (TextView) findViewById(R.id.obj);
			 txt_Ont = (TextView) findViewById(R.id.text_ont);
		 
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
	     
		 add_button = (Button) findViewById(R.id.add_button);
		 vote_button = (Button) findViewById(R.id.vote_button);
		 next_button = (Button) findViewById(R.id.next_button);
		 title2 = (TextView) findViewById(R.id.title2);
		 titleText = (TextView) findViewById(R.id.title);
		 
		 user_voted = (TextView) findViewById(R.id.user_voted);
		 find = (TextView) findViewById(R.id.find);
		 text_ont = (TextView) findViewById(R.id.text_ont);
		 l1 = (ListView) findViewById(R.id.list_db);
		 
			
		 add_button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
					Custom_dialog_add_loc customizeDialog = new Custom_dialog_add_loc(Vote_ont_db.this);
					
					customizeDialog.show();
					//loc = customizeDialog.loc.toString();
					/*final AlertDialog.Builder alert = new AlertDialog.Builder(this);
					final EditText input = new EditText(getApplicationContext());
					alert.setView(input);
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String value = input.getText().toString().trim();
							Toast.makeText(getApplicationContext(), value,
									Toast.LENGTH_SHORT).show();
						}
					});

					alert.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									dialog.cancel();
								}
							});
					alert.show();
*/
					
					
					
					}
			});
		 
		 vote_button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
					ListIterator<String> iterator= list_vote.listIterator();
					String locationsList="";
					
					//crea l'iteratore
					while (iterator.hasNext()) {
						
						locationsList = locationsList + iterator.next().toString() + ",";
	
					}
					
					if (!locationsList.equals(""))
		               {
		                   int u=locationsList.lastIndexOf(",");
		                   locationsList=locationsList.substring(0, u);
		               }
					
					ListIterator<String> iterator_negative= list_vote_negative.listIterator();
					String locationsListNegative="";
					
					//crea l'iteratore
					while (iterator_negative.hasNext()) {
						
						locationsListNegative = locationsListNegative + iterator_negative.next().toString() + ",";
	
					}
					
					if (!locationsListNegative.equals(""))
		               {
		                   int u=locationsListNegative.lastIndexOf(",");
		                   locationsListNegative=locationsListNegative.substring(0, u);
		               }
					
					Log.i(TAG, "LISTA VOTI POSITIVI: " + locationsList);
					Log.i(TAG, "LISTA VOTI NEGATIVI: " + locationsListNegative);
				
				//Toast.makeText(getBaseContext(), locationsList, Toast.LENGTH_LONG).show();
				//Toast.makeText(getBaseContext(), locationsListNegative, Toast.LENGTH_LONG).show();
				
					
				/*if (o.itemActionType.equals("1") && !(list_vote.isEmpty())){
					//Toast.makeText(getBaseContext(), "VOTE ITEM", Toast.LENGTH_LONG).show();
					VoteAssertion = AssertionsResource.voteList(new ItemLocationList(o.name,locationsList,locationsListNegative),handler, Vote_ont_db.this);
				}else if (o.itemActionType.equals("0") && !(list_vote.isEmpty())){
					//Toast.makeText(getBaseContext(), "VOTE ACTION", Toast.LENGTH_LONG).show();	
					VoteAssertion = AssertionsResource.voteList_action(new ActionLocationList(o.name,locationsList,locationsListNegative),handler, Vote_ont_db.this);
				}else
					VoteAssertion = AssertionsResource.stop_vote(o.name,handler, Vote_ont_db.this);*/
		
					if (o.itemActionType.equals("1")) {
						//Toast.makeText(getBaseContext(), "VOTE ITEM", Toast.LENGTH_LONG).show();
						VoteAssertion = AssertionsResource.voteList(new ItemLocationList(o.name,locationsList,locationsListNegative),handler, Vote_ont_db.this);
					}else if (o.itemActionType.equals("0")) {
						//Toast.makeText(getBaseContext(), "VOTE ACTION", Toast.LENGTH_LONG).show();	
						VoteAssertion = AssertionsResource.voteList_action(new ActionLocationList(o.name,locationsList,locationsListNegative),handler, Vote_ont_db.this);
					}
					
				}	
				
				
				});
		 
		next_button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
					if (it.hasNext()) {
						
						o = it.next();
						
						if (o.itemActionType.equals("1")){
					    	title2.setText("The object:");
						  	find.setText("can be found in ");
						}
						else{
						  	title2.setText("The action: ");
						  	find.setText("can do in ");
						}
				  
						txt_Object.clearComposingText();
						txt_Ont.clearComposingText();
						txt_Object.setText(o.name);
				  
						if (o.ontologyList.equals(""))
							txt_Ont.setText("No match in the ontology!");
						else
							txt_Ont.setText(o.ontologyList);
				  
						locations = new ArrayList<String>();
			       
						String[] location = o.dbList.split(",");
						locations.addAll(Arrays.asList(location));
					
						//Toast.makeText(getBaseContext(), "Lista: " + locations, Toast.LENGTH_LONG).show();
						if(locations==null || locations.size()==0 || locations.get(0).equals("")){
							Toast.makeText(getBaseContext(), "No vote from users", Toast.LENGTH_LONG).show();
							locations.remove("");
						}else
						{
						
						l1.setAdapter(new assertionsListAdapter(Vote_ont_db.this,locations,list_vote));
						
						l1.setOnItemClickListener(new OnItemClickListener(){

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
								
								//checkBoxItem = (CheckBox)findViewById(R.id.checkbox);
									//LinkedHashMap item = (LinkedHashMap) (arg0.getItemAtPosition(arg2));
									//SingleItemLocation itemLocation = (SingleItemLocation) item.get(ITEM_DATA);
									//if (!checkBoxItem.isChecked())
									//{
								checkBoxItem =(CheckBox) arg1.findViewById(R.id.checkbox);	
								
								
								
								if (!checkBoxItem.isChecked())
								{
										list_vote_negative.remove(locations.get(arg2).toString());
										list_vote.add(locations.get(arg2).toString());
										checkBoxItem.setChecked(true);
									}
									else
									{
										list_vote.remove(locations.get(arg2).toString());
										list_vote_negative.add(locations.get(arg2).toString());
										checkBoxItem.setChecked(false);
									}	
									
									//Toast.makeText(getBaseContext(), "Details for "+locations.get(arg2).toString(), Toast.LENGTH_LONG).show();
									//Toast.makeText(getBaseContext(), list_vote.toString(), Toast.LENGTH_LONG).show();
									
							
							}
							 
						 });
						
						}
						
						titleText.setVisibility(View.VISIBLE);
						title2.setVisibility(View.VISIBLE);
						txt_Object.setVisibility(View.VISIBLE);
						find .setVisibility(View.VISIBLE);
						text_ont.setVisibility(View.VISIBLE); 
						user_voted.setVisibility(View.VISIBLE);
						l1 .setVisibility(View.VISIBLE);
						add_button.setVisibility(View.VISIBLE);
						vote_button.setVisibility(View.VISIBLE);
						next_button.setVisibility(View.VISIBLE);
							
						 
					}else
						 finish();
					}
			});
			
		 
		//downloadAssertionsThread = AssertionsResource.getAllAssertions(handler, this);
		//Get join to group request list 
		downloadAssertionsThread = AssertionsResource.checkInOntologyDb(title, handler, Vote_ont_db.this);
	}
	
	

	
	
public void afterAssertionsListLoaded(final List<Item> itemList){
	
	//setContentView(R.layout.vote_ont_db);
	//Toast.makeText(Vote_ont_db.this,itemList.toString(),Toast.LENGTH_LONG).show();
	
	if(itemList==null || itemList.size()==0){
		
		titleText.setVisibility(View.INVISIBLE);
		title2.setVisibility(View.INVISIBLE);
		txt_Object.setVisibility(View.INVISIBLE);
		find .setVisibility(View.INVISIBLE);
		text_ont.setVisibility(View.INVISIBLE); 
		user_voted.setVisibility(View.INVISIBLE);
		l1 .setVisibility(View.INVISIBLE);
		add_button.setVisibility(View.INVISIBLE);
		vote_button.setVisibility(View.INVISIBLE);
		next_button.setVisibility(View.INVISIBLE);
		
		
		final CharSequence[] items = {"Do you want to find something?", "Do you want to do something?", "Do you want to go to a place?"};

		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 builder.setTitle("No match in ontology & Db! Choose one:");
		 builder.setItems(items, new DialogInterface.OnClickListener() {

		     public void onClick(DialogInterface dialog, int item) {
		         //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
		    	 if (items[item].equals("Do you want to find something?")){
		    		 intent = new Intent(getApplicationContext(), Create_Assertion_item_NoDb.class);
					startActivityForResult(intent,0);
					finish();
		     		}else if (items[item].equals("Do you want to do something?"))
		     		{
			    		 intent = new Intent(getApplicationContext(), Create_Assertion_action.class);
						startActivityForResult(intent,0);
						finish();
			     	}else if (items[item].equals("Do you want to go to a place?"))
		     		{
			     		Custom_dialog_location customizeDialog_loc = new Custom_dialog_location(Vote_ont_db.this);
						customizeDialog_loc.show();
						
			     	}
		     			
		     			
		     }
		 });
		 AlertDialog alert = builder.create();
		 alert.show();
		 
		 
	}
	else
	{	
		item_List=itemList;
		//supponiamo che la lista venga nel frattempo riempita
		it = item_List.listIterator();
		//crea l'iteratore
		if (it.hasNext()) {
			
			o = it.next();

			if (o.nScreen.equals("5"))
			{
				
				if (it.hasNext()) 	
					o = it.next();
				
				else{
					titleText.setVisibility(View.INVISIBLE);
					title2.setVisibility(View.INVISIBLE);
					txt_Object.setVisibility(View.INVISIBLE);
					find .setVisibility(View.INVISIBLE);
					text_ont.setVisibility(View.INVISIBLE); 
					user_voted.setVisibility(View.INVISIBLE);
					l1 .setVisibility(View.INVISIBLE);
					add_button.setVisibility(View.INVISIBLE);
					vote_button.setVisibility(View.INVISIBLE);
					next_button.setVisibility(View.INVISIBLE);
					
					
					ProgressDialog dialog = ProgressDialog.show(Vote_ont_db.this, "", 
	                        "Loading. Please wait...", true);
					
					dialog.show();
					
					finish();
				}
				
				
				
			}else
				
			{
				
				
				if (o.itemActionType.equals("1")){
			    	title2.setText("The object:");
				  	find.setText("can be found in ");
				}
				else{
				  	title2.setText("The action: ");
				  	find.setText("can do in ");
				}
		  
				txt_Object.clearComposingText();
				txt_Ont.clearComposingText();
				txt_Object.setText(o.name);
		  
				if (o.ontologyList.equals(""))
					txt_Ont.setText("No match in the ontology!");
				else
					txt_Ont.setText(o.ontologyList);
		  
				locations = new ArrayList<String>();
	       
				String[] location = o.dbList.split(",");
				locations.addAll(Arrays.asList(location));
				//Toast.makeText(getBaseContext(), "Lista: " + locations, Toast.LENGTH_LONG).show();
				
				if(locations==null || locations.size()==0 || locations.get(0).equals("")){
					Toast.makeText(getBaseContext(), "No vote from users", Toast.LENGTH_LONG).show();
					locations.remove("");
					
				}else
				{	
					//Toast.makeText(getBaseContext(), "Lista: " + locations, Toast.LENGTH_LONG).show();
					
					ListIterator<String> iterator= locations.listIterator();
					
					
					//crea l'iteratore
					while (iterator.hasNext()) {
						
						l = iterator.next().toString();
						list_dup.add(l);
						list_vote_negative.add(l);
	
					}
					
					l1.setAdapter(new assertionsListAdapter(this,locations,list_vote));
				
					l1.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						
						//checkBoxItem = (CheckBox)findViewById(R.id.checkbox);
							//LinkedHashMap item = (LinkedHashMap) (arg0.getItemAtPosition(arg2));
							//SingleItemLocation itemLocation = (SingleItemLocation) item.get(ITEM_DATA);
							//if (!checkBoxItem.isChecked())
							//{
							checkBoxItem =(CheckBox) arg1.findViewById(R.id.checkbox);	
						
						
						
							if (!checkBoxItem.isChecked())
							{
								list_vote_negative.remove(locations.get(arg2).toString());
								list_vote.add(locations.get(arg2).toString());
								
								checkBoxItem.setChecked(true);
							}
							else
							{
								list_vote.remove(locations.get(arg2).toString());
								list_vote_negative.add(locations.get(arg2).toString());
								checkBoxItem.setChecked(false);
							}	
							
							//Toast.makeText(getBaseContext(), "Details for "+locations.get(arg2).toString(), Toast.LENGTH_LONG).show();
							//Toast.makeText(getBaseContext(), list_vote.toString(), Toast.LENGTH_LONG).show();
							
					
						}
					 
					});
				}
				
				
				titleText.setVisibility(View.VISIBLE);
				title2.setVisibility(View.VISIBLE);
				txt_Object.setVisibility(View.VISIBLE);
				find .setVisibility(View.VISIBLE);
				text_ont.setVisibility(View.VISIBLE); 
				user_voted.setVisibility(View.VISIBLE);
				l1 .setVisibility(View.VISIBLE);
				add_button.setVisibility(View.VISIBLE);
				vote_button.setVisibility(View.VISIBLE);
				next_button.setVisibility(View.VISIBLE);
				
	
			}
		
		
		}
		

	  }
	
	
	
	
	  /*
	for (Item o : itemList) 
		{
			//Toast.makeText(Vote_ont_db.this,o.name,Toast.LENGTH_LONG).show();
			
			txt_Object.append(o.name);
			txt_Ont.append(o.ontologyList);
			
			List<String> locations = new ArrayList<String>();
		       
			String[] location = o.dbList.split(",");
		       locations.addAll(Arrays.asList(location));
			
		       l1 = (ListView) findViewById(R.id.list_db);
			l1.setAdapter(new assertionsListAdapter(this,locations));
			
		}
				
	
	
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

		 private List<String> Locations;
		 private List<String> locChecked;
		 
		 
		 public assertionsListAdapter(Context context,List<String> list, List<String> locChecked1) {
			 mInflater = LayoutInflater.from(context);
			 Locations=list;
			 locChecked = locChecked1;

		 }

		 public View getView(int position, View convertView, ViewGroup parent) 
		 {
		
			 final ViewHolder holder;
			 if (convertView == null) {
				 
				 convertView = mInflater.inflate(R.layout.list_item_db, null);
				 holder = new ViewHolder();
				 
				 
				 holder.location = Locations.get(position);
	
				 holder.txt_location = (TextView) convertView.findViewById(R.id.txt_location);
				 
				 holder.txt_location.setText(Locations.get(position).toString());
				 
				 holder.check = (CheckBox) convertView.findViewById(R.id.checkbox);
				 
	
				 holder.check.setClickable(false);
				 
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			 
			 holder.txt_location.setText(Locations.get(position).toString());
			 
			 
			 
			 if (locChecked.contains(holder.location))
			 {
 
				 holder.check.setChecked(true);
	 
			 }
			
			 	holder.check.setClickable(false);
			
			 return convertView;
		 }

			 static class ViewHolder {
				 
				 TextView txt_location;
				 CheckBox check;
				 String location;
				 List<String> locChecked;
			 }

			@Override
			public int getCount() {
				
				return Locations.size();
			}

			@Override
			public Object getItem(int position) {
				return Locations.get(position);
			}

			@Override
			public long getItemId(int position) {
				
				return Long.parseLong(Integer.toString(Locations.get(position).hashCode()));
			}
			


		 }
	
	public void imp_loc(final String location)
	
	{
		loc_to_add = location;
		list_vote.add(loc_to_add);
		if (o.itemActionType.equals("1"))
		{
			
			SingleItemLocation n = new SingleItemLocation(o.name,loc_to_add);
			
			Thread creationAssertionItem = AssertionsResource.createItemLocation(n,
					handler, Vote_ont_db.this);
			
			
		}else if (o.itemActionType.equals("0"))
		{
			SingleActionLocation n = new SingleActionLocation(o.name,loc_to_add);
			
			Thread creationAssertionAction = AssertionsResource.createActionLocation(n,
					handler, Vote_ont_db.this);
			
		}	
			
		//Toast.makeText(Vote_ont_db.this,loc_to_add,Toast.LENGTH_LONG).show();
	}
	
public void imp_location(final String location)
	
	{
		location_for_location = location;
		
		Thread creationAssertionAction = AssertionsResource.addLocation(title,location_for_location,handler, Vote_ont_db.this);
		
		finish();
	}
	
	public class Custom_dialog_add_loc extends Dialog implements OnClickListener {
		Button add_button;
		Button cancel_button;
		EditText location;
		String loc;

		public Custom_dialog_add_loc(Context context) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.customize_dialog);
		
		 //add_button = (Button) findViewById(R.id.add_button);
		 //cancel_button = (Button) findViewById(R.id.cancel_button);
		location = (EditText)findViewById(R.id.location);
		add_button = (Button) findViewById(R.id.add_button);
		add_button.setOnClickListener(this);
		
		cancel_button = (Button) findViewById(R.id.cancel_button);
		cancel_button.setOnClickListener(this);
		
		}

		@Override
		public void onClick(View v) {
		/** When OK Button is clicked, dismiss the dialog */
		if (v == add_button){
			if (location.getText().toString().equals("") )
			{
				Toast.makeText(Vote_ont_db.this, "Empty fields!Compile it!",Toast.LENGTH_LONG).show();
				
			}else if (list_dup.contains(location.getText().toString()))
			{
				Toast.makeText(Vote_ont_db.this, "Location already exit!",Toast.LENGTH_LONG).show();
			}else if ( txt_Ont.getText().toString().contains(location.getText().toString()) ){
				Toast.makeText(Vote_ont_db.this, "Location already exist in the ontology!",Toast.LENGTH_LONG).show();
			
			}else 
			{
				loc= location.getText().toString();
				imp_loc(loc);
				dismiss();
				}
			}
		else if (v == cancel_button)
			dismiss();
		}

		}
	
	
	public class Custom_dialog_location extends Dialog implements OnClickListener {
		Button add_button;
		Button cancel_button;
		EditText location;
		String loc;

		public Custom_dialog_location(Context context) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.customize_dialog_loc_for_loc);
		
		 //add_button = (Button) findViewById(R.id.add_button);
		 //cancel_button = (Button) findViewById(R.id.cancel_button);
		location = (EditText)findViewById(R.id.location);
		add_button = (Button) findViewById(R.id.add_button);
		add_button.setOnClickListener(this);
		
		cancel_button = (Button) findViewById(R.id.cancel_button);
		cancel_button.setOnClickListener(this);
		
		}

		@Override
		public void onClick(View v) {
		/** When OK Button is clicked, dismiss the dialog */
		if (v == add_button){
			
			if (location.getText().toString().equals(""))
			{
				Toast.makeText(Vote_ont_db.this, "Empty fields!Compile it!",Toast.LENGTH_LONG).show();
				
			}else{
				loc= location.getText().toString();
				imp_location(loc);
				dismiss();
		
			}
		}
		else if (v == cancel_button)
		{
			//if (location.getText().toString().equals(""))
			//{
				Toast.makeText(Vote_ont_db.this, "Empty fields!If you want that the program to help you solve the task compile it next times!",Toast.LENGTH_LONG).show();
			//}else	
				
				dismiss();
				finish();
				
		
			}
		}
		}
	
	
	public void finishSave (boolean result) {
		 
		 if (result)
		 { 
			Toast.makeText(Vote_ont_db.this, R.string.edit_success,Toast.LENGTH_LONG).show();
			locations.add(loc_to_add);
			l1.setAdapter(new assertionsListAdapter(Vote_ont_db.this,locations,list_vote));
			//check.setChecked(true);

		 	/*intent = new Intent();
			intent.putExtra("item", editObject.getText().toString());
			intent.putExtra("location", editLocation.getText().toString());
			intent.putExtra("description", editDescription.getText().toString());
			setResult(RESULT_OK, intent);
			finish();*/
		 }
		 else
			 Toast.makeText(Vote_ont_db.this, R.string.saving_error, Toast.LENGTH_LONG).show();
		 
		 
	    	
	    }
	
	public void finishSave_Loc (boolean result) {
		 
		 if (result)
		 { 
			Toast.makeText(Vote_ont_db.this, R.string.edit_success,Toast.LENGTH_LONG).show();
			

		 	/*intent = new Intent();
			intent.putExtra("item", editObject.getText().toString());
			intent.putExtra("location", editLocation.getText().toString());
			intent.putExtra("description", editDescription.getText().toString());
			setResult(RESULT_OK, intent);
			finish();*/
		 }
		 else
			 Toast.makeText(Vote_ont_db.this, R.string.saving_error, Toast.LENGTH_LONG).show();
		 
		 
	    	
	    }
	
	
	public void finishSave_voted (boolean result) {
		 
		 if (result)
		 { 
			Toast.makeText(Vote_ont_db.this, R.string.edit_success,Toast.LENGTH_LONG).show();
			vote_button.setEnabled(false);

		 }
		 else
			 Toast.makeText(Vote_ont_db.this, R.string.saving_error, Toast.LENGTH_LONG).show();
		 
		 
	    	
	    }
	
	public void finishSave_stop_vote (boolean result,String object) {
		 
		 if (result)
		 { 
			Toast.makeText(Vote_ont_db.this, "Stop votation for " + object + " ! ",Toast.LENGTH_LONG).show();
			vote_button.setEnabled(false);
			add_button.setEnabled(false);

		 }
		 else
			 Toast.makeText(Vote_ont_db.this, R.string.saving_error, Toast.LENGTH_LONG).show();
		 
		 
	    	
	    }
	
}
