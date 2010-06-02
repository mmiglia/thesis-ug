package businessobject.parser;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Language {
	public List<SemanticRoles> roles;
	public String name;
	private static final Properties constants = new Properties();
	public Language(){
		roles = new LinkedList<SemanticRoles>();
	}
	public Language(String name, List<SemanticRoles> roles){
		this.name = name;
		this.roles = roles;
	}
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
