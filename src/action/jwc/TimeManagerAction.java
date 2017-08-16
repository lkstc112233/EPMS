package action.jwc;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import obj.annualTable.Time;

public class TimeManagerAction extends AnnualAction{
	private static final long serialVersionUID = -2768220819301945257L;
	
	private List<Time> projects=new ArrayList<Time>();

	public List<Time> getProjects() {return projects;}
	public void setProjects(List<Time> projects) {this.projects = projects;}
	
	
	public TimeManagerAction() throws SQLException, NoSuchFieldException, SecurityException{
		super();
	}

	@Override
	public String execute(){
		if(!executive)
			return display();
		System.out.println(">> TimeManagerAction:execute > year="+year);
		Map<String, Object> session=ActionContext.getContext().getSession();
		System.out.println(">> TimeManagerAction:execute > 开始上传修改数据到数据库");
		for(int i=0;i<projects.size();i++){
			Time t=projects.get(i);
			System.out.println(">> TimeManagerAction:execute > ["+i+"]"+t.year+","+t.getProject()+","+t.getTime()+";");
			try {
				t.update();
			} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
				e.printStackTrace();
				session.put(token.ActionInterceptor.ErrorTipsName,"第"+i+"条上传失败！");
				System.out.println(">> TimeManagerAction:execute > ["+i+"]失败");
				break;
			}
			System.out.println(">> TimeManagerAction:execute > ["+i+"]成功");
		}
		System.out.println(">> TimeManagerAction:execute <SUCCESS");
		return SUCCESS;
	}
	
	public String display(){
		System.out.println(">> TimeManagerAction:display > year="+this.getYear());
		try {
			projects=Time.list(Time.class,this.getYear());
		} catch (NoSuchFieldException | SecurityException | SQLException e) {
			e.printStackTrace();
			System.out.println(">> TimeManagerAction:display > list Exception("+e.getMessage()+")");
			return ERROR;
		}
		System.out.print(">> TimeManagerAction:display > list=[\n");
		for(Time t:projects)
			System.out.print(t.year+","+t.getProject()+","+t.getTime()+";\n");
		System.out.print(">> TimeManagerAction:display > list=]\n");
		System.out.println(">> TimeManagerAction:display <NONE");
		return NONE;
	}

	
	
	
}
