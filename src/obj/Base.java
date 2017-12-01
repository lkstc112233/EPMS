package obj;

import java.sql.*;
import java.util.*;

import action.POI;
import persistence.DB;

@SuppressWarnings("unchecked")
public abstract class Base extends Object implements Comparable<Base>, Cloneable{
	protected Base(){
		
	}
	/**
	 * 用于select描述信息
	 */
	public abstract String getDescription();
	//=============================================================
	//io
	//=============================================================
	static private SQLIO io=new POI();
		static public SQLIO io(){return io;}
	
	//=============================================================
	//SQL表名
	//=============================================================
	static public final String TableNames[]={
			"ACCESS","InnerOffice","Major","OuterOffice","Province","School","ZZMM",
			"PracticeBase",
			"Time","Student","Region","Plan"
	};
	static public final String packageNames[]={"staticSource","staticObject","annualTable"};
	static public Class<? extends Base> getClassForName(String name){
		if(name==null||name.isEmpty()) return null;
		for(String p:packageNames){
			try {
				Class<?> tmp=Class.forName("obj."+p+"."+name);
				Class<? extends Base> res=(Class<? extends Base>) tmp;
				return res;
			} catch (ClassNotFoundException | ClassCastException  e) {
			}
		}
		return null;
	}
	static public String getSQLTableName(Class<? extends Base> clazz){
		return clazz.getAnnotation(SQLTable.class).value();
	}
	public final String getSQLTableName(){
		return Base.getSQLTableName(this.getClass());
	}

	//=============================================================
	//关于Field
	//=============================================================
	public Field[] getFields(){
		return Field.getFields(this.getClass());
	}
	/**
	 * 该性能比Field.getField(Class,String)要差，慎用。
	 */
	static public Field getField(Class<? extends Base> clazz,String fieldName){
	/*	Class<? extends Base> clazz=Base.getClassForName(new Object(){public String getClassName(){
			String s=this.getClass().getName();
			return s.substring(0,s.lastIndexOf('$'));
		}}.getClassName());*/
		return Field.getField(clazz,fieldName);
	}
	public Field getField(String fieldName){
		return Base.getField(this.getClass(),fieldName);
	}
	
	public final Object[] getFieldsValue(){
		Field[] fs=this.getFields();
		Object[] res=new Object[fs.length];
		for(int i=0;i<fs.length;i++) res[i]=fs[i].get(this);
		return res;
	}
	/**
	 * 检查this是不是有key的Field为空
	 */
	public boolean checkKeyField(){
		for(Field f:this.getFields()) if(f.isKey())
			if(Field.nullValue(f.get(this)))
				return false;
		return true;
	}
	/**
	 * 检查this是不是有notNull的Field为空
	 */
	public boolean checkNotNullField(){
		for(Field f:this.getFields()) if(f.notNull())
			if(Field.nullValue(f.get(this)))
				return false;
		return true;
	}
	
	/**
	 * 把this的所有SQLField的值导入b中
	 * @param b 
	 */
	public void copyTo(Base b){
		if(b==null || this==b) return;
		for(Field f:this.getFields()) try{
			b.getField(f.getName())
				.set(b,f.get(this));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	//=============================================================
	//覆盖方法
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append('[');
		sb.append(this.getSQLTableName());
		for(Field f:this.getFields()){
			sb.append(',');
			if(f.isKey()) sb.append('*');
			sb.append(f.getName());
			sb.append('(');
			try{sb.append(f.get(this));
			}catch (IllegalArgumentException e){
				sb.append("?");
			}
			sb.append(')');
		}
		sb.append(']');
		return sb.toString();
	}
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(!(o instanceof Base)) return false;
		Base b=(Base)o;
		if(!this.getClass().equals(b.getClass())) return false;
		for(Field f:b.getFields()){
			Object ff=null,bb=null;
			try{
				ff=f.get(this);
				bb=f.get(b);
			}catch (IllegalArgumentException e) {
				return false;
			}
			if((ff==null && bb!=null)
					||(ff!=null && !ff.equals(bb)))
				return false;
		}
		return true;
	}
	private final int HashBase=7;
	@Override
	public int hashCode() {
		int res=0,base=1;
		for(Field f:this.getFields()) if(f.isKey())
			res+=f.get(this).hashCode()*(base*=HashBase);
		return res;
	}
	@Override
	public int compareTo(Base b) {
		for(Field f:this.getFields()) {
			Object o=f.get(this);
			Object o2=f.get(b);
			if(o==null)
				if(o2==null) continue;
				else return -1;
			@SuppressWarnings("rawtypes")
			int cmp=(o instanceof Comparable)?
					((Comparable)o).compareTo(o2):
						Integer.compare(o.hashCode(),o2.hashCode());
			if(cmp!=0) return cmp;
			else continue;
		}
		return 0;
	}
	@Override
	public Base clone() {
		try{
			Base res=this.getClass().newInstance();
			this.copyTo(res);
			return res;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	
	//=============================================================
	// 清除、存在
	// 增删改查
	//=============================================================
	public void clear(){
		for(Field f:this.getFields())try{
			f.set(this,null);
		} catch (IllegalArgumentException e) {}
	}
	
	public boolean existAndLoad() throws IllegalArgumentException, SQLException{
		return this.load(true)>0;
	}
	public boolean exist() throws InstantiationException, IllegalArgumentException, SQLException, IllegalAccessException{
		Base b=this.getClass().newInstance();
		this.copyTo(b);
		return b.load(false)>0;
	}
	public int load() throws SQLException, IllegalArgumentException{
		return this.load(true);
	}
	public int load(boolean setFields) throws SQLException, IllegalArgumentException{
		if(!this.checkKeyField())
			throw new IllegalArgumentException("The key fields are not completed!");
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT ");
		boolean first=true;
		for(Field f:this.getFields()){
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
		}
		sb.append(" FROM ");
		sb.append(this.getSQLTableName());
		sb.append(" WHERE ");
		first=true;
		for(Field f:this.getFields()) if(f.isKey()){
			if(first) first=false;
			else sb.append(" AND ");
			sb.append(f.getName());
			sb.append(" = ?");
		}
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int SQLParameterIndex=1;
		for(Field f:this.getFields()) if(f.isKey())
			pst.setObject(SQLParameterIndex++,f.get(this));
		ResultSet rs=pst.executeQuery();
		rs.last();
		int num=rs.getRow();
		System.err.println("查询了"+num+"重值！("+pst.toString()+")");
		rs.first();
		if(num>0 && setFields){
			for(Field f:this.getFields()) if(!f.isKey())
				f.set(this,rs.getObject(f.getName()));
		}
	//	if(num==0) throw new SQLException("未查询到值！");
		return num;
	}
	
	public void update()throws SQLException, IllegalArgumentException{
		this.update(this);
	}
	/**
	 * 把b的值更新到当前上
	 * @param b
	 */
	public void update(Base base)throws SQLException, IllegalArgumentException{
		if(base==null) return;
		if(!base.getClass().equals(this.getClass()))
			throw new IllegalArgumentException("类型不同！");
		if(!this.checkKeyField())
			throw new IllegalArgumentException("The key fields are not completed!");
		StringBuilder sb=new StringBuilder();
		sb.append("UPDATE ");
		sb.append(this.getSQLTableName());
		sb.append(" SET ");
		boolean first=true;
		for(Field f:this.getFields()){
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
			sb.append(" = ?");
		}
		sb.append(" WHERE ");
		first=true;
		for(Field f:this.getFields()) if(f.isKey()){
			if(first) first=false;
			else sb.append(" AND ");
			sb.append(f.getName());
			sb.append(" = ?");
		}
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int parameterIndex=1;
		for(Field f:this.getFields())
			pst.setObject(parameterIndex++,f.get(base));
		for(Field f:this.getFields()) if(f.isKey())
			pst.setObject(parameterIndex++,f.get(this));
		int num=pst.executeUpdate();
		System.err.println("更新了"+num+"重值！("+pst.toString()+")");
		base.copyTo(this);
		Base.MapList.remove(this.getClass());
	}
	
	public void delete() throws IllegalArgumentException, SQLException{
		if(!this.checkKeyField())
			throw new IllegalArgumentException("The key fields are not completed!");
		StringBuilder sb=new StringBuilder();
		sb.append("DELETE FROM ");
		sb.append(this.getSQLTableName());
		sb.append(" WHERE ");
		boolean first=true;
		for(Field f:this.getFields()) if(f.isKey()){
			if(first) first=false;
			else sb.append(" AND ");
			sb.append(f.getName());
			sb.append(" = ?");
		}
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int SQLParameterIndex=1;
		for(Field f:this.getFields()) if(f.isKey())
			pst.setObject(SQLParameterIndex++,f.get(this));
		int num=pst.executeUpdate();
		System.err.println("删除了"+num+"重值！("+pst.toString()+")");
		Base.MapList.remove(this.getClass());
	}
	
	public void create()throws SQLException, IllegalArgumentException, IllegalAccessException{
		if(!this.checkNotNullField())
			throw new IllegalArgumentException("The notNull fields are not completed!");
		StringBuilder sb=new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(this.getSQLTableName());
		sb.append(" (");
		boolean first=true;
		for(Field f:this.getFields()){
			if(first) first=false;
			else sb.append(",");
			sb.append(f.getName());
		}
		sb.append(") VALUES (");
		first=true;
		for(@SuppressWarnings("unused")Field f:this.getFields()){
			if(first) first=false;
			else sb.append(",");
			sb.append("?");
		}
		sb.append(")");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int SQLParameterIndex=1;
		for(Field f:this.getFields())
			pst.setObject(SQLParameterIndex++,f.get(this));
		int num=pst.executeUpdate();
		System.err.println("更新了"+num+"重值！("+pst.toString()+")");
		Base.MapList.remove(this.getClass());
	}
	
	
	

	//=============================================================
	// list相关
	//=============================================================
	/**
	 * 即用即查的
	 */
	static public interface ListableWithNoSave{
	}
	/**
	 * 是否是即用即查的
	 */
	static public boolean isListableWithNoSave(Class<? extends Base> clazz){
		return ListableWithNoSave.class.isAssignableFrom(clazz);
	}
	/**
	 * 只保存非即用即查的Base对应的list内容
	 */
	static private Map<Class<? extends Base>,List<? extends Base>> MapList=new HashMap<Class<? extends Base>,List<? extends Base>>();
	/**
	 * 列出清单，非即用即查的会立即从数据库查询一次 
	 */
	static public <T extends Base>  List<T> list(Class<T> clazz)
			throws SQLException, IllegalArgumentException, InstantiationException{
		List<T> res=(!Base.isListableWithNoSave(clazz)&&Base.MapList.containsKey(clazz))
				? (List<T>) Base.MapList.get(clazz) : null;
		if(res==null)
			Base.MapList.put(clazz, res = Base.list(clazz,(Restraint)null) );
		return res;
	}
	static public <T extends Base> List<T> list(Class<T> clazz,Restraint where)
			throws IllegalArgumentException, InstantiationException, SQLException{
		List<Base[]> tmp=Base.list(new JoinParam(clazz),where);
		List<T> res=new ArrayList<T>();
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=1 && bs[0]!=null
					&& clazz!=null && clazz.isInstance(bs[0]))
				res.add(clazz.cast(bs[0]));
		}
		return res;
	}
	
	/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param,Restraint restraint)
			throws IllegalArgumentException,SQLException, InstantiationException{
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT ");
		boolean first=true;
		for(JoinParam.Part p:param.getList()){
			for(Field f:Field.getFields(p.getClazz())){
				if(first) first=false;
				else sb.append(" , ");
				sb.append(f.getSQLField("."));
				sb.append(" AS ");
				sb.append(JoinParam.ListFieldPrefix);sb.append(f.getSQLField("_"));
			}
		}
		if(first) throw new IllegalArgumentException("There is NO field in SELECT Field!");
		sb.append(" FROM ");
		sb.append(param.toString());//===
		//新增的带orderId的字段会被强制排序
		List<Field> orderFields=new ArrayList<Field>();
		for(JoinParam.Part p:param.getList()) {
			for(Field f:Field.getFields(p.getClazz()))
				if(f.getName().equals(Field.orderIdFieldName))
					orderFields.add(f);
		}
		if(restraint!=null && restraint.getOrder()!=null) {
			for(Field f:restraint.getOrder())
				orderFields.add(f);
		}
		Field[] fs=orderFields.toArray(new Field[0]);
		restraint=new Restraint(restraint==null?null:restraint.getWhere(),
				fs);
		sb.append(restraint.getSQLString());
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int parameterIndex=1;
		for(JoinParam.Part part:param.getList())
			for(Object o:part.getOnCheckFieldsValue())
				pst.setObject(parameterIndex++,o);
		if(restraint!=null && restraint.getWhere()!=null)
			for(Restraint.Part part:restraint.getWhere()) if(part!=null)
				parameterIndex=part.setSQLParam(pst,parameterIndex);
		ResultSet rs=pst.executeQuery();
		rs.last();
		int num=rs.getRow();
		System.err.println("list了"+num+"重值！("+pst.toString()+")");
		rs.beforeFirst();
		List<Base[]> res=new ArrayList<Base[]>();
		while(rs.next()){
			Base[] x=param.newInstance();
			for(int i=0;i<x.length;i++){
				Class<? extends Base> c=param.getClassByIndex(i);
				boolean flag=true;
				for(Field f:Field.getFields(c)){
					String columnName=JoinParam.ListFieldPrefix+f.getSQLField("_");
					Object o=null;
					try{o=rs.getObject(columnName);}catch(SQLException e){}
					if(flag && o!=null) flag=false;
					if(f.isKey() && o==null) {flag=true;break;}
					try{
						f.set(x[i],o);
					}catch(IllegalArgumentException e){
						flag=false;
						try{f.set(x[i],null);
						}catch(IllegalArgumentException e2){
						}
					}
				}
				//若x[i]的属性全部都是null，则x[i]应为null
				if(flag) x[i]=null;
				//若x[i]的key的Field都是null，则x[i]应为null
				if(x[i]!=null && !x[i].checkKeyField())
					x[i]=null;
			}
			res.add(x);
		}
		return res;
	}static public List<Base[]> list(JoinParam param)
			throws IllegalArgumentException,SQLException, InstantiationException{
		return Base.list(param,(Restraint)null);
	}
	
	

}
