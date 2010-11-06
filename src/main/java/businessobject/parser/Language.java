package businessobject.parser;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/*
 * This class defines the language that is registered to de Parser
 * Currently there is only English language implemented
 * with configuration file en.lang
 * See en.lang to expand to another language by
 * giving a lists of Semantic Roles and delimiter for each semantic
 * roles in that language
 */
public class Language {
	public List<SemanticRoles> roles;
	public String name;
	private static final Properties constants = new Properties();
	
	/*
	 * Construct Language object with default constructor.
	 * This means, language has not been assigned any
	 * semantic roles yet
	 */
	public Language(){
		roles = new LinkedList<SemanticRoles>();
	}
	
	/*
	 * Construct language object
	 * with a given lists of semantic roles
	 * in that language
	 */
	public Language(String name, List<SemanticRoles> roles){
		this.name = name;
		this.roles = roles;
	}
	/**
	 * Construct Language from a language file.
	 * The file consists of name, list of semantic roles, and delimiter for each
	 * of those semantic roles.
	 * see en.lang in resource folder for example
	 * @param filename
	 */
	public Language(String filename){
		this();
		try {
			constants.load(this.getClass().getClassLoader().getResourceAsStream(filename));
			this.name = constants.getProperty("name");
			String[] r = constants.getProperty("roles").split(",");
			String[] d = constants.getProperty("delimiters").split(",");
			SemanticRoles temp;
			for (int i=0;i<r.length;i++){
				temp = new SemanticRoles(SemanticRoles.RoleType.valueOf(r[i]), d[i]);
				this.roles.add(temp);
			}
		} catch (IOException e) {
			System.out.println("Cannot find the language file");
			e.printStackTrace();
		}
	}
}
