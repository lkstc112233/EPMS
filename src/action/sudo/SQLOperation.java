package action.sudo;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import persistence.DB;


public class SQLOperation extends ActionSupport{
	private static final long serialVersionUID = -2075656836784414352L;
	static public final String SESSION_List="SQLOperation_List";
	
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
		if(sql==null||sql.isEmpty())
			return display();
		System.out.println(">> SQLOperstionSelectAction:execute > sql: "+sql);
		String check=Manager.SQLCheck(sql);
		if(!Manager.SQLCheck_Success.equals(check)){
			System.out.println(">> SQLOperstionSelectAction:execute > sql不合法:"+check);
			return Manager.tips(check+"，请重新输入！",display());
		}
		try{
			Statement st=DB.con().createStatement();
			ResultSet rs=st.executeQuery(sql);
			rs.last();
			int num=rs.getRow();
			System.out.println(">> SQLOperstionSelectAction:execute > 查询到"+num+"重值");
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
			try {
				Statement st=DB.con().createStatement();
				int num=st.executeUpdate(sql);
				System.out.println(">> SQLOperstionSelectAction:execute > 更新"+num+"重值");
				Manager.tips("更新"+num+"个值!");
			}catch(SQLException e2) {
				return Manager.tips("数据库开小差去了，请重新输入！",
						e2,display());
			}
		}
		return SUCCESS;
	}
	
	public String display(){
		return NONE;
	}

	
	
}
