package action.sudo;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.annualTable.Time;
import obj.staticSource.ACCESS;
import token.Role;

public class TimeManager extends ActionSupport{
	private static final long serialVersionUID = -2768220819301945257L;
	
	private action.Annual annual=new action.Annual();;
	public action.Annual getAnnual(){return this.annual;}
	
	
	private List<Time> times=new ArrayList<Time>();

	public List<Time> getTimes() {return times;}
	public void setTimes(List<Time> times) {this.times = times;}
	
	
	public TimeManager() throws SQLException, NoSuchFieldException, SecurityException{
		super();
	}

	@Override
	public String execute(){
		System.out.println(">> TimeManagerAction:execute > 开始上传修改数据到数据库Time表,year="+this.getAnnual().getYear());
		for(int i=0;i<times.size();i++){
			Time t=times.get(i);
			t.setYear(this.getAnnual().getYear());
			try {
				t.update();
			} catch (IllegalArgumentException | SQLException e) {
				return Manager.tips("第"+i+"条上传Time失败！",
						e,NONE);
			}
		}
		System.out.println(">> TimeManagerAction:execute > 开始上传修改数据到数据库ACCESS表");
		for(int i=0;i<times.size();i++){
			Time t=times.get(i);
			try {
				ACCESS a=ACCESS.getFromTime(t);
				a.update();
			} catch (IllegalArgumentException | IllegalAccessException | SQLException | NullPointerException e) {
				return Manager.tips("第"+i+"条上传ACCESS失败！",
						e,NONE);
			}
		}
		return SUCCESS;
	}
	
	public String display(){
		System.out.println(">> TimeManagerAction:display > year="+this.getAnnual().getYear());
		Role role=Role.getRole(Manager.getUser());
		try {
			times=Time.listTime(role,this.getAnnual().getYear(),/*setupIfEmpty*/true);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return Manager.tips("出错了！",
					e,ERROR);
		}
		return NONE;
	}

	
	
	
}
