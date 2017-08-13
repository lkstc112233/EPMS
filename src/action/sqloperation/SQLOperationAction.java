package action.sqloperation;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import obj.*;


public class SQLOperationAction extends ActionSupport{
	private static final long serialVersionUID = -2075656836784414352L;
	
	private String table=null;
	private Base selectObject;
	private int selectIndex=DefaultSelectIndex;
	static private final int DefaultSelectIndex=-1;

	public String getTable() {return table;}
	public void setTable(String table) {this.table=table;}
	public int getselectIndex(){return this.selectIndex;}
	public void setselectIndex(int selectIndex) {this.selectIndex = selectIndex;}
	public void setselectIndex(String s){
		if(s==null) selectIndex=DefaultSelectIndex;
		else try{
			selectIndex=Integer.parseInt(s);
		}catch(NumberFormatException e){
			selectIndex=DefaultSelectIndex;
		}
	}
	
	private boolean executive=false;
	public boolean isExecutive() {return executive;}
	public void setExecutive(boolean executive) {this.executive = executive;}
	
	
	public SQLOperationAction() throws SQLException, IllegalArgumentException, IllegalAccessException{
		super();
		System.out.println("SQLOperationAction constructor is working!");
	}
	
	@SuppressWarnings("null")
	@Override
	public String execute(){
		Map<String, Object> session=ActionContext.getContext().getSession();
		Object tmp=session.get(SQLOperationSelectAction.SESSION_List);
		System.out.println(">> SQLOperationAction:execute > "+SQLOperationSelectAction.SESSION_List
				+"="+tmp);
		List<?> list=null;
		try{
			if(tmp!=null)
				list=(List<?>) tmp;
			if(list==null||list.isEmpty())
				throw new ClassCastException("null!");
		}catch(ClassCastException e){
			System.out.println(">> SQLOperationAction:execute > 无法从session_List中提取表名 Exception("+e.getMessage()+")");
			System.out.println(">> SQLOperationAction:display <NONE");
			return NONE;
		}
		this.table=list.get(0).getClass().getSimpleName();
		System.out.println(">> SQLOperationAction:execute > table="+table);
		System.out.println(">> SQLOperationAction:display > 已选择条目序号:"+selectIndex);
		try{
			this.selectObject=(Base)list.get(this.selectIndex);
		}catch(ClassCastException|IndexOutOfBoundsException e){
			System.out.println(">> SQLOperationAction:display > 总条目数:"+list.size());
			session.put(token.ActionInterceptor.ErrorTipsName,"请选择列表中的条目进行修改操作！");
			System.out.println(">> SQLOperationAction:display <NONE");
			return NONE;
		}
		System.out.println(">> SQLOperationAction:display > 选择了第"+this.selectIndex+"行！");
		//Base对象合法
		System.out.println(">> SQLOperationAction:execute > selectObject="+selectObject.toString());
		String[] baselabels=null;
		String[] baselist=SQLCollection.getOutAll(this.selectObject,baselabels);
		System.out.print(">> SQLOperationAction:execute > labels:[");
		for(String l:baselabels)
			System.out.print(l+" | ");
		System.out.println("]");
		System.out.print(">> SQLOperationAction:execute > list:[");
		for(String l:baselist)
			System.out.print(l+" | ");
		System.out.println("]");
		System.out.println(">> SQLOperationAction:execute <SUCCESS");
		return SUCCESS;
	}
	public String display(){
		System.out.println(">> SQLOperationAction:display >");
		System.out.println(">> SQLOperationAction:display <NONE");
		return NONE;
	}

	
	
}
