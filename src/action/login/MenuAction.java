package action.login;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.FunctionAction;
import action.Manager;
import obj.annualTable.Time;
import obj.staticObject.InnerPerson;
import token.Role;

public class MenuAction extends ActionSupport{
	private static final long serialVersionUID = 5246911694929172909L;
	
	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private List<Time> times=new ArrayList<Time>();
	private List<Integer> progress=new ArrayList<Integer>();
	
	public List<Time> getTimes(){return times;}
	public void setTimes(List<Time> times){this.times=times;}
	public List<Integer> getProgress(){return this.progress;}
	

	public MenuAction(){
		super();
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
			this.times=new ArrayList<Time>();
			return Manager.tips("服务器开了一些小差，尚未搜索到["+this.getAnnual().getYear()+"年]的时间表！",
					e,ERROR);
		}
		Manager.clearSession();
		Manager.setUser(user);
		try {
			obj.staticSource.School school=new obj.staticSource.School(Manager.getUser().getSchool());
			for(Time t:times) {
				int res=FunctionAction.ProgressMin;
				try {
					Class<?> clazz=Class.forName("action.function."+t.getActionClass());
					if(FunctionAction.class.isAssignableFrom(clazz)) {
						FunctionAction fa=(FunctionAction)clazz.newInstance();
						res=fa.checkProgress(school);
						System.out.println(">> "+t.getActionClass()+" >Progress>> "+res);
					}
				}catch(ClassNotFoundException e) {
				}catch(Exception e) {
					e.printStackTrace();
				}
				this.progress.add(Math.max(FunctionAction.ProgressMin,Math.min(res,FunctionAction.ProgressMax)));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		String res=Role.getRole(Manager.getUser()).toString();
		System.out.println(">> MenuAction:execute <"+res);
		return res;
	}
	
}
