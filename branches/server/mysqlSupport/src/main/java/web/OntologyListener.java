package web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleItemLocation;
import valueobject.SingleActionLocation;
import valueobject.SingleLocationLocation;
import valueobject.Location;
import valueobject.Item;
import valueobject.ItemLocationList;
import valueobject.ActionLocationList;

import businessobject.OntologyManager;
import businessobject.OntologyReasoner;


/**
 * Responsible for add/delete/vote assertion to update ontology file
 * @author Anuska
 */

@Path("/{username}/ontology")

public class OntologyListener {
	private static Logger log = LoggerFactory.getLogger(OntologyListener.class);
/*
 * Funzione per controllare nell'ontologia una frase e  ricercare  nell'ontologia
 *  e nel database ogni parola e vedere dove si può trovare(cioè la location)
 * @return ritorna una lista di oggetti di tipo Item, 
 * se non c'è nessuna corrispondenza né nell'ontologia né nel database 
 * torna una lista vuota
 */
	@GET
	@Path("/checkInOntologyDb")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Item> checkInOntologyDb(@PathParam("username") String userid, 
			@QueryParam("title") String title,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to check title from user " + userid + 
				", session "+ sessionid);
		
		List<Item> queryListItem = new ArrayList<Item>(); // list of inferred search query string
		
		/*
		 * Controllo se c'è corrispondenza nel db Location(cioè mi ritorna 
		 * una stringa diversa da ""), se si torno un item=5,
		 * altrimenti controllo parola per parola
		 */
		String location = OntologyManager.getInstance().findLocation(userid, title.toLowerCase());
		if (!location.equalsIgnoreCase(""))
		{	
			Item item= new Item();
			item.name=title;
			item.itemActionType=2; //location
			item.ontologyList="";
			item.dbList="";
			item.nScreen=5;
			queryListItem.add(item);
		}
		else
		{
		
			List<String> needs = new ArrayList<String>();
			String[] words = title.split(" ");
			needs.addAll(Arrays.asList(words));
	
			// remove duplicates by using HashSet
			HashSet<String> needsfilter = new HashSet<String>(needs);
			needs.clear();
			needs.addAll(needsfilter);
		
			for (String o : needs)
			{	List<String> ontList = new ArrayList<String>();
				List<String> dList = new ArrayList<String>();
				int u;
				boolean isLocation=false;
				Item item= new Item();
				item.name=o;
				
				List<String> list1 = OntologyManager.getInstance().viewLocationForItemVoted(userid,o);
				System.out.println("list1:"+ list1);
				List<String> list2 = OntologyManager.getInstance().viewLocationForActionVoted(userid,o);
				System.out.println("list2:"+ list2);
			
				boolean hasVoted = OntologyManager.getInstance().hasVoted(userid,o);
				//l'utente non ha votato niente per quell'item
				//if (list1.isEmpty() && list2.isEmpty())
				
				// se l'utente non ha premuto il tasto per dire che non vuole che 
				//gli compaia più la schermata faccio tutti i controlli 
				//
				if (!hasVoted)
				{
					
					ontList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
					
					for (String l : ontList)
						item.ontologyList=item.ontologyList+l+",";
					if (!item.ontologyList.equals(""))
					{
						u=item.ontologyList.lastIndexOf(",");
						item.ontologyList=item.ontologyList.substring(0, u);
					}
			
					if (!ontList.isEmpty())
						if (OntologyReasoner.getInstance().isItem(o))
						{	
							item.itemActionType=1;	//è un Item
							dList.addAll(OntologyManager.getInstance().viewLocationForItem(userid,o));
						}
						else
						{	
							item.itemActionType=0;	//è un Action
							dList.addAll(OntologyManager.getInstance().viewLocationForAction(userid,o));
						}
					else
					{
						dList.addAll(OntologyManager.getInstance().viewLocationForItem(userid,o));
						if (!dList.isEmpty())
						{	
							item.itemActionType=1;		// è un Item
						}	
						else	
						{	
							dList.addAll(OntologyManager.getInstance().viewLocationForAction(userid,o));
							if (!dList.isEmpty())
								item.itemActionType=0; //è un Action
						}
					}		
					
					
					for (String l : dList)
						item.dbList=item.dbList+l+",";
					if (!item.dbList.equals(""))
					{
						u=item.dbList.lastIndexOf(",");
						item.dbList=item.dbList.substring(0, u);
					}
					
						
					if (ontList.isEmpty())
						if (dList.isEmpty())
						{	//non ho trovato niente né in ontologia né in db 
							//quindi non lo inserisco nella lista di ritorno
							item.nScreen=4;
						}
						else 
						{
							//non ho trovato niente in ontologia ma qualcosa nel db
							item.nScreen=3; 
							queryListItem.add(item);
						}
					else 
					{	
						if (OntologyReasoner.getInstance().isLocation(o))
						{	
							item.itemActionType=2;	//è una Location
							item.ontologyList="";
							item.dbList="";
							item.nScreen=5;
							queryListItem.add(item);
						}
						else
						{
							if (dList.isEmpty())
							{
								//ho trovato qualcosa in ontologia, ma niente nel db
								item.nScreen=2;
								queryListItem.add(item);
							}
							else
							{	
								//ho trovato sia in ontologia sia in db
								item.nScreen=1;
								queryListItem.add(item);
							}
						}
					}
				}
					
						
				/*
				 * se l'utente ha già votato per quell'item ritorno nScreen=5 che significa
				 * che non compare più la schermata di votazione perchè ha già votato
				 */
				//se l'utente ha deciso che non vuole più votare per quell'item
				else 
				{	if (!list1.isEmpty())
					{
						item.itemActionType=1; //item
						item.dbList="";
						System.out.println("!list1.isEmpty()");
					}
					else
					{
						item.itemActionType=0; //action
						item.dbList="";
						System.out.println("item.itemActionType="+item.itemActionType);
						System.out.println("item.dbList="+item.dbList);
					}
					ontList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
					item.ontologyList="";
					item.nScreen=5;
					queryListItem.add(item);
				}
			
			}
		}
		return queryListItem;
	}	
		
	/*
	 * L'utente {username} dice che non vuole più essere disturbato riguardo un dato oggetto(item o action)
	 */
	@GET
	@Path("/vote")	
	public void vote(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("object") String object)
	{
			log.info("Request to vote object from user " + userid + ", session "+ sessionid);
			OntologyManager.getInstance().vote(userid,object);
	}
	
	/*
	 * L'utente {username} dice che non vuole più essere disturbato riguardo un dato oggetto(item o action)
	 */
	@GET
	@Path("/hasVoted")	
	public boolean hasVoted(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("object") String object)
	{
			log.info("Request to hasVote from user " + userid + ",for object "+object+", session "+ sessionid);
			boolean toReturn=OntologyManager.getInstance().hasVoted(userid,object);
			System.out.println(toReturn);
			return toReturn;
	}
	
	
/*	@GET
	@Path("/checkInOntologyDb")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Item> checkInOntologyDb(@PathParam("username") String userid, 
			@QueryParam("title") String title,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to check title from user " + userid + 
				", session "+ sessionid);
		
		List<Item> queryListItem = new ArrayList<Item>(); // list of inferred search query string
		
		/*
		 * Controllo se c'è corrispondenza nel db Location(cioè mi ritorna 
		 * una stringa diversa da ""), se si torno un item=5,
		 * altrimenti controllo parola per parola
		 *
		String location = OntologyManager.getInstance().findLocation(userid, title.toLowerCase());
		if (!location.equalsIgnoreCase(""))
		{	
			Item item= new Item();
			item.name=title;
			item.itemActionType=2; //location
			item.ontologyList="";
			item.dbList="";
			item.nScreen=5;
			queryListItem.add(item);
		}
		else
		{
		
			List<String> needs = new ArrayList<String>();
			String[] words = title.split(" ");
			needs.addAll(Arrays.asList(words));
	
			// remove duplicates by using HashSet
			HashSet<String> needsfilter = new HashSet<String>(needs);
			needs.clear();
			needs.addAll(needsfilter);
		
			for (String o : needs)
			{	List<String> ontList = new ArrayList<String>();
				List<String> dList = new ArrayList<String>();
				int u;
				boolean isLocation=false;
				Item item= new Item();
				item.name=o;
			
				List<String> list1 = OntologyManager.getInstance().viewLocationForItemVoted(userid,o);
				System.out.println("list1:"+ list1);
				List<String> list2 = OntologyManager.getInstance().viewLocationForActionVoted(userid,o);
				System.out.println("list2:"+ list2);
				//l'utente non ha votato niente per quell'item
				if (list1.isEmpty() && list2.isEmpty())
				{
					ontList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
					
					for (String l : ontList)
						item.ontologyList=item.ontologyList+l+",";
					if (!item.ontologyList.equals(""))
					{
						u=item.ontologyList.lastIndexOf(",");
						item.ontologyList=item.ontologyList.substring(0, u);
					}
			
					if (!ontList.isEmpty())
						if (OntologyReasoner.getInstance().isItem(o))
						{	
							item.itemActionType=1;	//è un Item
							dList.addAll(OntologyManager.getInstance().viewLocationForItem(userid,o));
						}
						else
						{	
							item.itemActionType=0;	//è un Action
							dList.addAll(OntologyManager.getInstance().viewLocationForAction(userid,o));
						}
					else
					{
						dList.addAll(OntologyManager.getInstance().viewLocationForItem(userid,o));
						if (!dList.isEmpty())
						{	
							item.itemActionType=1;		// è un Item
						}	
						else	
						{	
							dList.addAll(OntologyManager.getInstance().viewLocationForAction(userid,o));
							if (!dList.isEmpty())
								item.itemActionType=0; //è un Action
						}
					}		
					
					
					for (String l : dList)
						item.dbList=item.dbList+l+",";
					if (!item.dbList.equals(""))
					{
						u=item.dbList.lastIndexOf(",");
						item.dbList=item.dbList.substring(0, u);
					}
					
						
					if (ontList.isEmpty())
						if (dList.isEmpty())
						{	//non ho trovato niente né in ontologia né in db 
							//quindi non lo inserisco nella lista di ritorno
							item.nScreen=4;
						}
						else 
						{
							//non ho trovato niente in ontologia ma qualcosa nel db
							item.nScreen=3; 
							queryListItem.add(item);
						}
					else 
					{	
						if (OntologyReasoner.getInstance().isLocation(o))
						{	
							item.itemActionType=2;	//è una Location
							item.ontologyList="";
							item.dbList="";
							item.nScreen=5;
							queryListItem.add(item);
						}
						else
						{
							if (dList.isEmpty())
							{
								//ho trovato qualcosa in ontologia, ma niente nel db
								item.nScreen=2;
								queryListItem.add(item);
							}
							else
							{	
								//ho trovato sia in ontologia sia in db
								item.nScreen=1;
								queryListItem.add(item);
							}
						}
					}
				}
					
						
				/*
				 * se l'utente ha già votato per quell'item ritorno nScreen=5 che significa
				 * che non compare più la schermata di votazione perchè ha già votato
				 *
				else //if (list1.isEmpty() && list2.isEmpty())
				{	if (!list1.isEmpty())
					{
						item.itemActionType=1; //item
						item.dbList="";
						System.out.println("!list1.isEmpty()");
					}
					else
					{
						item.itemActionType=0; //action
						item.dbList="";
						System.out.println("item.itemActionType="+item.itemActionType);
						System.out.println("item.dbList="+item.dbList);
					}
					ontList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
					item.ontologyList="";
					item.nScreen=5;
					queryListItem.add(item);
				}
			
			}
		}
		return queryListItem;
	}	
	*/
	
	/*
	 * Funzione per controllare nell'ontologia una frase e  ricercare  nell'ontologia
	 *  e nel database ogni parola e vedere dove si può trovare(cioè la location)
	 * @return ritorna una lista di oggetti di tipo Item, 
	 * se non c'è nessuna corrispondenza né nell'ontologia né nel database 
	 * torna una lista vuota
	 
	        @GET
	        @Path("/checkInOntologyDb")        
	        @Consumes("application/xml")
	        @Produces("application/xml")
	        public List<Item> checkInOntologyDb(@PathParam("username") String userid, 
	                        @QueryParam("title") String title,
	                        @CookieParam("sessionid") String sessionid) 
	        {
	                log.info("Request to check title from user " + userid + 
	                                ", session "+ sessionid);
	                
	                List<String> needs = new ArrayList<String>();
	                String[] words = title.split(" ");
	                needs.addAll(Arrays.asList(words));
	        
	                // remove duplicates by using HashSet
	                HashSet<String> needsfilter = new HashSet<String>(needs);
	                needs.clear();
	                needs.addAll(needsfilter);
	                
	                
	                List<Item> queryListItem = new ArrayList<Item>(); // list of inferred search query string
	                for (String o : needs)
	                {        List<String> ontList = new ArrayList<String>();
	                    List<String> dList = new ArrayList<String>();
	                
	                   
	                        Item item= new Item();
	                        item.name=o;
	                        
	                        List<String> list1 = OntologyManager.getInstance().viewLocationForItemVoted(userid,o);
	                        System.out.println("list1:"+ list1);
	                        List<String> list2 = OntologyManager.getInstance().viewLocationForActionVoted(userid,o);
	                        System.out.println("list2:"+ list2);
	                        //l'utente non ha votato niente per quell'item
	                        if (list1.isEmpty() && list2.isEmpty())
	                        {
	                                ontList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
	                                item.ontologyList=ontList;
	                        
	                                if (!ontList.isEmpty())
	                                        if (OntologyReasoner.getInstance().isItem(o))
	                                        {        
	                                                item.itemActionType=1;        //è un Item
	                                                dList.addAll(OntologyManager.getInstance().viewLocationForItem(userid,o));
	                                        }
	                                        else
	                                        {        
	                                                item.itemActionType=0;        //è un Action
	                                                dList.addAll(OntologyManager.getInstance().viewLocationForAction(userid,o));
	                                        }
	                                else
	                                {
	                                        dList.addAll(OntologyManager.getInstance().viewLocationForItem(userid,o));
	                                        if (!dList.isEmpty())
	                                        {        
	                                                item.itemActionType=1;                // è un Item
	                                        }        
	                                        else        
	                                        {        
	                                                dList.addAll(OntologyManager.getInstance().viewLocationForAction(userid,o));
	                                                if (!dList.isEmpty())
	                                                        item.itemActionType=0; //è un Action
	                                        }
	                                }                
	                                        
	                                item.dbList=dList;
	                
	                                if (item.ontologyList.isEmpty())
	                                        if (item.dbList.isEmpty())
	                                        {        //non ho trovato niente né in ontologia né in db 
	                                                //quindi non lo inserisco nella lista di ritorno
	                                                item.nScreen=4;
	                                        }
	                                        else 
	                                        {
	                                                //non ho trovato niente in ontologia ma qualcosa nel db
	                                                item.nScreen=3; 
	                                                queryListItem.add(item);
	                                        }
	                                else 
	                                        if (item.dbList.isEmpty())
	                                        {
	                                                //ho trovato qualcosa in ontologia, ma niente nel db
	                                                item.nScreen=2;
	                                                queryListItem.add(item);
	                                        }
	                                        else
	                                        {        
	                                                //ho trovato sia in ontologia sia in db
	                                                item.nScreen=1;
	                                                queryListItem.add(item);
	                                        }
	                         
	                        }
	                        /*
	                         * se l'utente ha già votato per quell'item ritorno nScreen=5 che significa
	                         * che non compare più la schermata di votazione perchè ha già votato
	                         */
	/*
	                        else //if (list1.isEmpty() && list2.isEmpty())
	                        {        if (!list1.isEmpty())
	                            {
	                                        item.itemActionType=1; //item
	                                        item.dbList=list1;
	                                        System.out.println("!list1.isEmpty()");
	                            }
	                                else
	                                {
	                                        item.itemActionType=0; //action
	                                        item.dbList=list2;
	                                        System.out.println("item.itemActionType="+item.itemActionType);
	                                        System.out.println("item.dbList="+item.dbList);
	                                }
	                                ontList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
	                                item.ontologyList=ontList;
	                                item.nScreen=5;
	                                queryListItem.add(item);
	                        }
	                        
	                }
	                return queryListItem;
	        }	
*/	
	
//ITEM	
	@GET
	@Path("/addItemInLocation")	
	@Produces("application/xml")
	public SingleItemLocation addItemInLocation(@PathParam("username") String userid, 
			@QueryParam("item") String item, @QueryParam("location") String location,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add item-location from user " + userid + 
				", session "+ sessionid);
		log.info("Add"+ item + "in location" + location);
		return OntologyManager.getInstance().addItemInLocation(userid, item, location);
	}
	
	@POST
	@Path("/addItemInLocationObject")	
	@Consumes("application/xml")
	//@Produces("application/xml")
	/*
	@return null if the couple item-location is already entered in ontology 
			file or in the db, a SingleItemLocation otherwise
	*/
	public void addItemInLocationObject(@PathParam("username") String userid, 
			@CookieParam("sessionid") String sessionid, SingleItemLocation itemLocation)
	{
		log.info("Request to add item-location from user " + userid + 
				", session "+ sessionid);
		log.info("Add"+ itemLocation.item + "in location" + itemLocation.location);
		
		OntologyManager.getInstance().addItemInLocation(userid, itemLocation.item,itemLocation.location);
	}

	/*
	 * Visualizza tutti gli item-location inserite da {username}
	 */
	@GET
	@Path("/viewItemLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleItemLocation> viewItemLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view item-location from user " + userid + ", session "+ sessionid);
			System.out.println("-OntologyListener metodo viewItemLocation");
			
			return OntologyManager.getInstance().retrieveAllItemLocation(userid);
	}
	
	/*
	 * L'utente {username} vota una coppia item-location
	 */
	@GET
	@Path("/voteItem")	
	@Produces("application/xml")
	public SingleItemLocation voteItem(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("item") String item, @QueryParam("location") String location)
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteItem(userid,item,location);
	}
	
	/*
	 * L'utente {username} vota item con una lista di location per quell'item
	 */
	@POST
	@Path("/voteItemLocationList")	
	@Consumes("application/xml")
	public void voteItemLocationList(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,ItemLocationList itemLocationList)
	{		
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			
			//creo la lista di location, dato che mi arriva una stringa con le location separate da una virgola
			List<String> locationList= new LinkedList<String>();
			String[] words = itemLocationList.locations.split(",");
			locationList.addAll(Arrays.asList(words));
			
			OntologyManager.getInstance().voteItemLocationList(userid,itemLocationList.item,locationList);
			
	}

	@POST
	@Path("/voteItemObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleItemLocation voteItemObject(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,SingleItemLocation itemLocation)
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteItem(userid,itemLocation.item,itemLocation.location);
	}
	
	// modificati, da List<Location> A List<String> quindi da errore x' non è xml
	@GET
	@Path("/viewLocationForItem")	
	@Produces("application/xml")
	public List<String> viewLocationForItem(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("item") String item)
	{
			log.info("Request to view location for item: " + item);
			List<String> list = OntologyManager.getInstance().viewLocationForItem(userid,item);
			System.out.println(list);
			return list;
	}
	// modificati, da List<Location> A List<String> quindi da errore x' non è xml
	@GET
	@Path("/viewLocationForItemVoted")	
	@Produces("application/xml")
	public List<String> viewLocationForItemVoted(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("item") String item)
	{
			log.info("Request to view location for item: " + item);
			List<String> list = OntologyManager.getInstance().viewLocationForItemVoted(userid,item);
			System.out.println(list);
			return list; 
	}
	
	@GET
	@Path("/deleteVoteForItemLocation")	
	//cancella il voto di un utente
	public boolean deleteVoteForItemLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("item") String item,@QueryParam("location") String location)
	{
			log.info("Request to delete vote for item location: " + item);
			return OntologyManager.getInstance().deleteVoteForItemLocation(userid,item,location);
	}
	
	@POST
	@Path("/deleteVoteForItemLocationObject")	
	@Consumes("application/xml")
	//cancella il voto di un utente
	public boolean deleteVoteForItemLocationObject(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,SingleItemLocation itemLocation)
	{
			log.info("Request to delete vote for item location: " + itemLocation.item);
			return OntologyManager.getInstance().deleteVoteForItemLocation(userid,itemLocation.item,itemLocation.location);
	}
	
	/*
	 * Visualizza tutti gli item-location votati da {username}
	 */
	@GET
	@Path("/viewItemLocationVoted")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleItemLocation> viewItemLocationVoted(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view item-location voted from user " + userid + ", session "+ sessionid);
			System.out.println(" Sono in OntologyListener /viewItemLocationVoted");
			return OntologyManager.getInstance().retrieveAllItemLocationVoted(userid);
	}
	
	/*
	 * Prova inserimento di un item-location nell'ontologia
	 */
	@GET
	@Path("/updateOntology")	
	//@Consumes("application/xml")
	public void updateOntology(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("item") String item,@QueryParam("location") String location) 
	{       
			
			OntologyReasoner.getInstance().updateOntology(item,location);
	}
	
	
// ACTION
	@GET
	@Path("/addActionInLocation")	
	@Produces("application/xml")
	public SingleActionLocation addActionInLocation(@PathParam("username") String userid, 
			@QueryParam("action") String action, @QueryParam("location") String location,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ action + "in location" + location);
		
		return OntologyManager.getInstance().addActionInLocation(userid, action, location);
	}
	
	@POST
	@Path("/addActionInLocationObject")	
	@Consumes("application/xml")
	
	/*
	@return null if the couple action-location is already entered in ontology 
			file or in the db, a SingleActionLocation otherwise
	*/
	public void addActionInLocationObject(@PathParam("username") String userid, 
			@CookieParam("sessionid") String sessionid, SingleActionLocation actionLocation) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ actionLocation.action + "in location" + actionLocation.location);
		
		OntologyManager.getInstance().addActionInLocation(userid, actionLocation.action,actionLocation.location);
	}
	
	/*
	 * Visualizza tutti le action-location inserite da {username}
	 */
	@GET
	@Path("/viewActionLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleActionLocation> viewActionLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view action-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().retrieveAllActionLocation(userid);
	}
	
	/*
	 * L'utente {username} vota una coppia action-location
	 */
	@GET
	@Path("/voteAction")	
	@Produces("application/xml")
	public SingleActionLocation voteAction(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("action") String action, @QueryParam("location") String location)
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteAction(userid,action,location);
	}
	
	/*
	 * L'utente {username} vota action con una lista di location per quell'action
	 */
	@POST
	@Path("/voteActionLocationList")	
	@Consumes("application/xml")
	public void voteActionLocationList(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,ActionLocationList actionLocationList)
	//public void voteActionLocationList(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid)
	
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			/*
			 * String action="passeggiare";
			 
			List<String> location= new LinkedList<String>();
			location.add("marciapiede");
			location.add("parco");
			*/
			//creo la lista di location, dato che mi arriva una stringa con le location separate da una virgola
			List<String> locationList= new LinkedList<String>();
			String[] words = actionLocationList.locations.split(",");
			locationList.addAll(Arrays.asList(words));
			
			OntologyManager.getInstance().voteActionLocationList(userid,actionLocationList.action,locationList);
			//OntologyManager.getInstance().voteActionLocationList(userid,action,location);
	}
	
	@POST
	@Path("/voteActionObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleActionLocation voteItemObject(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid, SingleActionLocation actionLocation ) 
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteAction(userid,actionLocation.action,actionLocation.location);
	}

	// modificati, da List<Location> A List<String> quindi da errore x' non è xml
	@GET
	@Path("/viewLocationForAction")	
	@Produces("application/xml")
	public List<String> viewLocationForAction(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("action") String action)
	{
			log.info("Request to view location for action: " + action);
			List<String> list = OntologyManager.getInstance().viewLocationForAction(userid,action);
			System.out.println(list);
			return list;
	}
	
	// modificati, da List<Location> A List<String> quindi da errore x' non è xml
	@GET
	@Path("/viewLocationForActionVoted")	
	@Produces("application/xml")
	public List<String> viewLocationForActionVoted(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("action") String action)
	{
			log.info("Request to view location for action: " + action);
			List<String> list = OntologyManager.getInstance().viewLocationForActionVoted(userid,action);
			//return OntologyManager.getInstance().viewLocationForActionVoted(userid,action);
			System.out.println(list);
			return list;
	}
	
	/*
	 * Visualizza tutti le action-location votate da {username}
	 */
	@GET
	@Path("/viewActionLocationVoted")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleActionLocation> viewActionLocationVoted(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view action-location voted from user " + userid + ", session "+ sessionid);
			System.out.println(" Sono in OntologyListener /viewActionLocationVoted");
			return OntologyManager.getInstance().retrieveAllActionLocationVoted(userid);
	}
	
	@GET
	@Path("/deleteVoteForActionLocation")	
	//@Produces("application/xml")
	public boolean deleteVoteForActionLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("action") String action,@QueryParam("location") String location)
	{
			log.info("Request to delete vote for item location: " + action);
			return OntologyManager.getInstance().deleteVoteForActionLocation(userid,action,location);
	}
	
	
	@POST
	@Path("/deleteVoteForActionLocationObject")	
	@Consumes("application/xml")
	//cancella il voto di un utente
	public boolean deleteVoteForActionLocationObject(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,SingleActionLocation actionLocation)
	{
			log.info("Request to delete vote for item location: " + actionLocation.action);
			return OntologyManager.getInstance().deleteVoteForActionLocation(userid,actionLocation.action,actionLocation.location);
	}
	
//LOCATION	
	
	@GET
	@Path("/addLocation")	
	@Produces("application/xml")
	public void addLocation(@PathParam("username") String userid, 
			@QueryParam("title") String title, @QueryParam("location") String location,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ location + "for title" + title);
		
		OntologyManager.getInstance().addLocation(userid, title.toLowerCase(), location.toLowerCase());
	}
	
	@GET
	@Path("/findLocation")	
	public String findLocation(@PathParam("username") String userid, 
			@QueryParam("title") String title,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to find a location for title " + title );
		String location = OntologyManager.getInstance().findLocation(userid, title.toLowerCase());
		System.out.println(location);
		return location;
	}

	
	@GET
	@Path("/addLocationInLocation")	
	@Produces("application/xml")
	public SingleLocationLocation addLocationInLocation(@PathParam("username") String userid, 
			@QueryParam("location1") String location1, @QueryParam("location2") String location2,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ location1 + "in location" + location2);
		
		return OntologyManager.getInstance().addLocationInLocation(userid, location1, location2);
	}

	
	@POST
	@Path("/addLocationInLocationObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleLocationLocation addLocationInLocationObject(@PathParam("username") String userid, 
			SingleLocationLocation locationLocation,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ locationLocation.location1 + "in location" + locationLocation.location2);
		
		return OntologyManager.getInstance().addLocationInLocation(userid, locationLocation.location1, locationLocation.location2);
	}

	/*
	 * Visualizza tutti le location-location inserite da {username}
	 */
	@GET
	@Path("/viewLocationLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleLocationLocation> viewLocationLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view action-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().retrieveAllLocationLocation(userid);
	}
	
	/*
	 * L'utente {username} vota una coppia location-location
	 */
	@GET
	@Path("/voteLocation")	
	@Produces("application/xml")
	public SingleLocationLocation voteLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("location1") String location1, @QueryParam("location2") String location2)
	{
			log.info("Request to vote location-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteLocation(userid,location1,location2);
	}

	
	@POST
	@Path("/voteLocationObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleLocationLocation voteLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid, SingleLocationLocation locationLocation ) 
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteLocation(userid,locationLocation.location1,locationLocation.location2);
	}

	
	@GET
	@Path("/viewLocationForLocation")	
	@Produces("application/xml")
	public List<Location> viewLocationForLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("location") String location)
	{
			log.info("Request to view location for location: " + location);
			return OntologyManager.getInstance().viewLocationForLocation(userid,location);
	}
	
	
}