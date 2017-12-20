package action.login;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.Pair;
import obj.annualTable.Time;
import obj.staticObject.InnerPerson;
import obj.staticSource.ACCESS;
import token.Role;

public class MenuAction extends ActionSupport{
	private static final long serialVersionUID = 5246911694929172909L;
	
	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private List<Pair<Time,ACCESS>> times;

	public List<Pair<Time,ACCESS>> getTimes(){return times;}
	public void setTimes(List<Pair<Time,ACCESS>> a) {this.times=a;}
	

	static public final String SessionListKey="MenuAction_List";
	
	@SuppressWarnings("unchecked")
	public MenuAction(){
		super();
		this.setTimes(Manager.loadSession(List.class,SessionListKey));
	}
	
	
	@Override
	public String execute(){
		System.out.println(">> MenuAction:execute > year="+this.getAnnual().getYear());
		InnerPerson user=Manager.getUser();
		if(user==null)
			return LOGIN;
		Role role=Role.getRole(user);
		try {
			//当setupIfEmpty为false时会实际调用join联合查询
			this.times=Time.listTime(role,this.getAnnual().getYear(),/*setupIfEmpty*/false);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			this.times=new ArrayList<Pair<Time,ACCESS>>();
			return Manager.tips("服务器开了一些小差，尚未搜索到["+this.getAnnual().getYear()+"年]的时间表！",
					e,ERROR);
		}
		Manager.clearSession();
		Manager.setUser(user);
		Manager.saveSession(SessionListKey,this.times);
		String res=Role.getRole(Manager.getUser()).toString();
		System.out.println(">> MenuAction:execute <"+res);
		return res;
	}
	
}
