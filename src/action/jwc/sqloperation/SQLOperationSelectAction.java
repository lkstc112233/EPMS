package action.jwc.sqloperation;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import persistence.DB;


public class SQLOperationSelectAction extends ActionSupport{
	private static final long serialVersionUID = -2075656836784414352L;
	static public final String SESSION_List="SQLOperationSelectAction_List";
	
	private String sql="";
	private List<String[]> list=null;
	private String[] labels=null;

	public String getSql() {return sql;}
	public void setSql(String sql) {this.sql=sql;}
	public List<String[]> getList() {return list;}
	public void setList(List<String[]> list) {this.list = list;}
	public String[] getLabels() {return labels;}
	public void setLabels(String[] labels) {this.labels = labels;}

	
	@Override
	public String execute(){
		System.out.println(">> SQLOperationSelectAction:execute > sql: "+sql);
		if(sql==null||sql.isEmpty())
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		String check=Manager.SQLCheck(sql);
		if(!Manager.SQLCheck_Success.equals(check)){
			System.out.println(">> SQLOperationSelectAction:execute > sql不合法:"+check);
			session.put(token.ActionInterceptor.ErrorTipsName,check+"，请重新输入！");
			return display();
		}
		try{
			Statement st=DB.con().createStatement();
			ResultSet rs=st.executeQuery(sql);
			rs.last();
			int num=rs.getRow();
			System.out.println(">> SQLOperationSelectAction:execute > 查询到"+num+"重值");
			rs.beforeFirst();
			ResultSetMetaData md=rs.getMetaData();
			labels=new String[md.getColumnCount()];
			for(int i=0;i<labels.length;i++)
				labels[i]=md.getColumnName(i+1)+"("+md.getColumnTypeName(i+1)+")";
			list=new ArrayList<String[]>();
			while(rs.next()){
				String[] obj=new String[labels.length];
				for(int i=0;i<labels.length;i++){
					try{
						Object tmp=rs.getObject(i+1);
						obj[i]=tmp==null?"-NULL-":tmp.toString();
					}catch(SQLException e){
						e.printStackTrace();
						obj[i]="-error-";
					}
				}
				list.add(obj);
			}
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println(">> SQLOperationSelectAction:execute > 数据库问题 Exception("+e.getMessage()+")");
			session.put(token.ActionInterceptor.ErrorTipsName,"数据库开小差去了，请重新输入！");
			return display();
		}
		System.out.println(">> SQLOperationSelectAction:execute <SUCCESS");
		return SUCCESS;
	}
	public String display(){
		System.out.println(">> SQLOperationSelectAction:display >");
		System.out.println(">> SQLOperationSelectAction:display <NONE");
		return NONE;
	}

	
	
}
