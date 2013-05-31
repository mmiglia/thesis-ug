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
public class ActionLocationList {
   
   public String action;
   public String locations;
   public String locationsNegative;
   
   
   public ActionLocationList()
   {
       super();
   }
   
   /**
    * Constructor for this class     
    * @param item
    */
   public ActionLocationList(String action,String locations, String locationsNegative) 
   {
       this.action = action;
       this.locations = locations;
       this.locationsNegative =locationsNegative;
       
   }
   

}
