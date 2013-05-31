package businessobject.parser;
public class SemanticRoles {
	
	public enum RoleType {OBJECT, GOAL, SOURCE, LOCATION, TIME};
	
	public String delimiter;
	public RoleType role;
	public SemanticRoles(RoleType role, String delimiter){
		this.delimiter = delimiter;
		this.role = role;
	}
	
}
