package com.thesisug.communication.valueobject;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import android.os.Parcel;
import android.os.Parcelable;

import com.thesisug.communication.valueobject.Hint.PhoneNumber;


public class Item implements Parcelable{
   
   public String name;
   public String nScreen; //il tipo di schermata
   public String itemActionType; //1=item,0=action 
   public String ontologyList;
   public String dbList;

   
   public Item()
   {
       
	   /*ontologyList = new LinkedList<String>();
      dbList = new LinkedList<String>();
       */
   }
   
   /**
    * Constructor for this class     
    * @param item
    */
   public Item(String name,String nScreen,String itemActionType,String ontologyList,String dbList) 
   {
       this.name = name;
       this.nScreen = nScreen;
       this.itemActionType = itemActionType;
       this.ontologyList = ontologyList;
       this.dbList = dbList;
   }
   
   @Override
   public String toString() {
       return build(this);
   }
   
   
   public Item copy(){
       Item item1=new Item();
       
       item1.name=name;
       item1.nScreen=nScreen;
       item1.itemActionType=itemActionType;
       item1.ontologyList=ontologyList;
       item1.dbList=dbList;
       //item1.dbList=dbList;
       /*for (String o : ontologyList){
           item1.ontologyList.add(new String(o));
       }/*
       for (String o : dbList){
           item1.dbList.add(new String);
       }*/
       return item1;
   }
   
   private static String build(Object obj) {
       StringBuilder builder = new StringBuilder();

       if (obj != null) {
           Class clazz = obj.getClass();
           Field[] fields = clazz.getDeclaredFields();

           if (fields != null) {
               try {
                   AccessibleObject.setAccessible(fields, true);
                   appendFields(builder, fields, obj);
               } catch (Exception ex) {
                   ex.printStackTrace();
               }
           }
       }
       return builder.toString();
   }

   private static void appendFields(StringBuilder builder, Field[] fields,
           Object obj) {
       Class clazz = obj.getClass();
       for (int i = 0; i < fields.length; i++) {
           Field f = fields[i];

           if (Modifier.isStatic(f.getModifiers())) {
               continue;
           }

           try {
               Object value = f.get(obj);
               if (!f.getName().equalsIgnoreCase("class")) {
                   builder.append(clazz.getSimpleName());
                   builder.append(".");
                   builder.append(f.getName());
                   builder.append(": ");
                   builder.append(String.valueOf(value));
                   builder.append("\n");
               }
           } catch (Exception ignore) {
               // ignored
           }
       }
   }
   
   @Override
   public int describeContents() {
       return 0;
   }
   
   @Override
   public void writeToParcel(Parcel out, int flag) {
       out.writeString(name);
       out.writeString(nScreen);
       out.writeString(itemActionType);
       out.writeString(ontologyList);
       out.writeString(dbList);

      // out.writeInt(ontologyList.size());
       //for (String s : ontologyList) out.writeString (s);
       /*out.writeInt(dbList.size());
       for (String s : dbList) out.writeString (s);
   */    
   }
   private void readFromParcel(Parcel in) {
       name = in.readString();
       nScreen = in.readString();
       itemActionType = in.readString();
       ontologyList = in.readString();
       dbList = in.readString();

       /* int count = in.readInt();
 
       for (int i=0; i<count; i++)    ontologyList.add(in.readString());
       count = in.readInt();
       
       for (int i=0; i<count; i++)    dbList.add(in.readString());
       */
   }

   private Item(Parcel in){
       this();
       readFromParcel(in);
   }

   public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
       public Item createFromParcel(Parcel in) {
           return new Item(in);
       }

       public Item[] newArray(int size) {
           return new Item[size];
       }
   };
   

}