package action.jwc;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import obj.Base;
import obj.Search;

/**
 * 导入免费师范生数据
 */
public class TableOperationAction extends action.login.AnnualAction{
	private static final long serialVersionUID = 5998268336475528662L;

	private String tableName;
		public void setTableName(String a){this.tableName=a;}
		public String getTableName(){return this.tableName;}
		
	static public final String SessionSearchKey="TableOperation_Search"; 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TableOperationAction() throws SQLException, NoSuchFieldException, SecurityException{
		super();
		System.out.println(">> TableOperationAction:constructor > year="+this.getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Object o=session.get(SessionSearchKey);
		System.out.println(">> TableOperationAction:constructor > tableNameKey="+String.valueOf(o));
		if(o==null) return;
		this.search=(Search)o;
		this.setTableName(this.search.getTableName());
	}
	
	
	private Search<? extends Base> search=null;//查询信息
	private Boolean modify;//是否可修改内容并提交
	private Integer choose;//操作项
	
	public Search<? extends Base> getSearch(){return this.search;} public void setSearch(Search<? extends Base> o){this.search=o;}
	public List<String> getTypes(){return Search.RestraintType.list();}
	public Boolean getModify(){return this.modify;} public void setModify(Boolean x){this.modify=x;} public void setModify(String x){this.modify=Boolean.valueOf(x);}
	public Integer getChoose(){return this.choose;} public void setChoose(Integer x){this.choose=x;} public void setChoose(String x){this.choose=Integer.valueOf(x);}
	
	
	
	@Override
	public String execute(){//执行查询
		if(!executive)
			return display();
		System.out.println(">> TableOperationAction:execute > year="+this.getYear());
		if(Base.getClassForName(tableName)==null)
			return display();
		System.out.println(">> TableOperationAction:execute > tableName="+this.getTableName());
		Map<String, Object> session=ActionContext.getContext().getSession();
		if(this.search==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"搜索引擎初始化失败！");
			return NONE;
		}
		//===
		session.put(SessionSearchKey,this.search);
		try {
			this.search.execute();
		} catch (IllegalArgumentException|IllegalAccessException|InstantiationException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"搜索结果实例初始化失败！");
			return NONE;
		} catch (SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库开小差去了！");
			return NONE;
		}
		System.out.println(">> TableOperationAction:execute > resultSet count="+this.search.getResultSet().size());
		return SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String display(){
		System.out.println(">> TableOperationAction:display > year="+this.getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		if(Base.getClassForName(this.getTableName())!=null){
			System.out.println(">> TableOperationAction:display > session.TableNameKey="+this.getTableName());
			try {
				this.search=new Search(Base.getClassForName(this.getTableName()));
				session.put(SessionSearchKey,this.search);
				this.setTableName(this.search.getTableName());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		System.out.println(">> TableOperationAction:display <NONE");
		return NONE;
	}


}
