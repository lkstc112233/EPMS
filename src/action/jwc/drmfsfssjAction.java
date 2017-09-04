package action.jwc;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import action.login.AnnualAction;
import obj.annualTable.Time;

public class drmfsfssjAction extends AnnualAction{
	private static final long serialVersionUID = 5998268336475528662L;
	
	private List<Time> times=new ArrayList<Time>();

	public List<Time> getTimes() {return times;}
	public void setTimes(List<Time> times) {this.times = times;}
	
	
	public drmfsfssjAction() throws SQLException, NoSuchFieldException, SecurityException{
		super();
	}

	@Override
	public String execute(){
		if(!executive)
			return display();
		System.out.println(">> 导入免费师范生数据Action:execute > year="+this.getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		session.put(token.ActionInterceptor.ErrorTipsName,"失败！");
		System.out.println(">> 导入免费师范生数据Action:execute <SUCCESS");
		return SUCCESS;
	}
	
	public String display(){
		System.out.println(">> 导入免费师范生数据Action:display > year="+this.getYear());
		
		
		System.out.println(">> 导入免费师范生数据Action:display <NONE");
		return NONE;
	}

	
	
	
}
