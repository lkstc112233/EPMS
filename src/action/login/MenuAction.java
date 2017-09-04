package action.login;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import action.Manager;
import obj.annualTable.Time;

public class MenuAction extends AnnualAction{
	private static final long serialVersionUID = 5246911694929172909L;
	
	private List<Time> times=new ArrayList<Time>();
	private String actionPrefix;
	
	public List<Time> getTimes(){return times;}
	public void setTimes(List<Time> times){this.times=times;}
	public String getActionPrefix(){return actionPrefix;}
	public void setActionPrefix(String actionPrefix){
		this.actionPrefix=actionPrefix;
		if(this.actionPrefix==null) this.actionPrefix="null";
	}

	public MenuAction(){
		super();
		this.setActionPrefix(Manager.getActionPrefix());
	}
	
	
	@Override
	public String execute(){
		super.setupYear();
		System.out.println(">> MenuAction:execute > year="+this.getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		try {
			this.times=Time.listTime(this.getYear(),false);
		} catch (NoSuchFieldException | SecurityException | SQLException e) {
			e.printStackTrace();
			this.times=new ArrayList<Time>();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开了一些小差，尚未搜索到["+this.getYear()+"年]的时间表！");//设置提示信息
		}
		String res=Manager.getActionPrefix();
		System.out.println(">> MenuAction:execute <"+res);
		return res;
	}
	
}
