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
	private List<InnerOffice> list_office;
	private List<School> list_school;

	public InnerPerson getInner(){return inner;}
	public void setInner(InnerPerson inner){this.inner=inner;}
	public List<InnerOffice> getList_office() {return list_office;}
	public void setList_office(List<InnerOffice> list_office) {this.list_office = list_office;}
	public List<School> getList_school() {return list_school;}
	public void setList_school(List<School> list_school) {this.list_school = list_school;}
	
	private boolean executive=false;
	public boolean isExecutive() {return executive;}
	public void setExecutive(boolean executive) {this.executive = executive;}
	
	
	public InnerInfoAction(){
		super();
		System.out.println(">> InnerInfoAction:constructor >");
		this.inner=new InnerPerson();
		Manager.getUser().copyTo(this.inner);
		inner.setPassword(null);
		try {
			list_office=InnerOffice.list(InnerOffice.class);
			list_school=School.list(School.class);
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String execute(){
		System.out.println(">> InnerInfoAction:execute > executive="+executive);
		if(!this.executive)
			return display();
		if(this.inner.getPassword()!=null && this.inner.getPassword().length()>0){
			System.out.println(">> InnerInfoAction:execute > 开始修改密码");
			try {
				Manager.getUser().updatePassword(this.inner.getPassword());
			} catch (SQLException e) {
				return Manager.tips("修改密码失败，请重试！",
						e,display());
			}
			System.out.println(">> InnerInfoAction:execute > 成功修改密码");
		}
		try {
			Manager.getUser().update(inner);
			Manager.setUser(inner);
		} catch (IllegalArgumentException | SQLException e) {
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
