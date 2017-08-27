package action.jwc;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import action.login.AnnualAction;
import obj.annualTable.Time;

public class jwcAction extends AnnualAction{
	private static final long serialVersionUID = -2768220819301945257L;
	
	private List<Time> projects=new ArrayList<Time>();

	public List<Time> getProjects() {return projects;}
	public void setProjects(List<Time> projects) {this.projects = projects;}
	
	
	public jwcAction() throws SQLException, NoSuchFieldException, SecurityException{
		super();
	}

	@Override
	public String execute(){
		if(!executive)
			return display();
		System.out.println(">> jwcAction:execute > year="+year);
		Map<String, Object> session=ActionContext.getContext().getSession();
		System.out.println(">> jwcAction:execute <SUCCESS");
		return SUCCESS;
	}
	
	public String display(){
		System.out.println(">> jwcAction:display > year="+this.getYear());
		try {
			projects=Time.listTime(this.getYear(),/*setupIfEmpty*/false);
		} catch (NoSuchFieldException | SecurityException | SQLException e) {
			e.printStackTrace();
			System.out.println(">> jwcAction:display > list Exception("+e.getMessage()+")");
			return ERROR;
		}
		System.out.print(">> jwcAction:display > list=[\n");
		for(Time t:projects)
			System.out.print(t.year+","+t.getProject()+","+t.getTime1()+"->"+t.getTime2()+";\n");
		System.out.print(">> jwcAction:display > list=]\n");
		System.out.println(">> jwcAction:display <NONE");
		return NONE;
	}

	
	
	
}
