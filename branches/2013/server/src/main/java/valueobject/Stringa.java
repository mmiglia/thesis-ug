package valueobject;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class Stringa {
public String stringa;
	
	public Stringa()
	{
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param action
	 */
	public Stringa(String stringa) 
	{
		this.stringa = stringa;	
	}

	public String toReturn()
	{
		return this.stringa;	
	}
}
