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
		InnerPerson user=Manager.getUser();
//		System.out.println(">? ActionInterceptor > ("
//				+((System.currentTimeMillis()-org.apache.struts2.ServletActionContext.getRequest().getSession().getLastAccessedTime())/1000)
//				+"/"+org.apache.struts2.ServletActionContext.getRequest().getSession().getMaxInactiveInterval()
//				+")userToken="+inner);
		if(user==null)
			return Manager.tips("登录已超时，请重新登录！",
					"login");
		//判断的当前权限
	/*	<!-- Action名称登记 -->
		<!-- sudo开头的必须要求教务处权限 -->
		<!-- function开头的必须要求Manager.getUser().getInnerOffice()对应的权限 -->
		<!-- 其他名称均可访问 --> */
		StringBuilder errorMsg=null;
		if(actionName.startsWith("function")){
			Role role=Role.getRoleByInnerPerson(user);
			//function
			List<ACCESS> as=null;
			try{
				as=Base.list(ACCESS.class,new Restraint(Field.getField(ACCESS.class,"actionClass"),actionName));
			}catch(IllegalArgumentException|InstantiationException|SQLException e){
				return Manager.tips("数据库错误!",
						e,"error");
			}
			if(as==null || as.isEmpty()){
				//TODO 没有权限限制的action
			}else{
				//只要某一条包含当前actionName的ACCESS(actionClass)允许即可访问
				boolean ok=false;
				for(ACCESS a:as) if(role.getACCESS(a)){
					ok=true;break;
				}
				if(!ok){
					//一般很少运行到这里，所以把需要遍历的工作放在这里
					Set<Role> tmp=new HashSet<Role>();
					for(ACCESS a:as) tmp.addAll(Role.getAccessRolesList(a));
					errorMsg=new StringBuilder();
					for(Role r:tmp){
						if(errorMsg.length()>0) errorMsg.append(",");
						errorMsg.append(r.name);
					}
				}
			}
		}else if(actionName.startsWith("sudo")){
			//sudo
			//只有教务处可以访问
			Role role=Role.getRoleByInnerPerson(user);
			if(role!=Role.jwc)
				errorMsg=new StringBuilder(Role.jwc.getName());
		}else{
			//不包含“_”的Action
			//不限制
			errorMsg=null;
		}
		if(errorMsg!=null)
			return Manager.tips(user.getOffice()+"无权访问（只允许"+errorMsg.toString()+"访问），请重新登录！",
					"error");
		return invocation.invoke();
	}
	
}
