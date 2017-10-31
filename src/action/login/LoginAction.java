package action.login;

import java.sql.SQLException;
import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.staticObject.InnerPerson;

public class LoginAction extends ActionSupport{
	private static final long serialVersionUID = -2488160597018042665L;

	private InnerPerson inner;
	
	public InnerPerson getInner(){return inner;}
	public void setInner(InnerPerson inner){this.inner=inner;}
	
	public LoginAction() throws SQLException{
		super();
		System.out.println(">> LoginAction:constructor");
		inner=new InnerPerson();
	}
	
	@Override
	public String execute(){
		System.out.println(">> LoginAction:execute");
		Manager.clearSession();
		if(inner.getId()==null&&inner.getPassword()==null){
			InnerPerson tmp=Manager.getUser();
			if(tmp==null){
				System.out.println(">> LoginAction:execute <LOGIN");
				return LOGIN;
			}
			inner=tmp;
			System.out.println(">> LoginAction:execute > 从session中读取inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
		}
		if(inner.checkPassword()){
			System.out.println(">> LoginAction:execute > 保存到session中:inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
			Manager.setUser(inner);
			try {
				inner.load();
			} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
				Manager.tips("读取个人信息失败，请重新登录！",
						e,ERROR);
			}
			System.out.println(">> LoginAction:execute > 登陆成功:"+inner);
		}else{
			System.out.println(">> LoginAction:execute > 登录失败:inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
			Manager.tips("密码错误，请重新输入！");//设置提示信息
			System.out.println(">> LoginAction:execute <ERROR");
			return ERROR;
		}
		System.out.println(">> LoginAction:execute <SUCCESS");
		return SUCCESS;//只有SUCCESS会redirect到menu
	}
	
	
	public String logout(){
		System.out.println(">> LoginAction:logout > inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
		Manager.clearSession();
		System.out.println(">> LoginAction:logout <SUCCESS");
		return SUCCESS;
	}
	
	
}
