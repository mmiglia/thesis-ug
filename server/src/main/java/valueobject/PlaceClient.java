package valueobject;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlaceClient {
   
   public String title;
   public String lat; 
   public String lng; 
   public String streetAddress;
   public String streetNumber;
   public String cap;
   public String city;
   public String category; //stringa che contiene la lista di categorie 
   						   //separate da una virgola

   public PlaceClient()
   {
       super();
   }
   
   /**
    * Constructor for this class     
    * @param Place
    */
   public PlaceClient(String title,String lat,String lng,String streetAddress,String streetNumber,String cap, String city,String category) 
   {
       this.title = title;
       this.lat = lat;
       this.lng = lng;
       this.streetAddress = streetAddress;
       this.streetNumber = streetNumber;
       this.cap = cap;
       this.city = city;
       this.category = category;
   }
}