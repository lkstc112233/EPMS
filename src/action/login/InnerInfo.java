package action.login;

import java.sql.SQLException;
import java.util.*;

import action.Action;
import action.Manager;
import obj.staticObject.InnerPerson;
import obj.staticSource.InnerOffice;
import obj.staticSource.School;

public class InnerInfo extends Action{
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
	
	public InnerInfo(){
		super();
		System.out.println(">> InnerInfoAction:constructor >");
		if(Manager.getUser()==null) return;
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
		if(this.inner.getPassword()!=null && this.inner.getPassword().length()>0){
			System.out.println(">> InnerInfoAction:execute > 开始修改密码");
			try {
				Manager.getUser().updatePassword(this.inner.getPassword());
			} catch (SQLException e) {
				return this.jumpToMethodWithTips("display","修改密码失败，请重试！",e);
			}
			System.out.println(">> InnerInfoAction:execute > 成功修改密码");
		}
		try {
			inner.setPassword(Manager.getUser().getPassword());
			Manager.getUser().update(inner);
			inner.setPassword(null);
		} catch (IllegalArgumentException | SQLException e) {
			return this.jumpToMethodWithTips("display","修改个人信息失败，请重试！",e);
		}
		return this.returnWithTips(SUCCESS,"修改个人信息成功！");
	}
	
	public String display(){
		return NONE;
	}
	
	
}
