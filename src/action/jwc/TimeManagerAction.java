package action.jwc;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import action.Manager;
import action.login.AnnualAction;
import obj.annualTable.Time;
import obj.staticSource.ACCESS;
import token.Role;

public class TimeManagerAction extends AnnualAction{
	private static final long serialVersionUID = -2768220819301945257L;
	
	private List<Time> times=new ArrayList<Time>();

	public List<Time> getTimes() {return times;}
	public void setTimes(List<Time> times) {this.times = times;}
	
	
	public TimeManagerAction() throws SQLException, NoSuchFieldException, SecurityException{
		super();
	}

	@Override
	public String execute(){
		if(!executive)
			return display();
		System.out.println(">> TimeManagerAction:execute > year="+this.getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		System.out.println(">> TimeManagerAction:execute > 开始上传修改数据到数据库Time表");
		for(int i=0;i<times.size();i++){
			Time t=times.get(i);
			System.out.println(">> TimeManagerAction:execute > ["+i+"]"+t.year+","+t.getProject()+","+t.getTime1()+"->"+t.getTime2()+";");
			try {
				t.update();
			} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
				e.printStackTrace();
				session.put(token.ActionInterceptor.ErrorTipsName,"第"+i+"条上传Time失败！");
				System.out.println(">> TimeManagerAction:execute > ["+i+"]Time失败");
				//fail
				System.out.println(">> TimeManagerAction:execute <NONE");
				return NONE;
			}
			System.out.println(">> TimeManagerAction:execute > ["+i+"]Time成功");
		}
		System.out.println(">> TimeManagerAction:execute > 开始上传修改数据到数据库ACCESS表");
		for(int i=0;i<times.size();i++){
			Time t=times.get(i);
			System.out.println(">> TimeManagerAction:execute > ["+i+"]"+t.getTime1()+"->"+t.getTime2()+";");
			try {
				ACCESS a=ACCESS.getFromTime(t);
				a.update();
			} catch (IllegalArgumentException | IllegalAccessException | SQLException | NullPointerException e) {
				e.printStackTrace();
				session.put(token.ActionInterceptor.ErrorTipsName,"第"+i+"条上传ACCESS失败！");
				System.out.println(">> TimeManagerAction:execute > ["+i+"]ACCESS失败");
				//fail
				System.out.println(">> TimeManagerAction:execute <NONE");
				return NONE;
			}
			System.out.println(">> TimeManagerAction:execute > ["+i+"]ACCESS成功");
		}
		System.out.println(">> TimeManagerAction:execute <SUCCESS");
		return SUCCESS;
	}
	
	public String display(){
		System.out.println(">> TimeManagerAction:display > year="+this.getYear());
		Role role=Role.getRoleByOffice(Manager.getUser());
		try {
			times=Time.listTime(role,this.getYear(),/*setupIfEmpty*/true);
		} catch (NoSuchFieldException | SecurityException | SQLException e) {
			e.printStackTrace();
			System.out.println(">> TimeManagerAction:display > list Exception("+e.getMessage()+")");
			return ERROR;
		}
		System.out.print(">> TimeManagerAction:display > list=[\n");
		for(Time t:times)
			System.out.print(t.year+","+t.getProject()+","+t.getTime1()+"->"+t.getTime2()+";\n");
		System.out.print(">> TimeManagerAction:display > list=]\n");
		System.out.println(">> TimeManagerAction:display <NONE");
		return NONE;
	}

	
	
	
}
