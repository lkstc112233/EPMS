package action.jwc;

import java.sql.*;

/**
 * 导入免费师范生数据
 */
public class qrsxjdxx extends action.login.AnnualAction{
	private static final long serialVersionUID = 5998268336475528662L;
	
	public qrsxjdxx() throws SQLException, NoSuchFieldException, SecurityException{
		super();
	}
	
	
	private String jumpURL="login.action";
		public String getJumpURL(){return jumpURL;}

	@Override
	public String execute(){
		if(!executive)
			return display();
		return display();
	}
	
	@Override
	public String display(){
		this.jumpURL="jwc_TableOperation_display.action?tableName=PracticeBase";
		return "jump";
	}
	
	
	
}
