package obj;

import java.sql.*;
import java.util.*;

import obj.Base.JoinParam.Part;
import persistence.DB;

@SuppressWarnings("unchecked")
public abstract class Base {
	protected Base(){
		
	}
	/**
	 * 用于select描述信息
	 */
	public abstract String getDescription();
	
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
		if(this.checkKeyField())
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
		System.err.println("更新了"+num+"重值！("+pst.toString()+")");
		rs.first();
		if(num>0 && setFields){
			for(Field f:this.getFields()) if(!f.isKey())
				f.set(this,rs.getObject(f.getName()));
		}
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
		if(this.checkKeyField())
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
		if(this.checkKeyField())
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
		if(this.checkNotNullField())
			throw new IllegalArgumentException("The notNull fields are not completed!");
		StringBuilder sb=new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(this.getSQLTableName());
		boolean first=true;
		for(Field f:this.getFields()){
			if(first) first=false;
			else sb.append(",");
			sb.append(f.getName());
		}
		sb.append(" VALUES (");
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
			Base.MapList.put(clazz,res=Base.list(clazz,(Field[])null,(Object[])null,(Field[])null));
		return res;
	}
	static public <T extends Base> List<T> list(Class<T> clazz,Field[] checkFields,Object[] checkFieldsValue,Field[] orderFields)
			throws IllegalArgumentException, InstantiationException, SQLException{
		List<Base[]> tmp=Base.list(new JoinParam(clazz), checkFields, checkFieldsValue, orderFields);
		List<T> res=new ArrayList<T>();
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=1 && bs[0]!=null
					&& clazz!=null && clazz.isInstance(bs[0]))
				res.add(clazz.cast(bs[0]));
		}
		return res;
	}static public <T extends Base> List<T> list(Class<T> clazz,Field[] checkFields,Object[] checkFieldsValue,Field orderFields)
			throws IllegalArgumentException, InstantiationException, SQLException{
		return Base.list(clazz,checkFields,checkFieldsValue,new Field[]{orderFields});
	}static public <T extends Base> List<T> list(Class<T> clazz,Field[] checkFields,Object[] checkFieldsValue)
			throws IllegalArgumentException, InstantiationException, SQLException{
		return Base.list(clazz,checkFields,checkFieldsValue,(Field[])null);
	}static public <T extends Base> List<T> list(Class<T> clazz,Field checkFields,Object checkFieldsValue,Field[] orderFields)
			throws IllegalArgumentException, InstantiationException, SQLException{
		return Base.list(clazz,new Field[]{checkFields},new Object[]{checkFieldsValue},orderFields);
	}static public <T extends Base> List<T> list(Class<T> clazz,Field checkFields,Object checkFieldsValue,Field orderFields)
			throws IllegalArgumentException, InstantiationException, SQLException{
		return Base.list(clazz,new Field[]{checkFields},new Object[]{checkFieldsValue},new Field[]{orderFields});
	}static public <T extends Base> List<T> list(Class<T> clazz,Field checkFields,Object checkFieldsValue)
			throws IllegalArgumentException, InstantiationException, SQLException{
		return Base.list(clazz,new Field[]{checkFields},new Object[]{checkFieldsValue},(Field[])null);
	}static public <T extends Base> List<T> list(Class<T> clazz,Field orderFields)
			throws IllegalArgumentException, InstantiationException, SQLException{
		return Base.list(clazz,(Field[])null,(Object[])null,new Field[]{orderFields});
	}
	
	/**
	 * 多表联合查询
	 */
	static public enum JoinType{
		InnerJoin,LeftJoin,RightJoin;
		@Override
		public String toString(){
			return this.name().replaceAll("Join"," Join").toUpperCase();
		}
	}
	static public class JoinParam{
		static public class Part{
			private JoinType type;						public JoinType getType(){return type;}
			private Class<? extends Base> clazz;		public Class<? extends Base> getClazz(){return clazz;}
			private Set<Field> fields;					public Set<Field> getFields(){return fields;}
			private Field[] lastOnFields;			public Field[] getLastOnFields(){return this.lastOnFields;}
			private Field[] thisOnFields;			public Field[] getThisOnFields(){return this.thisOnFields;}
			private Field[] onCheckFields;			public Field[] getOnCheckFields(){return this.onCheckFields;}
			private Object[] onCheckFieldsValue;		public Object[] getOnCheckFieldsValue(){return this.onCheckFieldsValue;}
			private Part(Class<? extends Base> c,Set<Field> fields){this(null,c,fields,null,null,null,null);}
			public Part(JoinType jp,Class<? extends Base> c,Set<Field> fields,
					Field[] lastOnFields,Field[] thisOnFields,
					Field[] onCheckFields,Object[] onCheckFieldsValue){
				this.type=jp;this.clazz=c;
				this.lastOnFields=lastOnFields==null?new Field[0]:lastOnFields;
				this.thisOnFields=thisOnFields==null?new Field[0]:thisOnFields;
				this.onCheckFields=onCheckFields==null?new Field[0]:onCheckFields;
				this.onCheckFieldsValue=onCheckFieldsValue==null?new Object[0]:onCheckFieldsValue;
			}
			public String toString(Class<? extends Base> lastClazz){
				if(this.type==null || lastClazz==null)
					return Base.getSQLTableName(this.clazz);
				StringBuilder sb=new StringBuilder();
				sb.append(this.type.toString());
				sb.append(' ');
				Class<? extends Base> c1=lastClazz;
				Class<? extends Base> c2=this.clazz;
				String c1table=Base.getSQLTableName(c1);
				String c2table=Base.getSQLTableName(c2);
				sb.append(c2table);
				sb.append(" ON ( ");
				boolean first=true;
				for(int i=0;i<this.lastOnFields.length && i<this.thisOnFields.length;i++){
					if(first) first=false;
					else sb.append(" AND ");
					sb.append(c1table);sb.append('.');sb.append(this.lastOnFields[i].getName());
					sb.append(" = ");
					sb.append(c2table);sb.append('.');sb.append(this.thisOnFields[i].getName());
				}
				for(Field f:this.onCheckFields){
					if(first) first=false;
					else sb.append(" AND ");
					sb.append(c2table);sb.append('.');sb.append(f.getName());
					sb.append(" LIKE ?");
				}
				sb.append(" )");
				return sb.toString();
			}
		}
		private List<Part> list=new ArrayList<Part>();	public List<Part> getList(){return list;}
		
		public JoinParam(Class<? extends Base> clazz){
			this(clazz,Field.getFieldsSet(clazz));
		}public JoinParam(Class<? extends Base> clazz,Set<Field> fields){
			this.list.add(new Part(clazz,fields));
		}
		public Class<? extends Base> getClassByIndex(int index){
			return this.list.get(index).getClazz();
		}
		public int size(){return this.list.size();}
		
		private JoinParam append(Part part){
			if(part!=null) this.list.add(part);
			return this;
		}public JoinParam append(JoinType jp,Class<? extends Base> c,Set<Field> fields,
				Field[] lastOnFields,Field[] thisOnFields,
				Field[] onCheckFields,Object[] onCheckFieldsValue){
			return this.append(new Part(
					jp,c,fields,lastOnFields,thisOnFields,onCheckFields,onCheckFieldsValue));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,Set<Field> fields,
				Field[] lastOnFields,Field[] thisOnFields,
				Field onCheckFields,Object onCheckFieldsValue){
			return this.append(new Part(
					jp,c,fields,lastOnFields,thisOnFields,new Field[]{onCheckFields},new Object[]{onCheckFieldsValue}));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,Set<Field> fields,
				Field[] lastOnFields,Field[] thisOnFields){
			return this.append(new Part(
					jp,c,fields,lastOnFields,thisOnFields,(Field[])null,(Object[])null));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,Set<Field> fields,
				Field lastOnFields,Field thisOnFields,
				Field[] onCheckFields,Object[] onCheckFieldsValue){
			return this.append(new Part(
					jp,c,fields,new Field[]{lastOnFields},new Field[]{thisOnFields},onCheckFields,onCheckFieldsValue));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,Set<Field> fields,
				Field lastOnFields,Field thisOnFields,
				Field onCheckFields,Object onCheckFieldsValue){
			return this.append(new Part(
					jp,c,fields,new Field[]{lastOnFields},new Field[]{thisOnFields},new Field[]{onCheckFields},new Object[]{onCheckFieldsValue}));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,Set<Field> fields,
				Field lastOnFields,Field thisOnFields){
			return this.append(new Part(
					jp,c,fields,new Field[]{lastOnFields},new Field[]{thisOnFields},(Field[])null,(Object[])null));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,Set<Field> fields){
			return this.append(new Part(
					jp,c,fields,(Field[])null,(Field[])null,(Field[])null,(Object[])null));
		}/*无fields*/
		public JoinParam append(JoinType jp,Class<? extends Base> c,
				Field[] lastOnFields,Field[] thisOnFields,
				Field[] onCheckFields,Object[] onCheckFieldsValue){
			return this.append(new Part(
					jp,c,Field.getFieldsSet(c),lastOnFields,thisOnFields,onCheckFields,onCheckFieldsValue));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,
				Field[] lastOnFields,Field[] thisOnFields,
				Field onCheckFields,Object onCheckFieldsValue){
			return this.append(new Part(
					jp,c,Field.getFieldsSet(c),lastOnFields,thisOnFields,new Field[]{onCheckFields},new Object[]{onCheckFieldsValue}));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,
				Field[] lastOnFields,Field[] thisOnFields){
			return this.append(new Part(
					jp,c,Field.getFieldsSet(c),lastOnFields,thisOnFields,null,null));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,
				Field lastOnFields,Field thisOnFields,
				Field[] onCheckFields,Object[] onCheckFieldsValue){
			return this.append(new Part(
					jp,c,Field.getFieldsSet(c),new Field[]{lastOnFields},new Field[]{thisOnFields},onCheckFields,onCheckFieldsValue));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,
				Field lastOnFields,Field thisOnFields,
				Field onCheckFields,Object onCheckFieldsValue){
			return this.append(new Part(
					jp,c,Field.getFieldsSet(c),new Field[]{lastOnFields},new Field[]{thisOnFields},new Field[]{onCheckFields},new Object[]{onCheckFieldsValue}));
		}public JoinParam append(JoinType jp,Class<? extends Base> c,
				Field lastOnFields,Field thisOnFields){
			return this.append(new Part(
					jp,c,Field.getFieldsSet(c),new Field[]{lastOnFields},new Field[]{thisOnFields},null,null));
		}public JoinParam append(JoinType jp,Class<? extends Base> c){
			return this.append(new Part(
					jp,c,Field.getFieldsSet(c),null,null,null,null));
		}
		
		@Override
		public String toString(){
			StringBuilder sb=new StringBuilder();
			Class<? extends Base> lastClazz=null;
			for(Part p:this.list){
				sb.append(' ');
				sb.append(p.toString(lastClazz));
				lastClazz=p.getClazz();
			}
			return sb.toString();
		}
		static private String ListDelimiter="__LD__";
	}
	/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param,Field[] checkFields,Object[] checkFieldsValue,Field[] orderFields)
			throws IllegalArgumentException,SQLException, InstantiationException{
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT ");
		boolean first=true;
		for(Part p:param.getList()){
			String ct=Base.getSQLTableName(p.getClazz());
			for(Field f:p.fields){
				if(first) first=false;
				else sb.append(",");
				sb.append(ct);sb.append(".");sb.append(f.getName());
				sb.append(" AS ");
				sb.append(ct);sb.append(JoinParam.ListDelimiter);sb.append(f.getName());
			}
		}
		if(first) throw new IllegalArgumentException("There is NO field in SELECT Field!");
		sb.append(" FROM ");
		sb.append(param.toString());//===
		int checkLength=-1;
		if(checkFields!=null && checkFieldsValue!=null && checkFields.length>0 && checkFieldsValue.length>0
				&& checkFields[0]!=null && checkFieldsValue[0]!=null){
			sb.append(" WHERE ");
			checkLength=Math.min(checkFields.length,checkFieldsValue.length);
			for(int i=0;i<checkLength;i++){
				if(i!=0) sb.append(" AND ");
				sb.append(checkFields[i].getName());
				sb.append(" LIKE ?");
			}
		}
		if(orderFields!=null && orderFields.length>0 && orderFields[0]!=null){
			sb.append(" ORDER BY ");
			first=true;
			for(Field f:orderFields){
				if(first) first=false;
				else sb.append(",");
				sb.append(f.toString());
			}
		}
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int parameterIndex=1;
		for(Part part:param.getList())
			for(Object o:part.getOnCheckFieldsValue())
				pst.setObject(parameterIndex++,o);
		for(int i=0;i<checkLength;i++)
			pst.setObject(parameterIndex++,checkFieldsValue[i]);
		ResultSet rs=pst.executeQuery();
		List<Base[]> res=new ArrayList<Base[]>();
		int len=param.size();
		while(rs.next()){
			Base[] x=new Base[len];
			for(int i=0;i<len;i++){
				Class<? extends Base> c=param.getClassByIndex(i);
				String ct=Base.getSQLTableName(c);
				try{
					x[i]=c.newInstance();
				} catch (IllegalAccessException e) {
					throw new InstantiationException(e.getMessage());
				}
				boolean flag=true;
				for(Field f:Field.getFields(c)){
					String columnName=ct+JoinParam.ListDelimiter+f.getName();
					Object o=null;
					try{o=rs.getObject(columnName);}catch(SQLException e){}
					if(flag && o!=null) flag=false;
					try{
						f.set(x[i],o);
					}catch(IllegalArgumentException e){
						throw e;
					}
				}
				//若x[i]的属性全部都是null，则x[i]应为null
				if(flag) x[i]=null;
				//若x[i]的key的Field都是null，则x[i]应为null
				if(x[i]!=null && x[i].checkKeyField())
					x[i]=null;
			}
			res.add(x);
		}
		return res;
	}/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param,Field[] checkFields,Object[] checkFieldsValue,Field orderFields)
			throws IllegalArgumentException,SQLException, InstantiationException{
		return Base.list(param, checkFields, checkFieldsValue, new Field[]{orderFields});
	}/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param,Field[] checkFields,Object[] checkFieldsValue)
			throws IllegalArgumentException,SQLException, InstantiationException{
		return Base.list(param, checkFields, checkFieldsValue,(Field[])null);
	}/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param,Field checkFields,Object checkFieldsValue,Field[] orderFields)
			throws IllegalArgumentException,SQLException, InstantiationException{
		return Base.list(param, new Field[]{checkFields}, new Object[]{checkFieldsValue}, orderFields);
	}/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param,Field checkFields,Object checkFieldsValue,Field orderFields)
			throws IllegalArgumentException,SQLException, InstantiationException{
		return Base.list(param, new Field[]{checkFields}, new Object[]{checkFieldsValue},new Field[]{orderFields});
	}/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param,Field checkFields,Object checkFieldsValue)
			throws IllegalArgumentException,SQLException, InstantiationException{
		return Base.list(param,new Field[]{checkFields},new Object[]{checkFieldsValue},(Field[])null);
	}/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param,Field[] orderFields)
			throws IllegalArgumentException,SQLException, InstantiationException{
		return Base.list(param,(Field[])null,(Object[])null,orderFields);
	}/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param,Field orderFields)
			throws IllegalArgumentException,SQLException, InstantiationException{
		return Base.list(param,(Field[])null,(Object[])null,new Field[]{orderFields});
	}/**
	 * 根据JoinParam进行联合查询
	 */
	static public List<Base[]> list(JoinParam param)
			throws IllegalArgumentException,SQLException, InstantiationException{
		return Base.list(param,(Field[])null,(Object[])null,(Field[])null);
	}
	
	

}
