package action;

import java.sql.Timestamp;

import com.opensymphony.xwork2.ActionContext;

import obj.staticObject.InnerPerson;

public class Manager {
	

	static public void saveSession(String key,Object value){
		if(value==null) Manager.removeSession(key);
		else ActionContext.getContext().getSession().put(key,value);
	}
	static public <T> T loadSession(Class<T> clazz,String key){
		Object o=ActionContext.getContext().getSession().get(key);
		if(o==null || !o.getClass().isAssignableFrom(clazz)) return null;
		try{
			return clazz.cast(o);
		}catch(ClassCastException e){return null;}
	}
	static public Object removeSession(String key){
		Object o=ActionContext.getContext().getSession().get(key);
		ActionContext.getContext().getSession().remove(key);
		return o;
	}
	static public void clearSession(){
		ActionContext.getContext().getSession().clear();
	}

	static private final String TipsName="errorTips";
	static public void tips(String msg){
		Manager.tips(msg,null,null);
	}
	static public void tips(String msg,Throwable exception){
		Manager.tips(msg,exception,null);
	}
	static public String tips(String msg,String result){
		return Manager.tips(msg,null,result);
	}
	static public String tips(String msg,Throwable exception,String result){
		if(msg==null) msg="";
		if(exception!=null){
			msg+="\n("+exception.getMessage()+")";
			exception.printStackTrace();
		}
		if(!msg.isEmpty()){
			Manager.saveSession(TipsName,msg.replaceAll("\n","\\\\n"));
			System.err.println("Tips>> "+msg);
		}
		System.err.println("Tips>> return "+result);
		return result;
	}
	
	static private final String userToken="inner";
	static public InnerPerson getUser(){
		return Manager.loadSession(InnerPerson.class,userToken);}
	static public void setUser(InnerPerson inner){
		Manager.saveSession(userToken,inner);}
	static public void removeUser(){
		Manager.removeSession(userToken);}
	
	
	
	static public final String SQLCheck_Success="OK";
	static public String SQLCheck(String sql){
		int cnt=0;
		for(char c:sql.toCharArray())
			if(c==';') cnt++;
		if(cnt>1)
			return "语句过多";
		if(sql.toUpperCase().contains("DROP"))
			return "不允许删除数据表";
		if(sql.toUpperCase().contains("CREATE"))
			return "不允许创建数据表";
		return SQLCheck_Success;
	}
	
	public static boolean RegularPeriod(Timestamp t1,Timestamp t2){
		if(t2==null) return false;
		if(t1==null){
			t1=t2;t2=null;return true;
		}
		if(t1.after(t2)){
			Timestamp tmp=t1;t1=t2;t2=tmp;
			return true;
		}
		return false;
	}
	
}
