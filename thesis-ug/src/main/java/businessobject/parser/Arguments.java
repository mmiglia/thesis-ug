package businessobject.parser;

import businessobject.parser.nountype.Noun;

/**
 * Basic class to be used as an argument to the parser
 *
 */
public class Arguments {
	public Noun noun;
	public SemanticRoles.RoleType role;
	public String content;
	public Arguments(SemanticRoles.RoleType r, Noun n){
		role = r;
		noun = n;
	}
	public void setContent(String c){
		content = c;
	}
}
