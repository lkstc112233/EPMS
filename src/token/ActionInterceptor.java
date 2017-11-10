package token;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import action.Manager;
import obj.*;
import obj.staticObject.InnerPerson;
import obj.staticSource.ACCESS;

public class ActionInterceptor extends AbstractInterceptor{
	private static final long serialVersionUID = -6659361640363083885L;	
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		String actionName=invocation.getInvocationContext().getName();
//		System.out.println(">? ActionInterceptor > (actionName="+actionName+")");
		InnerPerson inner=Manager.getUser();
//		System.out.println(">? ActionInterceptor > ("
//				+((System.currentTimeMillis()-org.apache.struts2.ServletActionContext.getRequest().getSession().getLastAccessedTime())/1000)
//				+"/"+org.apache.struts2.ServletActionContext.getRequest().getSession().getMaxInactiveInterval()
//				+")userToken="+inner);
		if(inner==null)
			return Manager.tips("登录已超时，请重新登录！",
					"error");
		//判断的当前权限
		List<ACCESS> as=null;
		try{
			as=Base.list(ACCESS.class,new Restraint(Field.getField(ACCESS.class,"actionClass"),actionName));
		}catch(IllegalArgumentException|InstantiationException|SQLException e){
			return Manager.tips("数据库错误!",
					e,"error");
		}
		if(as==null || as.isEmpty()){
			//没有权限限制的action
		}else{
			Role role=Role.getRoleByInnerPerson(Manager.getUser());
			boolean ok=false;
			//只要某一条包含当前actionName的ACCESS(actionClass)允许即可访问
			for(ACCESS a:as) if(role.getACCESS(a)){
				ok=true;break;
			}
			if(!ok){
				//一般很少运行到这里，所以把需要遍历的工作放在这里
				Set<Role> tmp=new HashSet<Role>();
				for(ACCESS a:as) tmp.addAll(Role.getAccessRolesList(a));
				StringBuilder x=new StringBuilder();
				for(Role r:tmp){
					if(x.length()>0) x.append(",");
					x.append(r.name);
				}
				return Manager.tips(inner.getOffice()+"无权访问（只允许"+x+"访问），请重新登录！",
						"error");
			}
		}
		return invocation.invoke();
	}
	
}
