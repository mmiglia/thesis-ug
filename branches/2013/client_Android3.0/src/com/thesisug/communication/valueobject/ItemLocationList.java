package com.thesisug.communication.valueobject;

import javax.xml.bind.annotation.XmlRootElement;

/* 28-5-2011
* 
* This is the basic Item-LocationList object voted by a client that will be used for communication
* to clients.
* 
* @author: Anuska
*/

@XmlRootElement
public class ItemLocationList {
   
   public String item;
   public String locations;
   public String locationsNegative;
   
   public ItemLocationList()
   {
       super();
   }
   
   /**
    * Constructor for this class     
    * @param item
    */
   public ItemLocationList(String item,String locations, String locationsNegative) 
   {
       this.item = item;
       this.locations = locations;
       this.locationsNegative = locationsNegative;
       
   }

}
