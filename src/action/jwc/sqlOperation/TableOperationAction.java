package action.jwc.sqlOperation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import obj.Base;
import obj.ListableBase;
import obj.SQLField;
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
		this.search=o==null?null:((Search)o);
		if(search!=null)
			this.setTableName(this.search.getTableName());
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		//这里的初始化过程是为了在create和delete和update下保存回传的参数的
		this.setupOther(clazz);
	}
	
	
	private Search<? extends Base> search=null;//查询信息
	private boolean modify=false;//是否可修改内容并提交
	private int choose=0;//操作项
	private Base updateBase;
	private Pair[] updateBaseFieldsSourceList;		public Pair[] getUpdateBaseFieldsSourceList(){return this.updateBaseFieldsSourceList;}
		static public class Pair{
			private Map<String,String> list;
			public Pair(Map<String,String> list){this.list=list;}
			public Map<String,String> getList(){return list;}
			/*
			private List<? extends Base> list;
			private Field field=null;
			Pair(List<? extends Base> list,Field field){this.list=list;this.field=field;}
			public List<? extends Base> getList(){return list;}
			public Field getField(){return field;}
			public String getFieldName(){return field.getName();}
			*/
		}
	private Base createNewBase;
	
	public Search<? extends Base> getSearch(){return this.search;} public void setSearch(Search<? extends Base> o){this.search=o;}
	public List<String> getTypes(){return Search.RestraintType.list();}
	public Boolean getModify(){return this.modify;} public void setModify(Boolean x){this.modify=x;} public void setModify(String x){this.modify=Boolean.valueOf(x);}
	public Integer getChoose(){return this.choose;} public void setChoose(Integer x){this.choose=x;} public void setChoose(String x){this.choose=Integer.valueOf(x);}
	public Base getUpdateBase(){return this.updateBase;}	public void setUpdateBase(Base b){this.updateBase=b;}
	public Base getCreateNewBase(){return this.createNewBase;}	public void setCreateNewBase(Base b){this.createNewBase=b;}
	
	/**
	 * 设置除了search之外的东西
	 */
	@SuppressWarnings("unchecked")
	private void setupOther(Class<? extends Base> clazz){
		if(clazz==null) return;
		try {
			this.updateBase=clazz.newInstance();
			this.createNewBase=clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		List<Field> fs=Base.getFields(clazz);
		this.updateBaseFieldsSourceList=new Pair[fs.size()];
		for(int i=0;i<this.updateBaseFieldsSourceList.length;i++){
			this.updateBaseFieldsSourceList[i]=null;
			Field f=fs.get(i);
			SQLField s=f.getAnnotation(SQLField.class);
			String[] ss=s.source().split("\\.",2);
			Class<? extends Base> sourceClass=Base.getClassForName(ss[0]);
			if(sourceClass==null) continue;
			if(!ListableBase.class.isAssignableFrom(sourceClass)) continue;
			try {
				Method m=sourceClass.getMethod("list",sourceClass.getClass());
				Field field = Base.getField(sourceClass,ss[1]);
				List<? extends Base> list=(List<? extends Base>) m.invoke(null,sourceClass);
				Map<String,String> map=new TreeMap<String,String>();
				field.setAccessible(true);
				for(Base b:list){
					Object o=field.get(b);
					String key=o==null?null:o.toString();
					String value=b.toNametring();
					if(value==null)
						value=key;
					else
						value+="("+key+")";
					map.put(key,value);
				}
				this.updateBaseFieldsSourceList[i]=//new Pair(list,field);
						new Pair(map);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	/**
	 * 在Action的construction中只读取session和初始化其他值（除searcher之外的，updateBase、createNewBase等）
	 * 在Action调用method中会预先调用setupSearch来初始化search
	 * 主要工作是比对当前search（或为null或在construction中从session中提取的）和tableName的匹配性
	 * 如果不匹配，则利用tableName新建Search并赋值给search
	 * 最后将search放入session
	 * 
	 * 注：在setup中规整search，使之符合tableName（实际的tableName会在construction之后被赋值）
	 * @return 是否更新过（若和当前tableName不符，则会更新，更新后执行display而不是execute）
	 */
	private boolean setupSearch(){
		boolean res=false;
		Map<String, Object> session=ActionContext.getContext().getSession();
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		if(clazz==null) this.search=null;
		else{
			if(this.search==null|| !this.search.clazz.equals(clazz)){
				System.out.println(">> TableOperationAction:display > session.TableNameKey="+this.getTableName());
				try {
					this.search=new Search(clazz);
					session.put(SessionSearchKey,this.search);
					res=true;
					this.setTableName(this.search.getTableName());
					//只有在发生search修正的时候才会重新生成该两项
					this.setupOther(clazz);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
					this.search=null;
					return false;
				}
			}
		}
		session.put(SessionSearchKey,this.search);
		return res;
	}
	
	
	@Override
	public String execute(){//执行查询
		System.out.println(">> TableOperationAction:execute > tableName="+this.getTableName());
		if(Base.getClassForName(tableName)==null||
				this.setupSearch())
			return display();
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
	
	public String display(){
		System.out.println(">> TableOperationAction:display > tableName="+this.getTableName());
		this.setupSearch();
		System.out.println(">> TableOperationAction:display <NONE");
		return NONE;
	}
	

	/**
	 * 更新选中条（根据choose值）
	 */
	public String update(){
		System.out.println(">> TableOperationAction:update > tableName="+this.getTableName());
		if(Base.getClassForName(tableName)==null||
				this.setupSearch())
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		Base b=this.getChooseBase();
		this.choose=0;
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
		System.out.println(">> TableOperationAction:delete > 修改成功");
		this.execute();
		session.put(token.ActionInterceptor.ErrorTipsName,
				"修改成功！");
		return SUCCESS;
	}
	/**
	 * 删除选中条（根据choose值）
	 */
	public String delete(){
		System.out.println(">> TableOperationAction:delete > tableName="+this.getTableName());
		if(Base.getClassForName(tableName)==null||
				this.setupSearch())
			return display();
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
	/**
	 * 新建条（新建createBase）
	 */
	public String create(){
		System.out.println(">> TableOperationAction:create > tableName="+this.getTableName());
		System.out.println(">> TableOperationAction:create > tableName="+this.createNewBase.toString());
		if(Base.getClassForName(tableName)==null||
				this.setupSearch())
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		try{if(this.createNewBase.checkKeyNull()){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"新建条目内容不充分，请补全！");
			return NONE;
		}}catch(IllegalArgumentException | IllegalAccessException e){
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开小差去了，暂时无法创建！("+e.getMessage()+")");
			return NONE;
		}
		try {
			this.createNewBase.create();
		}catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开小差去了，暂时无法创建！("+e.getMessage()+")");
			return NONE;
		}catch(SQLException e){
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库发现问题，无法创建该条目！("+e.getMessage()+")");
			return NONE;
		}
		try {
			this.createNewBase=this.search.clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		session.put(token.ActionInterceptor.ErrorTipsName,
				"创建成功！");
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
