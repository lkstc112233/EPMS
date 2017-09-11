package action.jwc.sqlOperation;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import obj.Base;
import obj.Search;

/**
 * 导入免费师范生数据
 */
public class TableOperationAction extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private String tableName;
		public void setTableName(String a){this.tableName=a;}
		public String getTableName(){return this.tableName;}
		
	static public final String SessionSearchKey="TableOperation_Search"; 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TableOperationAction() throws SQLException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException{
		super();
		System.out.println(">> TableOperationAction:constructor > tableName="+this.getTableName());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Object o=session.get(SessionSearchKey);
		System.out.println(">> TableOperationAction:constructor > tableNameKey="+String.valueOf(o));
		if(o==null) return;
		this.search=(Search)o;
		this.setTableName(this.search.getTableName());
		this.updateBase=this.search.clazz.newInstance();
	}
	
	
	private Search<? extends Base> search=null;//查询信息
	private boolean modify=false;//是否可修改内容并提交
	private int choose=0;//操作项
	private Base updateBase;
	
	public Search<? extends Base> getSearch(){return this.search;} public void setSearch(Search<? extends Base> o){this.search=o;}
	public List<String> getTypes(){return Search.RestraintType.list();}
	public Boolean getModify(){return this.modify;} public void setModify(Boolean x){this.modify=x;} public void setModify(String x){this.modify=Boolean.valueOf(x);}
	public Integer getChoose(){return this.choose;} public void setChoose(Integer x){this.choose=x;} public void setChoose(String x){this.choose=Integer.valueOf(x);}
	public Base getUpdateBase(){return this.updateBase;}	public void setUpdateBase(Base b){this.updateBase=b;}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	/**
	 * 设置好Search的值（从session中读取并和当前tableName比对）
	 * 返回是否更新过（若和当前tableName不符，则会更新，更新后执行display而不是execute）
	 * @return
	 */
	private boolean setupSearch(){
		boolean res=false;
		Map<String, Object> session=ActionContext.getContext().getSession();
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		if(clazz!=null &&
				(this.search==null|| !this.search.clazz.equals(clazz))){
			System.out.println(">> TableOperationAction:display > session.TableNameKey="+this.getTableName());
			try {
				this.search=new Search(clazz);
				session.put(SessionSearchKey,this.search);
				res=true;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				this.search=null;
				return false;
			}
		}
		if(this.search!=null)
			this.setTableName(this.search.getTableName());
		return res;
	}
	
	
	@Override
	public String execute(){//执行查询
		System.out.println(">> TableOperationAction:execute > tableName="+this.getTableName());
		if(Base.getClassForName(tableName)==null)
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		if(this.setupSearch())
			return display();
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
	
	public String display(){
		System.out.println(">> TableOperationAction:display > tableName="+this.getTableName());
		this.setupSearch();
		System.out.println(">> TableOperationAction:display <NONE");
		return NONE;
	}
	
	
	public String update(){
		System.out.println(">> TableOperationAction:update > tableName="+this.getTableName());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Base b=this.getChooseBase();
		if(b==null) return NONE;
		try{
			b.update();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"修改参数错误！("+e.getMessage()+")");
			return NONE;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"修改权限错误！("+e.getMessage()+")");
			return NONE;
		} catch (SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库错误！("+e.getMessage()+")");
			return NONE;
		}
		session.put(token.ActionInterceptor.ErrorTipsName,
				"修改成功！");
		return SUCCESS;
	}
	public String delete(){
		System.out.println(">> TableOperationAction:delete > tableName="+this.getTableName());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Base b=this.getChooseBase();
		this.choose=0;
		if(b==null) return NONE;
		try {
			b.delete();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"修改参数错误！("+e.getMessage()+")");
			return NONE;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"修改权限错误！("+e.getMessage()+")");
			return NONE;
		} catch (SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库错误！("+e.getMessage()+")");
			return NONE;
		}
		System.out.println(">> TableOperationAction:delete > 删除成功");
		this.execute();
		session.put(token.ActionInterceptor.ErrorTipsName,
				"删除成功！");
		return SUCCESS;
	}
	private Base getChooseBase(){
		Map<String, Object> session=ActionContext.getContext().getSession();
		if(this.search==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"搜索引擎初始化失败！");
			return null;
		}
		List<? extends Base> list=this.search.getResultSet();
		try{
			return list.get(this.choose);
		}catch(IndexOutOfBoundsException e){
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"条目选择错误！");
			return null;
		}
	}


}
