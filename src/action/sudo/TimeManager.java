package action.sudo;

import java.sql.*;
import java.util.*;

import action.Action;
import action.Manager;
import action.login.MenuAction;
import obj.Pair;
import obj.annualTable.Time;
import obj.staticSource.ACCESS;
import token.Role;

public class TimeManager extends Action{
	private static final long serialVersionUID = -2768220819301945257L;
	
	private action.Annual annual=new action.Annual();;
	public action.Annual getAnnual(){return this.annual;}
	
	
	private List<Pair<Time,ACCESS>> times;

	public List<Pair<Time,ACCESS>> getTimes() {return times;}
	public void setTimes(List<Pair<Time,ACCESS>> times) {this.times=times;}

	
	static public final String SessionListKey=MenuAction.SessionListKey;
	
	@SuppressWarnings("unchecked")
	public TimeManager() throws SQLException, NoSuchFieldException, SecurityException{
		super();
		this.setTimes(Manager.loadSession(List.class,SessionListKey));
	}

	@Override
	public String execute(){
		System.out.println(">> TimeManagerAction:execute > 开始上传修改数据到数据库Time表,year="+this.getAnnual().getYear());
		for(int i=0;i<times.size();i++){
			Time t=times.get(i).getKey();
			t.setYear(this.getAnnual().getYear());
			try {
				t.update();
			} catch (IllegalArgumentException | SQLException e) {
				return this.returnWithTips(NONE,"第"+i+"条上传Time失败！",e);
			}
		}
		System.out.println(">> TimeManagerAction:execute > 开始上传修改数据到数据库ACCESS表");
		for(int i=0;i<times.size();i++){
			Time t=times.get(i).getKey();
			try {
				ACCESS a=ACCESS.getFromTime(t);
				a.update();
			} catch (IllegalArgumentException | IllegalAccessException | SQLException | NullPointerException e) {
				return this.returnWithTips(NONE,"第"+i+"条上传ACCESS失败！",e);
			}
		}
		return this.jumpToMethodWithTips("display","修改成功!");
	}
	
	public String display(){
		System.out.println(">> TimeManagerAction:display > year="+this.getAnnual().getYear());
		Role role=Role.getRole(Manager.getUser());
		try {
			times=Time.listTime(role,this.getAnnual().getYear(),/*setupIfEmpty*/true);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return this.returnWithTips(NONE,"服务器开小差去了!",e);
		}
		Manager.saveSession(SessionListKey,times);
		return NONE;
	}

	
	
	
}
