package action.login;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.SQLCollection;
import obj.staticObject.InnerPerson;
import obj.staticSource.InnerOffice;
import obj.staticSource.School;

public class InnerInfoAction extends ActionSupport{
	private static final long serialVersionUID = -2488160597018042665L;

	private InnerPerson inner;
	private String newPassword=null;
	private List<String> list_office;
	private List<String> list_school;
	private String select_retire;

	public InnerPerson getInner(){return inner;}
	public void setInner(InnerPerson inner){this.inner=inner;}
	public String getNewPassword(){return newPassword;}
	public void setNewPassword(String newPass){this.newPassword=newPass;}
	public List<String> getList_office() {return list_office;}
	public void setList_office(List<String> list_office) {this.list_office = list_office;}
	public List<String> getList_school() {return list_school;}
	public void setList_school(List<String> list_school) {this.list_school = list_school;}
	public String getSelect_retire() {return select_retire;}
	public void setSelect_retire(String select_retire) {this.select_retire = select_retire;}
	
	private boolean executive=false;
	public boolean isExecutive() {return executive;}
	public void setExecutive(boolean executive) {this.executive = executive;}
	
	
	public InnerInfoAction() throws SQLException, IllegalArgumentException, IllegalAccessException{
		super();
		System.out.println(">> InnerInfoAction:constructor >");
		this.inner=Manager.getUser();
		if(inner==null)
			inner=new InnerPerson();
		list_office=SQLCollection.getOutOne(InnerOffice.list(InnerOffice.class));
		System.out.print(">> InnerInfoAction:constructor > list_office:[");
		for(String s:list_office) System.out.print(s+",");
		System.out.println("]");
		list_school=SQLCollection.getOutOne(School.list(School.class));
		System.out.print(">> InnerInfoAction:constructor > list_school:[");
		for(String s:list_school) System.out.print(s+",");
		System.out.println("]");
		this.select_retire=Boolean.toString(inner.getRetire());
	}
	
	@Override
	public String execute(){
		System.out.println(">> InnerInfoAction:execute > executive="+executive);
		if(!this.executive)
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		try {
			if(inner==null || inner.checkKeyNull()){
				System.out.println(">> InnerInfoAction:execute > inner=null");
				session.put(token.ActionInterceptor.ErrorTipsName,
						"服务器开了一些小差！");
				return display();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			System.out.println(">> InnerInfoAction:execute > inner.checkKeyNull Exception("+e.getMessage()+")");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开了一些小差！");
			return display();
		}
		inner.setRetire(Boolean.parseBoolean(this.select_retire));
		if(this.newPassword!=null && this.newPassword.length()>0){
			System.out.println(">> InnerInfoAction:execute > 开始修改密码");
			try {
				inner.updatePassword(this.newPassword);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println(">> InnerInfoAction:execute > 修改密码失败");
				session.put(token.ActionInterceptor.ErrorTipsName,
						"修改密码失败，请重试！");
				return display();
			}
			System.out.println(">> InnerInfoAction:execute > 成功修改密码");
		}
		System.out.println(">> InnerInfoAction:execute > 开始上传修改后新数据:"+inner.toString());
		System.out.println(">> InnerInfoAction:execute > :"+inner.toString());
		try {
			inner.update();
			Manager.setUser(inner);
		} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
			System.out.println(">> InnerInfoAction:execute > 上传个人信息失败");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"修改个人信息失败，请重试！");
			return display();
		}
		session.put(token.ActionInterceptor.ErrorTipsName,
				"修改个人信息成功！");
		System.out.println(">> InnerInfoAction:display <SUCCESS");
		return SUCCESS;
	}
	
	public String display(){
		System.out.println(">> InnerInfoAction:display >");
		System.out.println(">> InnerInfoAction:display <NONE");
		return NONE;
	}
	
	
}
