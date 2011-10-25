package businessobject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OntologyReasoner is a class that access the OWL file to get inference from a
 * need to get the list of location that might satisfy the need Current
 * implementation is using OWL-API 3 (http://owlapi.sourceforge.net/) and HermiT
 * reasoner (http://hermit-reasoner.com/)
 * 
 */
public class OntologyReasoner {
	private final static Logger log = LoggerFactory
			.getLogger(OntologyReasoner.class);
	
	//private final static String ONTOLOGY_FILE = "http://gronksoft.altervista.org/HintsOntology.owl";
	//private final static String ONTOLOGY_FILE = "http://localhost/HintsOntologyNew.owl";
	private final static String ONTOLOGY_FILE = "/var/www/HintsOntologyNew.owl";
	
	//private final static String ONTOLOGY_FILE = "http://gronksoft.altervista.org/HintsOntology.owl";
	//private final static String ONTOLOGY_FILE = "http://nettuno.dyndns.org/HintsOntology.owl";
	
	File file;
	//private final static String ONTOLOGY_FILE = Configuration.getInstance().constants.getProperty("DATABASE_FOLDER")+"/HintsOntologyIta.owl";
	private final static OntologyReasoner instance = new OntologyReasoner();
	private static OWLOntology ontology;
	private static OWLOntologyManager manager;
	
	private OntologyReasoner() {
		manager = OWLManager.createOWLOntologyManager();

		
		try {

            file = new File(ONTOLOGY_FILE);


			// load the ontology file from IRI created from the ONTOLOGY_FILE string that points to a file on internet
			ontology = manager.loadOntologyFromOntologyDocument(file);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static OntologyReasoner getInstance(){
		return instance;
	}
	
	/**
	* Get a list of location that might satisfy the need.
	* Location are determined using the HermiT reasoner
	* 
	* @param need String from a parsed user query / task that needs to be done
	* @return list of location that can satisfy the need
	*/
	public static List<String> getSearchQuery(String need) {	
		log.info("2File name:"+ONTOLOGY_FILE);
		
			log.info("getSearchQuery with need:"+need);
			String base = ontology.getOntologyID().getOntologyIRI().toString();
			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			log.info(IRI.create(base + "#" + need).toString());
			OWLNamedIndividual item = dataFactory.getOWLNamedIndividual(IRI
					.create(base + "#" + need));
			log.info(IRI.create(base + "#CanBeFoundIn").toString());
			OWLObjectProperty location = dataFactory.getOWLObjectProperty(IRI
					.create(base + "#CanBeFoundIn"));//messa lettera maiuscola iniziale canbefoundin

			// runs classification with the HermiT reasoner
			Reasoner hermit = new Reasoner(manager, ontology);
			hermit.classify();
			hermit.classifyObjectProperties();
			NodeSet<OWLNamedIndividual> inferredlocation = hermit
					.getObjectPropertyValues(item, location);
			Set<OWLNamedIndividual> locationvalues = inferredlocation
					.getFlattened();
			List<String> result = new LinkedList<String>();			
			for (OWLNamedIndividual o : locationvalues) {				
				result.add(o.toStringID().replace('_', ' ').replaceFirst(base + "#", ""));
			}
			return result;
		
	}
	
	public static boolean isItem(String need) {	
		log.info("2File name:"+ONTOLOGY_FILE);
		
			log.info("getSearchQuery with need:"+need);
			String base = ontology.getOntologyID().getOntologyIRI().toString();
			
			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			
			log.info(IRI.create(base + "#" + need).toString());
			OWLNamedIndividual item = dataFactory.getOWLNamedIndividual(IRI
					.create(base + "#" + need));
			
			OWLClass itemClass = dataFactory.getOWLClass(IRI.create(base + "#Item")); 
			
			// runs classification with the HermiT reasoner
			Reasoner hermit = new Reasoner(manager, ontology);
			hermit.classify();
			hermit.classifyObjectProperties();
			
			//NodeSet<OWLClass> inferredlocationItem = hermit.getAncestorClasses(itemClass);
			
			NodeSet<OWLNamedIndividual> inferredlocation = hermit.getInstances(itemClass,true);
			Set<OWLNamedIndividual> itemvalues = inferredlocation
					.getFlattened();
			List<String> result = new LinkedList<String>();		
			String s;
			System.out.println("isItem");
			for (OWLNamedIndividual o : itemvalues) {				
				s = o.toStringID().replace('_', ' ').replaceFirst(base + "#", "");
				System.out.println("isItem for itemvalues= "+s);
				if (s.equals(need))
				{	System.out.println(need + " è un item");
					return true;
				}
			}
			return false;
		
	}
	
	public static boolean isLocation(String need) {	
		log.info("2File name:"+ONTOLOGY_FILE);
		
			log.info("getSearchQuery with need:"+need);
			String base = ontology.getOntologyID().getOntologyIRI().toString();
			
			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			
			log.info(IRI.create(base + "#" + need).toString());
			OWLNamedIndividual location = dataFactory.getOWLNamedIndividual(IRI
					.create(base + "#" + need));
			
			OWLClass itemClass = dataFactory.getOWLClass(IRI.create(base + "#Location")); 
			
			// runs classification with the HermiT reasoner
			Reasoner hermit = new Reasoner(manager, ontology);
			hermit.classify();
			hermit.classifyObjectProperties();
			
			//NodeSet<OWLClass> inferredlocationItem = hermit.getAncestorClasses(itemClass);
			
			NodeSet<OWLNamedIndividual> inferredlocation = hermit.getInstances(itemClass,true);
			Set<OWLNamedIndividual> locationvalues = inferredlocation
					.getFlattened();
			List<String> result = new LinkedList<String>();		
			String s;
			System.out.println("isLocation");
			for (OWLNamedIndividual o : locationvalues) {				
				s = o.toStringID().replace('_', ' ').replaceFirst(base + "#", "");
				System.out.println("isLocation for locationvalues= "+s);
				if (s.equals(need))
				{	System.out.println(need + " è un location");
					return true;
				}
			}
			return false;
		
	}
	/*	17-05-2010
	 * 	Function that perform the update of the ontology with the item-location
	 *  in the db. This item-location couple has been promoted!
	 *  @author anuska
	 */
	//public static boolean updateOntology(SingleItemLocation itemLocation){
	public static void updateOntology(String item,String location, int type){
	       
	       /*
	        * vedi:
	        * http://owlapi.svn.sourceforge.net/viewvc/owlapi/v3/trunk/examples/src/main/java/org/coode/owlapi/examples/ClassesAndInstances.java?view=markup
	        * http://owlapi.svn.sourceforge.net/viewvc/owlapi/v3/trunk/examples/src/main/java/org/coode/owlapi/examples/Example4.java?view=markup
	        * 
	        */
	                try {
	                		item = item.replaceAll(" ", "_");
	                		location = location.replaceAll(" ", "_");
	                        // Get hold of an ontology manager
	                         //OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	                        //type=0 -->action
	                        //type=1 -->item
	                        
	                        
	                        //File file = new File("/var/www/HintsOntologyNew.owl");
	                        //OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
	                         System.out.println("Loaded ontology: " + ontology);
	                         
	                         // We can always obtain the location where an ontology was loaded from
	                         IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
	                         System.out.println(" from: " + documentIRI);
	                         
	                            OWLDataFactory dataFactory = manager.getOWLDataFactory(); 
	                           String base = ontology.getOntologyID().getOntologyIRI().toString();
	                            
	                            
	  
	                   //Add NameIndividual
	                   OWLNamedIndividual oggetto = dataFactory.getOWLNamedIndividual(IRI.create(base + "#" + item)); 
	                   OWLDeclarationAxiom ax1 = dataFactory.getOWLDeclarationAxiom(oggetto); 
	                   manager.addAxiom(ontology, ax1); 
	                   
	                   //Add NameIndividual
	                   OWLNamedIndividual loc = dataFactory.getOWLNamedIndividual(IRI.create(base + "#" + location)); 
	                   OWLDeclarationAxiom ax2 = dataFactory.getOWLDeclarationAxiom(loc); 
	                   manager.addAxiom(ontology, ax2); 


	                   OWLIndividual itemIndividual = dataFactory.getOWLNamedIndividual(IRI.create(base + "#"+ item ));
	                   OWLIndividual locationIndividual = dataFactory.getOWLNamedIndividual(IRI.create(base + "#"+location)); 
	                   
	                   
	                   if (type==1)
	                   {   
	                                   //Add Item
	                                   OWLClass itemClass = dataFactory.getOWLClass(IRI.create(base + "#Item")); 
	                                   OWLClassAssertionAxiom ax = dataFactory.getOWLClassAssertionAxiom(itemClass, itemIndividual); 
	                                   manager.addAxiom(ontology, ax); 
	                   }else
	                   {   
	                                   //Add Action
	                                   OWLClass actionClass = dataFactory.getOWLClass(IRI.create(base + "#Action")); 
	                                   OWLClassAssertionAxiom ax = dataFactory.getOWLClassAssertionAxiom(actionClass, itemIndividual); 
	                                   manager.addAxiom(ontology, ax); 
	                   }   
	                   
	                   
	                   //Add Location
	                   OWLClass locationClass = dataFactory.getOWLClass(IRI.create(base + "#Location")); 
	                   OWLClassAssertionAxiom bx = dataFactory.getOWLClassAssertionAxiom(locationClass, locationIndividual); 
	                   manager.addAxiom(ontology, bx); 
	                         
	                   //Add Property
	                   OWLObjectProperty canBeFoundIn = dataFactory.getOWLObjectProperty(IRI.create(base + "#CanBeFoundIn")); 
	                   OWLObjectPropertyAssertionAxiom assertion = dataFactory.getOWLObjectPropertyAssertionAxiom(canBeFoundIn,itemIndividual, locationIndividual); 
	                   AddAxiom addAxiomChange = new AddAxiom(ontology, assertion);
	                   manager.applyChange(addAxiomChange); 
	                       
	              
	                   manager.saveOntology(ontology, documentIRI); 
	                   System.out.println("Save ontology");
	                         
	                 
	                 }

	            
	        
	                 catch (OWLOntologyStorageException e)
	                 {
	                                  System.out.println("Could not save ontology: " + e.getMessage());
	                                  
	                 } 
	       
	   
	   
	   }
	
}