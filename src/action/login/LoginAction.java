package action.login;

import java.sql.SQLException;
import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.Field;
import obj.staticObject.InnerPerson;

public class LoginAction extends ActionSupport{
	private static final long serialVersionUID = -2488160597018042665L;

	private InnerPerson inner;
	private int back=0;// 0表示直接进入，-1表示返回跳转，1表示登入
	
	public InnerPerson getInner(){return inner;}
	public void setInner(InnerPerson inner){this.inner=inner;}
	public void setBack(String a){this.back=Field.s2i(a);}
	
	public LoginAction() throws SQLException{
		super();
		System.out.println(">> LoginAction:constructor");
		inner=new InnerPerson();
	}
	
	@Override
	public String execute(){
		System.out.println(">> LoginAction:execute");
		boolean ok=false;
		if(inner.getId()==null&&inner.getPassword()==null){
			InnerPerson tmp=Manager.getUser();
			if(tmp!=null){
				ok=true;
				inner=tmp;
				System.out.println(">> LoginAction:execute > 从session中读取inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
			}else if(back<0){
				Manager.tips("已超时，请重新输入！");
			}else if(back>0){
				Manager.tips("密码错误，请重新输入！");
			}
		}else if(!ok){
			if(inner.checkPassword()){
				System.out.println(">> LoginAction:execute > 保存到session中:inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
				try {
					inner.load();
					Manager.setUser(inner);
					ok=true;
					System.out.println(">> LoginAction:execute > 登陆成功:"+inner);
				} catch (IllegalArgumentException | SQLException e) {
					Manager.tips("读取个人信息失败，请重新登录！",e);
				}
			}else{
				System.out.println(">> LoginAction:execute > 登录失败:inner(id="+inner.getId()+",ps="+inner.getPassword()+")");
				Manager.tips("密码错误，请重新输入！");
			}
		}
		if(!ok){
			Manager.clearSession();
			System.out.println(">> LoginAction:execute <LOGIN");
			return LOGIN;
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
