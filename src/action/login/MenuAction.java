package action.login;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import action.Manager;
import obj.annualTable.Time;

public class MenuAction extends AnnualAction{
	private static final long serialVersionUID = 5246911694929172909L;
	
	private List<Time> times=new ArrayList<Time>();
	
	public List<Time> getTimes(){return times;}
	public void setTimes(List<Time> times){this.times=times;}
	

	public MenuAction(){
		super();
	}
	
	private void init(){
		if(year<1900)
			this.year=Calendar.getInstance().get(Calendar.YEAR);
	}
	
	@Override
	public String execute(){
		this.init();
		System.out.println(">> MenuAction:execute > year="+year);
		Map<String, Object> session=ActionContext.getContext().getSession();
		try {
			this.times=Time.listTime(year,false);
		} catch (NoSuchFieldException | SecurityException | SQLException e) {
			e.printStackTrace();
			this.times=new ArrayList<Time>();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开了一些小差，尚未搜索到["+year+"年]的时间表！");//设置提示信息
		}
		System.out.println(">> MenuAction:execute <SUCCESS");
		return Manager.getUser().getOffice();
	}
	
}
