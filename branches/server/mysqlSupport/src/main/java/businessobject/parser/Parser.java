package businessobject.parser;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.parser.command.Verb;
import businessobject.parser.nountype.ArbitraryObject;

/**
 * This class is the main entry point for parser subsystem
 * all logic that happened in the parser are defined here.
 * Most of the implementation are inspired by parser.js in
 * Ubiquity project.
 *
 */
public class Parser {
	private static final Logger log = LoggerFactory.getLogger(Parser.class);
	private List<Verb> command = new LinkedList<Verb>();
	private List<SemanticRoles> roles;
	
	public void createCommand(Verb o){
		if (!command.contains(o)) 
			command.add(o);
	}
	
	public void setLanguage(Language l){
		this.roles = l.roles;
	}
	
	public boolean startParse(String userid, String text, Language l) {
		 //step 1: split words/arguments + case markers (DONE)
		System.out.println("Testo da parsare: "+text);
//		System.out.println("Parser - step 1 DONE");
		 //step 2: pick possible Verbs
		int index;
		text = text.toLowerCase();
		LinkedList<String> words, regex; 
		LinkedList<VerbArgs> step2result = new LinkedList<VerbArgs>();
		log.debug("There are "+command.size()+" commands");
//		System.out.println("Ci sono "+command.size()+" commands");
//		for (int i=0; i<command.size(); i++) {
//			System.out.println("command "+i+": "+command.get(i));
//		}
		//System.out.println("There are "+command.size()+" commands");
		
		for (Verb o : command){ // for all commands registered to this parser
			log.debug("Analyse command:"+o.getName()+" "+o.toString());
//			System.out.println("Analisi comando: "+o.getName()+" - "+o.toString());
			//System.out.println("Analyse command:"+o.getName()+" "+o.toString());
			double maxVerbScore =0.0;
			for (String s : o.getVerbs(l)){ // for every synonyms of the command
//				System.out.println("#############################################Ciclo for - Verb: "+s);
				//get the list of substring of the verb, from 'add task' we have ['add task','add tas','add ta'..etc]
				regex = getRegex(s);
				System.out.println("regex: "+regex);
				
				double score = regex.size();
				//System.out.println("Start score:"+score);
				for (String r : regex){ // for every possible regex
					log.debug("if "+text + " contains " + r);
//					System.out.println("Espressione regolare: \""+r+"\" - SCORE="+score);
//					System.out.println("se \""+text+"\" contiene \""+r+"\"");
					System.out.println("if "+text + " contains " + r);
					if (text.contains(r)){
//						System.out.println("------------------DENTRO ALL'IF---------------");
//						System.out.println("Dimensione iniziale step2result:"+step2result.size());
						score /= regex.size();
						System.out.println("IF - Espressione regolare: \""+r+"\" - SCORE="+score);
//						System.out.println("Score after if:"+score);
//						System.out.println("Split text con "+r+" e crea nuova LinkedList");
						LinkedList<String> args = new LinkedList<String>(Arrays.asList(text.split(r)));
//							System.out.println("Start args list - 1");
//							for(String arg : args){
//								System.out.print(" - \""+arg+"\"");
//							}
//							System.out.println("");
//							System.out.println("End args list - 1");
						Iterator<String> argsIterator = args.iterator();
						//remove empty string
						while (argsIterator.hasNext()) {
							if(argsIterator.next().isEmpty()) {
//								System.out.println("Rimozione elemento da LinkedList args perchè vuoto");
								argsIterator.remove();
							}
						}
//							System.out.println("Start args list - 2");
//							for(String arg : args){
//								System.out.println(arg);
//							}
//							System.out.println("End args list - 2");
						step2result.add(new VerbArgs(o, args, score));
						
//						System.out.print("++++++Added "+o.getName()+" with args:");
//						for(String arg : args){
//							System.out.print(", "+arg);
//						}
//						System.out.println("");
//							for (VerbArgs vea : step2result) {
//								System.out.print("verbo: "+vea.verb+" - args: ");
//								for (String str : vea.args) 
//									System.out.print(str+", ");
//								System.out.println(" - score: "+vea.score);
//							}
						maxVerbScore = (score>maxVerbScore)? score : maxVerbScore;
//						System.out.println("maxVerbScore= "+maxVerbScore);
						//System.out.println("Maxscore:"+maxVerbScore);
//						System.out.println("------------------FINE IF---------------");
						break; //break loop, get the next synonym
					}
//					System.out.println("Dimensione (dopo l'if) step2result="+step2result.size());
					score-=1;
				}
			}
			//System.out.println("--Final filtering on step2result so that each verb only has one candidate with the best score--");
			// final filtering on step2result so that each verb only has one candidate
			// with the best score
			Iterator<VerbArgs> va = step2result.iterator();
			while (va.hasNext()) {
				
				VerbArgs current = va.next();
				System.out.println("Analize:"+current.verb.getName());
				if (current.verb.equals(o) && current.score<maxVerbScore){
//					System.out.println("***********"+ current.verb.getName()+": "+current.verb.getName()+" equals["+o.getName()+"] removed ("+current.score+"<"+maxVerbScore+")");
					va.remove(); 
				}
			}
			//System.out.println("--Filtering DONE--");
		}
		
		System.out.println("<<<<<<<<<<<<Contenuto step2result>>>>>>>>>>>>");
		for (VerbArgs vea : step2result) {
			System.out.print("verbo: "+vea.verb+" - args: ");
			for (String str : vea.args) 
				System.out.print("\""+str+"\", ");
			System.out.println(" - score: "+vea.score);
		}
		System.out.println("<<<<<<<<<<<<Contenuto step2result>>>>>>>>>>>>");
		
		//Now, in step2result, for each Verb (command) we got the verb that better match in the input sentence
		
		 //step 3: pick possible clitics (DONE)
		
		 //step 4: group into arguments
		LinkedList<PossibleParses> step4result = new LinkedList<PossibleParses>();
		 //Get the delimiters
		//System.out.println("Step 4 - Start");

		 for (VerbArgs va : step2result){ // for every verb-argument pairs 
			 int findcount = 0;
			 LinkedList<Arguments> findarg = new LinkedList<Arguments>();
			 System.out.print("Verb:"+va.verb.getName());
			 System.out.print(" - args: ");
			 for (String s : va.args) {
				 System.out.print("'"+s+"', ");
			 }
			 System.out.println("");
			 
			 
			 for (Arguments arg : va.verb.getArguments()){ 
				 // for every arguments needed by this verb
				 // esistono due verbi (comandi): AddTask ed AddEvent
				 // per AddTask gli argomenti sono COSA e QUANDO, per AddEvent sono COSA e QUANDO
				 // COSA -> OBJECT - QUANDO -> TIME
				 
				 System.out.println("Argument for '"+va.verb.getName()+"': noun="+arg.noun.toString()+" role="+arg.role);
				 
				 ListIterator<String> vaIterator = va.args.listIterator();
				 				 
				 while (vaIterator.hasNext()){ // for every possible argument string in this pair
					 // per ognuno degli argomenti estratti allo step2 per un determinato comando
					 String vargs = vaIterator.next();
					 System.out.println("STEP2 vargs="+vargs);					 
					 
					 possibleArgument:
					 for (SemanticRoles sr : roles){ // find the delimiter
						 // per tutti i tipi di delimitatore (OBJECT, GOAL, SOURCE, LOCATION, TIME)
						 System.out.println("If "+arg.role+"=="+sr.role);
						 if (arg.role == sr.role){
							 System.out.print("if !hasDelimiter("+vargs+","+sr.delimiter+")->");
							 if (!hasDelimiter(vargs,sr.delimiter)) {
								 System.out.println("CONTINUE");
								 continue;
							 }
							 else {
								 System.out.println("ELSE");
								 System.out.println("Else: extractArgument("+vargs+","+ sr.delimiter+")");
								 ExtractArgumentReturn extractResult = extractArgument(vargs, sr.delimiter);
								 //Remove current element (the one actually in vargs)
								 vaIterator.remove();
								 //System.out.println(vargs+" removed");
								 if (!extractResult.remainder.isEmpty()) {
									 // add a possible remainder (another
									 vaIterator.add(extractResult.remainder);
									 System.out.println("Added:"+extractResult.remainder);
									 System.out.println("extractResult.realargument="+extractResult.realargument);
								 }
								 Arguments toSave = new Arguments(arg.role, arg.noun);
								 System.out.println("Arguments toSave: arg.role="+arg.role.name()+" arg.noun="+arg.noun.toString());
								 toSave.setContent(extractResult.realargument);
								 System.out.println("Arguments toSave: content="+extractResult.realargument);
								 findarg.add(toSave); // link to reference
								 findcount++;
								 
								 //Is this Like a go-to?
								 break possibleArgument; // next arguments
							 }
						 }
					 }
				 }
			 }
//			 System.out.println("--INIZIO Process degli elementi che non ho ancora catalogato, " +
//			 		"li classifico come oggetti con ruolo OBJECT (quindi elementi generici, " +
//			 		"che non mi servono a capire)--");
			 if (va.args.size()>0){
				 for (String s:va.args){
					 //System.out.println(">>>>>>>>>>>>>>>>Stringa in va.args:"+s);
					 
					 Arguments toSave = new Arguments(SemanticRoles.RoleType.OBJECT, new ArbitraryObject());
					 
					 toSave.setContent(s);
					 findarg.add(toSave);
//					 System.out.println("Added to findarg: object with RoleType:OBJECT and content="+s);
					 findcount++;
				 }
			 }
//			 System.out.println("--FINE Process degli elementi che non ho ancora catalogato--");
			 
			 for (Arguments elemento : findarg) {
				 System.out.println("Elemento: noun:'"+elemento.noun+"' role:'"+elemento.role+"' content:'"+elemento.content+"'");
			 }
			 
			 double score = va.score;
			 System.out.println("va.score:"+va.score);
			 
			 System.out.println("va.verb.getArguments():");
			 for(Arguments arg: va.verb.getArguments()){
				 System.out.println("va.verb.name:'"+va.verb.getName()+"' arg.role:'"+arg.role+"' arg.noun:'"+arg.noun+"'");				 
			 }
			 System.out.println("filled if findcount >="+va.verb.getArguments().size());
			 
			 boolean filled = findcount >= va.verb.getArguments().size(); // this verb is complete with arguments
			 System.out.println("filled:"+filled);
			 //If filled we give one more point to score
			 score += (filled)? 1:0;
			 System.out.print("oggetto PossibleParses con: '"+va.verb.getName()+"',");
			 for (Arguments elemento : findarg) {
				 System.out.print(" <<"+elemento.noun+", "+elemento.role+", "+elemento.content+">>, ");
			 }
			 System.out.println(filled+", "+score);
			 step4result.add(new PossibleParses(va.verb, findarg, filled, score));
		 }
		 // END STEP 4
		 System.out.println("Fine step 4");
		 
		 //System.out.println("In step4result ci sono "+ step4result.size()+" elementi");
		 double maxscore = 1.5;// this is the threshold
		 PossibleParses bestresult = null;
		 //System.out.println("We get the PossibleParses that is complete with the max score, if there is not a complete one, we return null");
		 for (PossibleParses p : step4result){
			 if (!p.complete) 
				 continue;
			 //System.out.println(p.verb+" complete:"+p.complete+" score:"+p.score);
			 if (p.score > maxscore) {
				 bestresult = p; 
				 maxscore=p.score;
			 }
		 }
		 System.out.println("bestresult +++++++++++");
		 if (bestresult == null){
			 log.info("Parser could not understand user input");
			 System.out.println("Parser could not understand user input");
			 return false;
		 } 
		 else {
			 log.info("Parser successfully parsed user input");
			 System.out.println("Parser successfully parsed user input");
			 System.out.print("PARSER: verbo:"+bestresult.verb+" userid:"+userid+" argomenti:");
			 for (Arguments ciccio : bestresult.args) {
				 System.out.println("Noun:"+ciccio.noun.toString()+" Content:"+ciccio.content+" Role:"+ciccio.role.toString());
			 }
			 System.out.println("");
			 System.out.println("Richiamo la funzione execute");
			 return bestresult.verb.execute(userid, bestresult.args);
		 }
		
		 //step 5: anaphora substitution (DONE)
		 //step 6: suggest normalized arguments (DONE)
		 //step 7: suggest verbs for parses without one (DONE)
		 //step 8: noun type detection
		 //step 9: replace arguments with nountype suggestions
		 //step 10: ranking
	}
	
	/*
	 * Get a string matching score between two string.
	 * Comparison is done char by char from the first character
	 * return value is between 0 to 1,
	 * 0 if there is no match since the first character
	 * 1 if there is a full match with the command string
	 * example: 'trans' and 'translate' returns (5/9)
	 * 			'goog' and 'google' returns (4/5)
	 */
	private double getMatchScore(String query, String command){
		Double result=0.0;
		int counter=0;
		char[] c = command.toCharArray();
		for (char q : query.toCharArray()){
			if (counter>command.length()) break;
			if (q==c[counter++])result+=1;
		}
		return result/command.length();
	}
	
	private LinkedList<String> getRegex(String complete){
		LinkedList<String> regex = new LinkedList<String>();
		for (int i=complete.length();i>0;i--) regex.add(complete.substring(0, i));
		return regex;
	}
	
	private class VerbArgs{
		public Verb verb;
		public LinkedList<String> args;
		public double score;
		
		public VerbArgs(){
			args = new LinkedList<String>();
		}
		public VerbArgs(Verb v, LinkedList<String> a, double s){
			verb = v;
			args = a;
			score = s;
		}
	}
	
	private class PossibleParses{
		public Verb verb;
		public LinkedList<Arguments> args;
		public boolean complete;
		public double score;
		
		public PossibleParses(Verb v, LinkedList<Arguments> a, boolean c, double s){
			verb = v;
			args = a;
			complete = c;
			score = s;
		}
	}
	
	/**
	 * Get bitmask for every roles found in the given string
	 * @param s string to be masked
	 * @return boolean array of bitmask
	 */
	public boolean[] bitmaskString(String s){
		String arg = " "+s+" ";//pad with space
		System.out.println("bitmask su stringa: '"+arg+"' "+arg.length());
		boolean[] mask = new boolean[s.length()];
		for (boolean m:mask) 
			m=false; // initialize mask value
		for (SemanticRoles sr : roles){
			// per tutti i tipi di delimitatore (OBJECT, GOAL, SOURCE, LOCATION, TIME)
			String delimiter= " "+sr.delimiter+" "; // pad with space
			for (int i=0; i<=arg.length()-delimiter.length(); i++){
				//controlla se è presente il delimitatore a partire dall'inizio della stringa
				//se non è presente -> continue
				//se è presente mette true
				if (!arg.regionMatches(true, i, delimiter, 0, delimiter.length())) 
					continue;
				else {
					for (int j=i; j<delimiter.length()+i-2;j++)
//					for (int j=i; j<i+delimiter.length(); j++) 
						mask[j]=true;
				}
			}
		}
		System.out.print("da bitmask - array mask:");
		for (boolean a : mask) {
			if (a)
				System.out.print("T");
			else
				System.out.print("F");
		}
		System.out.println(" "+mask.length);
		return mask;
	}
	
	/**
	 * Check if delimiter exist as a FULL word
	 * @param s string to be checked for delimiter
	 * @param delimiter the delimiter
	 * @return true if delimiter is exist as a word inside s
	 */
	public boolean hasDelimiter(String s, String delimiter){
		String arg = " "+s+" ";
		String d = " "+delimiter+" ";
		return arg.contains(delimiter);
	}
	
	/**
	 * Extract argument from a given string
	 * @param s
	 * @param delimiter
	 * @return ExtractArgumentReturn object
	 */
	public ExtractArgumentReturn extractArgument(final String s, final String delimiter){
		System.out.println("--------------------INIZIO extractArgument");
		ExtractArgumentReturn result = new ExtractArgumentReturn();
		if (!s.contains(delimiter)) {
			System.out.println("extractArgument - La stringa "+s+"non contiene il delimitatore "+delimiter);
			System.out.println("--------------------FINE extractArgument");
			return null;
		}
		String temps=s;
		
		// remove the spaces at the beginning and end fo the string
		if (temps.charAt(0)==' ') 
			temps=temps.substring(1);
		if (temps.charAt(temps.length()-1)==' ') 
			temps=temps.substring(0, temps.length()-1);
		
		int argumentlocation = temps.indexOf(delimiter)+delimiter.length();
		// remove the delimiter from string and calculate bitmaskString
		boolean[] mask = bitmaskString(temps.substring(argumentlocation, temps.length()));
		//boolean[] mask = bitmaskString(temps);
		
		System.out.print("BitMask: ");
		for (int i=0; i<mask.length; i++) {
			if (mask[i])
				System.out.print("T");
			else
				System.out.print("F");
		}
		System.out.println(" "+mask.length);
		
		int i;
		// extract from string the real argument (the substring after delimiter)
		// until a new delimiter found
		for (i=0; i<mask.length; i++){
			if (!mask[i]) 
				result.realargument+=temps.charAt(argumentlocation+i);
			else 
				break;
		}
		System.out.println("realargument: '"+result.realargument+"'");
		// the rest of the string became remainder
		while (i<mask.length) 
			result.remainder += temps.charAt(argumentlocation+i++);
		System.out.println("temps: '"+temps+"' "+temps.length());
		System.out.println("remainder: '"+result.remainder+"', temps.indexOf(delimiter)="+temps.indexOf(delimiter)+"("+temps+","+delimiter+")");
		// if indexOf(delimiter)!=0 -> delimiter ins't at the beginning of the string
		// so we have another reminder (the portion of string before the delimiter)
		if (temps.indexOf(delimiter)!=0){
			System.out.println("Stringa dentro if: "+temps.substring(0, temps.indexOf(delimiter)-1)+" "+result.remainder);
			result.remainder = temps.substring(0, temps.indexOf(delimiter)-1)+" "+result.remainder;			
		}
		System.out.println("remainder: '"+result.remainder+"'");
		System.out.println("--------------------FINE extractArgument");
		return result;
	}
	
	private class ExtractArgumentReturn{
		String remainder;
		String realargument;
		public ExtractArgumentReturn(){
			remainder ="";
			realargument="";
		}
	}
	

}
