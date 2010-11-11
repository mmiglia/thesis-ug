package dao.management;

public class QueryStatus {

	//Execution error
	public boolean execError=false;
	public String execErrorMessage="";
	public int execErrorID=0;
	public Exception occourtedErrorException=null;
	
	
	//Execution warning
	public boolean execWarning=false;
	public String execWarningMessage="";
	public int execWarningID=0;
	public Exception occourtedWarningException=null;
	
	
	
	//Fields specific for each type of query
	public Object insertOutput=null;
	public Object selectOutput=null;
	public Object updateOutput=null;
	public Object deleteOutput=null;
	public Object customQueryOutput=null;
	
	
	
	public String explainError(){
		
		switch(execErrorID)
		{
		case 0:
			this.execErrorMessage="No errors";
			break;
			
		case 1:
			this.execErrorMessage="Not all query fields were specified";
			break;
		

		case 2:
			this.execErrorMessage="Cannot create statement";
			break;
		
		case 3:
			this.execErrorMessage="Cannot execute query";
			// TODO Specify what to do with this.occourtedException;
			
			break;
			
		case 4:
			this.execErrorMessage="Cannot verify if connection is closed";
			// TODO Specify what to do with this.occourtedException;
			break;
			
		case 5:
			this.execErrorMessage="Cannot change database";
			// TODO Specify what to do with this.occourtedException;
			break;
			
		case 6:
			this.execErrorMessage="Cannot execute the batch file to create the database";
			// TODO Specify what to do with this.occourtedException;
			break;

		case 7:
			this.execErrorMessage="Cannot execute query";
			// TODO Specify what to do with this.occourtedException;
			break;

		case 8:
			this.execErrorMessage="Cannot retreive the ResultSet during the execution of the select query";
			// TODO Specify what to do with this.occourtedException;
			break;

		case 9:
			this.execErrorMessage="The received query was not a select";

			break;
			
		case 10:
			this.execErrorMessage="Cannot retreive the ResultSet during the execution of the custom query";
			// TODO Specify what to do with this.occourtedException;
			break;
			
		}
		
		return this.execErrorMessage;
		
	}
	
	
	public String explainWarning(){
		
		switch(execWarningID)
		{
		case 0:
			this.execWarningMessage="No errors";
			break;
			
		case 1:
			this.execWarningMessage="Cannot close connection";
			break;

		}
		
		return this.execWarningMessage;
		
	}
}
