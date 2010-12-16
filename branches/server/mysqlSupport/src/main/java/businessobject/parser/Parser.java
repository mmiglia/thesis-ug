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
		if (!command.contains(o)) command.add(o);
	}
	
	public void setLanguage(Language l){
		this.roles = l.roles;
	}
	
	public boolean startParse(String userid, String text) {
		 //step 1: split words/arguments + case markers (DONE)
		 //step 2: pick possible Verbs
		int index;
		text = text.toLowerCase();
		LinkedList<String> words, regex; 
		LinkedList<VerbArgs> step2result = new LinkedList<VerbArgs>();
		log.debug("There are "+command.size()+" commands");
		//System.out.println("There are "+command.size()+" commands");
		
		for (Verb o : command){ // for all commands registered to this parser
			log.debug("Analyse command:"+o.getName()+" "+o.toString());
			//System.out.println("Analyse command:"+o.getName()+" "+o.toString());
			double maxVerbScore =0.0;
			for (String s : o.getVerbs()){ // for every synonyms of the command
				//get the list of substring of the verb, from 'add task' we have ['add task','add tas','add ta'..etc]
				regex = getRegex(s);
				
				double score = regex.size();
				//System.out.println("Start score:"+score);
				for (String r : regex){ // for every possible regex
					log.debug("if "+text + " contains " + r);
					//System.out.println("if "+text + " contains " + r);
					if (text.contains(r)){
						score /= regex.size();
						//System.out.println("Score after if:"+score);
						LinkedList<String> args = new LinkedList<String>(Arrays.asList(text.split(r)));
						//System.out.println("Start args list");
						//for(String arg : args){
							//System.out.println(arg);
						//}
						//System.out.println("End args list");
						Iterator<String> argsIterator = args.iterator();
						//remove empty string
						while (argsIterator.hasNext()) if(argsIterator.next().isEmpty()) argsIterator.remove();
						step2result.add(new VerbArgs(o, args, score));
						//System.out.println("Added "+o.getName()+" with args:");
						//for(String arg : args){
							//System.out.println(arg);
						//}
						maxVerbScore = (score>maxVerbScore)? score : maxVerbScore;
						//System.out.println("Maxscore:"+maxVerbScore);
						break; //break loop, get the next synonym
					}
					score-=1;
				}
			}
			//System.out.println("--Final filtering on step2result so that each verb only has one candidate with the best score--");
			// final filtering on step2result so that each verb only has one candidate
			// with the best score
			Iterator<VerbArgs> va = step2result.iterator();
			while (va.hasNext()) {
				
				VerbArgs current = va.next();
				//System.out.println("Analize:"+current.verb.getName());
				if (current.verb.equals(o) && current.score<maxVerbScore){
					//System.out.println( current.verb.getName()+": "+current.verb.getName()+" equals["+o.getName()+"] removed ("+current.score+"<"+maxVerbScore+")");
					va.remove(); 
				}
			}
			//System.out.println("--Filtering DONE--");
		}
		
		//Now, in step2result, for each Verb (command) we got the verb that better match in the input sentence
		
		 //step 3: pick possible clitics (DONE)
		 //step 4: group into arguments
		LinkedList<PossibleParses> step4result = new LinkedList<PossibleParses>();
		 //Get the delimiters
		//System.out.println("Step 4 - Start");

		 for (VerbArgs va : step2result){ // for every verb-argument pairs 
			 int findcount = 0;
			 LinkedList<Arguments> findarg = new LinkedList<Arguments>();
			 //System.out.println("Verb:"+va.verb.getName());
			 
			 for (Arguments arg : va.verb.getArguments()){ 
				 // for every arguments needed by this verb
				 
				 //System.out.println("Argument: noun="+arg.noun.toString()+" role="+arg.role);
				 
				 ListIterator<String> vaIterator = va.args.listIterator();
				 
				 while (vaIterator.hasNext()){ // for every possible argument string in this pair
					 String vargs = vaIterator.next();
					 //System.out.println("vargs="+vargs);
					 
					 
					 possibleArgument:
					 for (SemanticRoles sr : roles){ // find the delimiter
						 //System.out.println("If "+arg.role+"=="+sr.role);
						 if (arg.role == sr.role){
							 //System.out.println("if!hasDelimiter("+vargs+","+sr.delimiter+")->continue");
							 if (!hasDelimiter(vargs,sr.delimiter)) continue;
							 else {
								 //System.out.println("Else:extractArgument("+vargs+","+ sr.delimiter+")");
								 ExtractArgumentReturn extractResult = extractArgument(vargs, sr.delimiter);
								 //Remove current element (the one actually in vargs)
								 vaIterator.remove();
								 //System.out.println(vargs+" removed");
								 if (!extractResult.remainder.isEmpty()) {
									 
									 vaIterator.add(extractResult.remainder);
									 //System.out.println("Added:"+extractResult.remainder);
									 //System.out.println("extractResult.realargument="+extractResult.realargument);
								 }
								 Arguments toSave = new Arguments(arg.role, arg.noun);
								 //System.out.println("Arguments toSave: arg.role="+arg.role.name()+" arg.noun="+arg.noun.toString());
								 toSave.setContent(extractResult.realargument);
								 //System.out.println("Arguments toSave: content="+extractResult.realargument);
								 findarg.add(toSave); // link to reference
								 findcount++;
								 
								 //Is this Like a go-to?
								 break possibleArgument; // next arguments
							 }
						 }
					 }
				 }
			 }
			 //System.out.println("--INIZIO Process degli elementi che non ho ancora catalogato, li classifico come oggetti con ruolo OBJECT (quindi elementi generici, che non mi servono a capire)--");
			 if (va.args.size()>0){
				 for (String s:va.args){
					 //System.out.println("Stringa in va.args:"+s);
					 
					 Arguments toSave = new Arguments(SemanticRoles.RoleType.OBJECT, new ArbitraryObject());
					 
					 toSave.setContent(s);
					 findarg.add(toSave);
					 //System.out.println("Added to findarg: object with RoleType:OBJECT and content="+s);
					 findcount++;
				 }
			 }
			 //System.out.println("--FINE Process degli elementi che non ho ancora catalogato--");
			 double score = va.score;
			 //System.out.println("va.score:"+va.score);
			 
			 //System.out.println("va.verb.getArguments():");
			 //for(Arguments arg: va.verb.getArguments()){
				 //System.out.println("arg.role"+arg.role+" arg.noun"+arg.noun);				 
			 //}
			 //System.out.println("filled if findcount >="+va.verb.getArguments().size());
			 boolean filled = findcount >= va.verb.getArguments().size(); // this verb is complete with arguments
			 //System.out.println("filled:"+filled);
			 //If filled we give one more point to score
			 score += (filled)? 1:0;
			 step4result.add(new PossibleParses(va.verb, findarg, filled, score));
		 }
		 //System.out.println("In step4result ci sono "+ step4result.size()+" elementi");
		 double maxscore = 1.5;// this is the threshold
		 PossibleParses bestresult = null;
		 //System.out.println("We get the PossibleParses that is complete with the max score, if there is not a complete one, we return null");
		 for (PossibleParses p : step4result){
			 if (!p.complete) continue;
			 //System.out.println(p.verb+" complete:"+p.complete+" score:"+p.score);
			 if (p.score > maxscore) {bestresult = p; maxscore=p.score;}
		 }
		 if (bestresult == null){
			 log.info("Parser could not understand user input");
			 //System.out.println("Parser could not understand user input");
			 return false;
		 } 
		 else {
			 log.info("Parser successfully parsed user input");
			 //System.out.println("Parser successfully parsed user input");
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
		boolean[] mask = new boolean[s.length()];
		for (boolean m:mask) m=false; // initialize mask value
		for (SemanticRoles sr : roles){
			String delimiter= " "+sr.delimiter+" "; // pad with space
			for (int i=0; i<=arg.length()-delimiter.length(); i++){
				if (!arg.regionMatches(true, i, delimiter, 0, delimiter.length())) continue;
				else {
					for (int j=i; j<delimiter.length()+i-2;j++) mask[j]=true;
				}
			}
		}
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
	 * @return
	 */
	public ExtractArgumentReturn extractArgument(final String s, final String delimiter){
		ExtractArgumentReturn result = new ExtractArgumentReturn();
		if (!s.contains(delimiter)) return null;
		String temps=s;
		// remove the spaces in front and back
		if (temps.charAt(0)==' ') temps=temps.substring(1);
		if (temps.charAt(temps.length()-1)==' ') temps=temps.substring(0, temps.length()-1);
		int argumentlocation = temps.indexOf(delimiter)+delimiter.length();
		boolean[] mask = bitmaskString(temps.substring(argumentlocation, temps.length()));
		int i;
		for (i=0; i<mask.length; i++){
			if (!mask[i]) result.realargument+=temps.charAt(argumentlocation+i);
			else break;
		}
		while (i<mask.length) result.remainder += temps.charAt(argumentlocation+i++);
		if (temps.indexOf(delimiter)!=0){
			result.remainder = temps.substring(0, temps.indexOf(delimiter)-1)+" "+result.remainder;
		}
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
