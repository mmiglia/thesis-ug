package businessobject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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
	private final static String ONTOLOGY_FILE = Configuration.getInstance().constants.getProperty("DATABASE_FOLDER")+"/HintsOntology.owl";
	private final static OntologyReasoner instance = new OntologyReasoner();
	private static OWLOntology ontology;
	private static OWLOntologyManager manager;
	
	private OntologyReasoner() {
		manager = OWLManager.createOWLOntologyManager();
		// load the ontology file
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File(
					ONTOLOGY_FILE));
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static OntologyReasoner getInstance(){
		return instance;
	}
	
	/**
	* Get a list of location that might satisfy the need
	* 
	* @param need String from a parsed user query / task that needs to be done
	* @return list of location that can satisfy the need
	*/
	public static List<String> getSearchQuery(String need) {		
			String base = ontology.getOntologyID().getOntologyIRI().toString();
			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			OWLNamedIndividual item = dataFactory.getOWLNamedIndividual(IRI
					.create(base + "#" + need));
			OWLObjectProperty location = dataFactory.getOWLObjectProperty(IRI
					.create(base + "#canBeFoundIn"));

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
}