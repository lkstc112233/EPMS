package action.jwc;

import java.sql.*;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 导入免费师范生数据
 */
public class qrsxjdxx extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	public qrsxjdxx() throws SQLException, NoSuchFieldException, SecurityException{
		super();
	}
	
	
	private String jumpURL="login.action";
		public String getJumpURL(){return jumpURL;}

	@Override
	public String execute(){
		this.jumpURL="jwc_function_TableOperation_display.action?tableName=PracticeBase&year="+this.getAnnual().getYear();
		return "jump";
	}
	
	
	
}
