package action.login;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.staticObject.InnerPerson;
import obj.staticSource.InnerOffice;
import obj.staticSource.School;

public class InnerInfoAction extends ActionSupport{
	private static final long serialVersionUID = -2488160597018042665L;

	private InnerPerson inner;
	private String newPassword=null;
	private List<InnerOffice> list_office;
	private List<School> list_school;

	public InnerPerson getInner(){return inner;}
	public void setInner(InnerPerson inner){this.inner=inner;}
	public String getNewPassword(){return newPassword;}
	public void setNewPassword(String newPass){this.newPassword=newPass;}
	public List<InnerOffice> getList_office() {return list_office;}
	public void setList_office(List<InnerOffice> list_office) {this.list_office = list_office;}
	public List<School> getList_school() {return list_school;}
	public void setList_school(List<School> list_school) {this.list_school = list_school;}
	
	private boolean executive=false;
	public boolean isExecutive() {return executive;}
	public void setExecutive(boolean executive) {this.executive = executive;}
	
	
	public InnerInfoAction() throws SQLException, IllegalArgumentException, IllegalAccessException{
		super();
		System.out.println(">> InnerInfoAction:constructor >");
		this.inner=Manager.getUser();
		if(inner==null)
			inner=new InnerPerson();
		list_office=InnerOffice.list(InnerOffice.class);
		System.out.print(">> InnerInfoAction:constructor > list_office:[");
		for(InnerOffice s:list_office) System.out.print(s+",");
		System.out.println("]");
		list_school=School.list(School.class);
		System.out.print(">> InnerInfoAction:constructor > list_school:[");
		for(School s:list_school) System.out.print(s+",");
		System.out.println("]");
	}
	
	@Override
	public String execute(){
		System.out.println(">> InnerInfoAction:execute > executive="+executive);
		if(!this.executive)
			return display();
		try {
			if(inner==null || inner.checkKeyNull()){
				System.out.println(">> InnerInfoAction:execute > inner=null");
				return Manager.tips("服务器开了一些小差！",
						display());
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			System.out.println(">> InnerInfoAction:execute > inner.checkKeyNull Exception("+e.getMessage()+")");
			return Manager.tips("服务器开了一些小差！",
					e,display());
		}
		if(this.newPassword!=null && this.newPassword.length()>0){
			System.out.println(">> InnerInfoAction:execute > 开始修改密码");
			try {
				inner.updatePassword(this.newPassword);
			} catch (SQLException e) {
				return Manager.tips("修改密码失败，请重试！",
						e,display());
			}
			System.out.println(">> InnerInfoAction:execute > 成功修改密码");
		}
		System.out.println(">> InnerInfoAction:execute > 开始上传修改后新数据:"+inner.toString());
		System.out.println(">> InnerInfoAction:execute > :"+inner.toString());
		try {
			inner.update();
			Manager.setUser(inner);
		} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
			return Manager.tips("修改个人信息失败，请重试！",
					e,display());
		}
		return Manager.tips("修改个人信息成功！",
				SUCCESS);
	}
	
	public String display(){
		System.out.println(">> InnerInfoAction:display >");
		System.out.println(">> InnerInfoAction:display <NONE");
		return NONE;
	}
	
	
}
