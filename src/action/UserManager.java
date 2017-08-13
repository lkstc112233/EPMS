package action;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;

import obj.staticObject.InnerPerson;

public class UserManager {
	
	static final public String userToken="inner";
	
	static public InnerPerson getUser(){
		Map<String, Object> session=ActionContext.getContext().getSession();
		Object tmp=session.get(userToken);
		if(tmp==null)
			return null;
		try{
			return (InnerPerson)tmp;
		}catch(ClassCastException e){
			return null;
		}
	}
	static public void setUser(InnerPerson inner){
		if(inner==null)
			removeUser();
		else
			ActionContext.getContext().getSession().put(userToken,inner);
	}
	static public void removeUser(){
		ActionContext.getContext().getSession().remove(userToken);
	}
	
	
	
	
}
