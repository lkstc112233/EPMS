package action.login;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.annualTable.Time;
import token.Role;

public class MenuAction extends ActionSupport{
	private static final long serialVersionUID = 5246911694929172909L;
	
	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private Role role;
	private List<Time> times=new ArrayList<Time>();
	
	public Role getRole(){return this.role;}
	public List<Time> getTimes(){return times;}
	public void setTimes(List<Time> times){this.times=times;}

	public MenuAction(){
		super();
	}
	
	
	@Override
	public String execute(){
		System.out.println(">> MenuAction:execute > year="+this.getAnnual().getYear());
		this.role=Role.getRoleByInnerPerson(Manager.getUser());
		try {
			//当setupIfEmpty为false时会实际调用join联合查询
			this.times=Time.listTime(role,this.getAnnual().getYear(),/*setupIfEmpty*/false);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			this.times=new ArrayList<Time>();
			return Manager.tips("服务器开了一些小差，尚未搜索到["+this.getAnnual().getYear()+"年]的时间表！",
					e,ERROR);
		}
		String res=Role.getRoleByInnerPerson(Manager.getUser()).toString();
		System.out.println(">> MenuAction:execute <"+res);
		return res;
	}
	
}
