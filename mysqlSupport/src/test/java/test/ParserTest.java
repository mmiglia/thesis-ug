package test;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import businessobject.ParserManager;

import valueobject.QueryReply;

public class ParserTest extends TestCase {
	private String toParse = "ricordami di prendere il pane prima delle 11";
	private String user = "user";
	private String lang = "it.lang";
	
	@BeforeClass
	public void initClass() {
		
	}
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testParser() {
		
		System.out.println("Inizio test parser");
		QueryReply result = new QueryReply();
		
		result.status = new ParserManager().inputQuery(user, toParse, lang);
		
		if (result.status)
			assertFalse(false);
		else
			assertTrue(true);
	}
}
