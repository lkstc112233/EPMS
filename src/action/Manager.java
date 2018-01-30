package action;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import obj.Base;
import obj.Field;
import obj.Restraint;
import obj.staticObject.InnerPerson;
import token.Role;

public class Manager {

	/*=======================================================
	 * 关于user
	 */
	static private final String userToken="inner";
	static private final String userRoleToken="role";
	static public InnerPerson getUser(){
		return Manager.loadSession(InnerPerson.class,userToken);}
	static public void setUser(InnerPerson inner){
		Manager.saveSession(userToken,inner);
		try{
			Manager.saveSession(userRoleToken,token.Role.getRole(inner));
		}catch(Exception e){Manager.removeSession(userRoleToken);}
	}
	static public void removeUser(){
		Manager.removeSession(userToken);}
	/*=======================================================
	 * 关于Annual
	 */
	static private final String annualToken="year";
	static public void setYear(int year) {
		Manager.saveSession(annualToken,Integer.valueOf(year));
	}
	static public Integer getYear() {
		return Manager.loadSession(Integer.class,annualToken);
	}
	/*=======================================================
	 * 关于ActionName
	 */
	static private final String chainListKey="chainList";
	static private final int ListMaxSize=10;
	static private LinkedList<String> getChainList(){
		@SuppressWarnings("unchecked")
		LinkedList<String> list=Manager.loadSession(LinkedList.class,chainListKey);
		if(list==null) Manager.saveSession(chainListKey,list=new LinkedList<String>());
		return list;
	}
	static private final String ActionNameKey="actionName";
	static private boolean checkActionNameAvailable(String a) {
		if(a==null) return false;
		if(a.isEmpty()) return false;
		if(a.equals("back")) return false;
		if(!a.contains("_"))
			return true;
		String b=a.substring(0,a.lastIndexOf("_"));
		if(!b.contains("_"))
			return true;
		String actionClassName=b.substring(b.lastIndexOf("_")+1,b.length());
		String c=actionClassName.toLowerCase();
		if(c.startsWith("export") && !c.equals("export")) return false;
		return true;
		
	}
	static public void setActionName(String actionName) {
		if(!Manager.checkActionNameAvailable(actionName)) return;
		Manager.saveSession(ActionNameKey,actionName);
		LinkedList<String> list=Manager.getChainList();
		if(list.isEmpty() || list.getFirst()==null || !list.getFirst().equals(actionName))
			list.addFirst(actionName);
		while(list.size()>ListMaxSize)
			Manager.removeLastActionName();
	}
	static public String removeLastActionName() {
		LinkedList<String> list=Manager.getChainList();
		String res=null;
		if(!list.isEmpty()) {
			res=list.getLast();
			list.removeLast();
		}
		return res;
	}
	static public String removeFirstActionName() {
		LinkedList<String> list=Manager.getChainList();
		String res=null;
		if(!list.isEmpty()) {
			res=list.getFirst();
			list.removeFirst();
		}
		return res;
	}
	static public String getActionName() {
		return Manager.loadSession(String.class,ActionNameKey);
	}
	/*=======================================================
	 * 关于Session
	 */
	static public void resetSession() {
		InnerPerson usr=Manager.getUser();
		Integer year=Manager.getYear();
		LinkedList<String> list=Manager.getChainList();
		String actionName=Manager.getActionName();
		//clear
		Manager.clearSession();
		//reload
		if(usr!=null)
			Manager.setUser(usr);
		if(year!=null)
			Manager.setYear(year);
		Manager.saveSession(Manager.chainListKey,list);
		Manager.saveSession(ActionNameKey,actionName);
	}
	
	static public void saveSession(String key,Object value){
		if(value==null) Manager.removeSession(key);
		else ActionContext.getContext().getSession().put(key,value);
	}
	static public <T> T loadSession(Class<T> clazz,String key){
		Object o=ActionContext.getContext().getSession().get(key);
		if(o==null || !clazz.isAssignableFrom(o.getClass()))
			return null;
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
		String errorTips=Manager.loadSession(String.class,TipsName);
		ActionContext.getContext().getSession().clear();
		if(errorTips!=null)
			Manager.saveSession(TipsName,errorTips);
	}

	/*=======================================================
	 * 关于errorTips
	 */
	static private final String TipsName="errorTips";
	static public void tips(String msg){
		Manager.tips(msg,null);
	}
	static public void tips(String msg,Throwable exception){
		if(msg==null) msg="";
		if(exception!=null){
			msg+="\n("+exception.getMessage()+")";
			exception.printStackTrace();
		}
		if(!msg.isEmpty()){
			String oldMsg=Manager.loadSession(String.class,TipsName);
			if(oldMsg!=null && !oldMsg.isEmpty())
				msg=oldMsg+"\n\n===========\n\n"+msg;
			Manager.saveSession(TipsName,msg
					.replaceAll("\n","\\\\n")
					.replaceAll("\"","\\\\\""));
			System.err.println("Tips>> "+msg);
		}
	}
	


	/*=======================================================
	 * 关于SQL
	 */
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

	/*=======================================================
	 * 关于time规整
	 */
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
	

	/*=======================================================
	 * 关于时间
	 */
	static public int getNowTimeMonth() {
		return Calendar.getInstance().get(Calendar.MONTH)+1;
	}static public int getNowTimeDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}static public int getNowTimeYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}static public int getNowTimeDayOfWeek() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}
	static public int getTimeMonth(Date date) {
		Calendar c=Calendar.getInstance();c.setTime(date);return c.get(Calendar.MONTH)+1;
	}static public int getTimeDay(Date date) {
		Calendar c=Calendar.getInstance();c.setTime(date);return c.get(Calendar.DAY_OF_MONTH);
	}static public int getTimeYear(Date date) {
		Calendar c=Calendar.getInstance();c.setTime(date);return c.get(Calendar.YEAR);
	}static public int getTimeDayOfWeek(Date date) {
		Calendar c=Calendar.getInstance();c.setTime(date);return c.get(Calendar.DAY_OF_WEEK);
	}
	

	/*=======================================================
	 * 关于ManagerList
	 */
	static List<InnerPerson> getManagerInnerPersons() throws IllegalArgumentException, InstantiationException, SQLException {
		return Base.list(InnerPerson.class,new Restraint(new Restraint.Part[] {
				new Restraint.Part(Field.getField(InnerPerson.class,"office"),Restraint.Type.Like,"%"+Role.lxr.getName()+"%"),
				new Restraint.Part(Field.getField(InnerPerson.class,"school"),Role.jwc.getName())
		}));
	}
}
