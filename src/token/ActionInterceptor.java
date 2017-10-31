package token;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import action.Manager;
import obj.staticObject.InnerPerson;

public class ActionInterceptor extends AbstractInterceptor{
	private static final long serialVersionUID = -6659361640363083885L;	
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		String actionName=invocation.getInvocationContext().getName();
		System.out.println(">? ActionInterceptor > (actionName="+actionName+")");
		InnerPerson inner=Manager.getUser();
		System.out.println(">? ActionInterceptor > ("
				+((System.currentTimeMillis()-ServletActionContext.getRequest().getSession().getLastAccessedTime())/1000)
				+"/"+ServletActionContext.getRequest().getSession().getMaxInactiveInterval()
				+")userToken="+inner);
		if(inner==null)
			return Manager.tips("登录已超时，请重新登录！",
					"error");
		String actionType=actionName.split("_",2)[0];
		if(Role.containActionPrefix(actionType)){
			if(!actionType.equals(Role.getActionPrefix(Role.getRoleByOffice(inner))))
				return Manager.tips(inner.getOffice()+"无权访问("+actionType+")，请重新登录！",
						"error");
		}
		return invocation.invoke();
	}
	
}
