package action.login;

import java.sql.SQLException;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
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
		Map<String, Object> session=ActionContext.getContext().getSession();
		if(inner.getId()==null&&inner.getPassword()==null){
			InnerPerson tmp=Manager.getUser();
			if(tmp==null){
				System.out.println(">> LoginAction:execute <LOGIN");
				return LOGIN;
			}
			inner=tmp;
			System.out.println(">> LoginAction:execute > 从session中读取inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
		}else if(inner.checkPassword()){
			System.out.println(">> LoginAction:execute > 保存到session中:inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
			Manager.setUser(inner);
			try {
				inner.load();
			} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
				e.printStackTrace();
				session.put(token.ActionInterceptor.ErrorTipsName,"读取个人信息失败，请重新登录！");//设置提示信息
				System.out.println(">> LoginAction:execute <ERROR");
				return ERROR;
			}
			System.out.println(">> LoginAction:execute > 登陆成功:"+inner);
		}else{
			System.out.println(">> LoginAction:execute > 登录失败:inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
			session.put(token.ActionInterceptor.ErrorTipsName,"密码错误，请重新输入！");//设置提示信息
			System.out.println(">> LoginAction:execute <ERROR");
			return ERROR;
		}
		System.out.println(">> LoginAction:execute <SUCCESS");
		return SUCCESS;
	}
	
	
	public String logout(){
		System.out.println(">> LoginAction:logout > inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
		Map<String, Object> session=ActionContext.getContext().getSession();
		session.clear();
		System.out.println(">> LoginAction:logout <SUCCESS");
		return SUCCESS;
	}
	
	
}
