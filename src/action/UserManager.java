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
	
	static public final String SQLCheck_Success="OK";
	static public String SQLCheck(String sql){
		int cnt=0;
		for(char c:sql.toCharArray())
			if(c==';') cnt++;
		if(cnt>1)
			return "语句过多";
		if(sql.contains("DROP"))
			return "不允许删除数据表";
		if(sql.contains("CREATE"))
			return "不允许创建数据表";
		return SQLCheck_Success;
	}
	
	
	
}
