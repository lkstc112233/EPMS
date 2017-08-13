package submit;

import com.opensymphony.xwork2.ActionSupport;

public class SubmitAction extends ActionSupport{
	private static final long serialVersionUID = -2488160597018042665L;

	private String command;
	public String getCommand(){return command;}
	public void setCommand(String command){this.command=command;}
	
	@Override
	public String execute(){
		System.out.println("COMMAND: "+command);
		return SUCCESS;
	}


}
