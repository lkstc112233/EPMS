package obj;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import persistence.DB;

public abstract class ListableBase extends Base{
	
	/**
	 * 即用即查的
	 */
	static public interface ListableBaseWithNoSave{
	}
	/**
	 * 是否是即用即查的
	 */
	static public boolean isListableBaseWithNoSave(Class<? extends ListableBase> clazz){
		return ListableBaseWithNoSave.class.isAssignableFrom(clazz);
	}

	public ListableBase() throws SQLException {
		super();
	}
	
	//只保存非即用即查的Base对应的list内容
	static private Map<Class<? extends ListableBase>,List<ListableBase>> StaticList=new HashMap<Class<? extends ListableBase>,List<ListableBase>>();
	static public <T extends ListableBase>  List<T> list(Class<T> clazz,String[] checkFieldNames,Object[] checkFieldValues) throws SQLException, NoSuchFieldException{
		List<T> res=new ArrayList<T>();
		Field[] checkFields=new Field[checkFieldNames.length];
		for(int i=0;i<checkFieldNames.length;i++)
			checkFields[i]=Base.getField(clazz,checkFieldNames[i]);
		res.addAll(SQLCollection.selectAll(clazz,checkFields,checkFieldValues));
		return res;
	}
	static public <T extends ListableBase>  List<T> list(Class<T> clazz) throws SQLException{
		List<ListableBase> tmp;
		boolean nosave=ListableBase.isListableBaseWithNoSave(clazz);
		if(nosave||
				(tmp=ListableBase.StaticList.get(clazz))==null){
			tmp=ListableBase.initializeList(clazz);
			if(!nosave)//非即用即查的就保存
				ListableBase.StaticList.put(clazz,tmp);
			if(tmp==null) throw new SQLException(
					"ListableBase.list CANNOT initialize the class("+clazz.getName()+")");
		}
		List<T> res=new ArrayList<T>();
		for(ListableBase t:tmp){
			if(clazz.isInstance(t))
				res.add(clazz.cast(t));
		}
		return res;
	}
	static private List<ListableBase> initializeList(Class<? extends ListableBase> clazz){
		List<ListableBase> res=new ArrayList<ListableBase>();
		try {
			res.addAll(SQLCollection.selectAll(clazz,null,null));//主要代码，从数据库读取数据，这里不会调用Base.load()
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public void update() throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update();
		Class<? extends ListableBase> clazz=this.getClass();
		ListableBase.StaticList.remove(clazz);
	}
	@Override
	public void update(Field[] updateFields) throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update(updateFields);
		Class<? extends ListableBase> clazz=this.getClass();
		ListableBase.StaticList.remove(clazz);
	}
	@Override
	public void create() throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.create();
		Class<? extends ListableBase> clazz=this.getClass();
		ListableBase.StaticList.remove(clazz);
	}
	
	
	/*
	 * 多表联合查询
	 */
	static public enum JoinType{
		InnerJoin,LeftJoin,RightJoin;
		@Override
		public String toString(){
			return this.name().replaceAll("J"," J").toUpperCase();
		}
	}
	static public class JoinParam{
		private List<Class<? extends Base>> cs;	//0  1  2  3  4...
		private List<String> onFieldNames;		//0  12 34 56 78...
		private List<String[]> onCheckFieldNames;
		private List<Object[]> onCheckFieldValues;
		private List<JoinType> jps;
		public void clear(){if(cs!=null) cs.clear();}
		public int size(){return cs==null?0:cs.size();}
		private Class<? extends Base> getClassByIndex(int index){return cs==null?null:cs.get(index);}
		private List<Class<? extends Base>> getClassList(){return cs;}
		public JoinParam(Class<? extends Base> c)throws NullPointerException{
			if(c==null) throw new NullPointerException("JoinParam cannot accept the first class is NULL!");
			this.cs=new ArrayList<Class<? extends Base>>();
			this.onFieldNames=new ArrayList<String>();
			this.onCheckFieldNames=new ArrayList<String[]>();
			this.onCheckFieldValues=new ArrayList<Object[]>();
			this.cs.add(c);
			this.jps=new ArrayList<JoinType>();
		}
		public void append(String field1Name,JoinType jp,Class<? extends Base> c,String field2Name) throws NoSuchFieldException{
			this.append(field1Name,jp,c,field2Name,null,null);
		}
		public void append(String field1Name,JoinType jp,Class<? extends Base> c,String field2Name,
				String[] oncheckFieldNames,Object[] oncheckFieldValues) throws NoSuchFieldException{
			Class<? extends Base> c1=cs.get(cs.size()-1);
			Class<? extends Base> c2=c;
			Base.getField(c1,field1Name);
			Base.getField(c2,field2Name);
			this.onFieldNames.add(field1Name);
			this.onFieldNames.add(field2Name);
			this.onCheckFieldNames.add(oncheckFieldNames==null?new String[0]:oncheckFieldNames);
			this.onCheckFieldValues.add(oncheckFieldValues==null?new Object[0]:oncheckFieldValues);
			this.jps.add(jp);
			this.cs.add(c2);
		}
		@Override
		public String toString(){
			StringBuilder sb=new StringBuilder();
			sb.append(Base.getSQLTableName(cs.get(0)));
			for(int i=1;i<cs.size();i++){
				sb.append(' ');
				sb.append(jps.get(i-1));
				sb.append(' ');
				Class<? extends Base> c1=cs.get(i-1);
				Class<? extends Base> c2=cs.get(i);
				String c1table=Base.getSQLTableName(c1);
				String c2table=Base.getSQLTableName(c2);
				String f1=onFieldNames.get((i-1)<<1);
				String f2=onFieldNames.get((i<<1)-1);
				sb.append(c2table);
				sb.append(" ON ( ");
				sb.append(c1table);sb.append('.');sb.append(f1);
				sb.append(" = ");
				sb.append(c2table);sb.append('.');sb.append(f2);
				for(String onCheckFieldName:this.onCheckFieldNames.get(i-1)){
					sb.append(" AND ");
					sb.append(c2table);sb.append('.');sb.append(onCheckFieldName);
					sb.append(" = ?");
				}
				sb.append(" )");
			}
			return sb.toString();
		}
		public Class<? extends Base> firstClass(){
			return cs.get(0);
		}
	}
	static public List<Base[]> list(JoinParam param) throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException{
		return list(param,null,null,null);}
	static public List<Base[]> list(JoinParam param,String[] checkFields,Object[] checkObjects) throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException{
		return list(param,checkFields,checkObjects,null);}
	static public List<Base[]> list(JoinParam param,String[] checkFields,Object[] checkObjects,String[] orderFields)
			 throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException{
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT ");
		boolean first=true;
		for(Class<? extends Base> _c:param.getClassList()){
			String _ct=Base.getSQLTableName(_c);
			for(Field f:Base.getFields(_c)){
				f.setAccessible(true);
				if(first) first=false;
				else sql.append(',');
				sql.append(_ct);
				sql.append('.');
				sql.append(f.getName());
				sql.append(" AS ");
				sql.append(_ct);
				sql.append('_');
				sql.append(f.getName());//利用AS对结果集列名取别名为"表名_原列名"
			}
		}
		sql.append(" FROM ");
	//	sql.append(Base.getSQLTableName(c));	//already have in param.toString()
	//	sql.append(' ');						//already have in param.toString()
		sql.append(param.toString());
		int checkLength=-1;
		if(checkFields!=null && checkObjects!=null && checkFields.length>0 && checkObjects.length>0
				&& checkFields[0]!=null && checkFields[0].length()>0 && checkObjects[0]!=null){
			sql.append(" WHERE ");
			checkLength=Math.min(checkFields.length,checkObjects.length);
			for(int i=0;i<checkLength;i++){
				if(i!=0) sql.append(" AND ");
				sql.append(checkFields[i]);
				sql.append(" = ");
				sql.append('?');
			}
		}
		if(orderFields!=null && orderFields.length>0 && orderFields[0]!=null && orderFields[0].length()>0){
			sql.append(" ORDER BY ");
			boolean flag=true;
			for(String s:orderFields){
				if(flag) flag=false;
				else sql.append(",");
				sql.append(s);
			}
		}
		PreparedStatement pst=DB.con().prepareStatement(sql.toString());
		int pstindex=0;
		for(Object[] os:param.onCheckFieldValues) for(Object o:os)
			pst.setObject(++pstindex,o);
		for(int i=0;i<checkLength;i++)
			pst.setObject(++pstindex,checkObjects[i]);
		ResultSet rs=pst.executeQuery();
		List<Base[]> res=new ArrayList<Base[]>();
		int len=param.size();
		while(rs.next()){
			Base[] x=new Base[len];
			for(int i=0;i<len;i++){
				Class<? extends Base> c=param.getClassByIndex(i);
				String ct=Base.getSQLTableName(c);
				x[i]=c.newInstance();
				boolean flag=true;
				for(Field f:Base.getFields(c)){
					f.setAccessible(true);
					String columnName=ct+"_"+f.getName();
					Object o=null;
				try{o=rs.getObject(columnName);}catch(SQLException e){}
					if(flag && o!=null) flag=false;
					f.set(x[i],o);
				}
				//若x[i]的属性全部都是null，则x[i]应为null
				if(flag) x[i]=null;
				//若x[i]的key属性都是null，则x[i]应为null
				if(x[i]!=null&&x[i].checkKeyNull()) x[i]=null;
			}
			res.add(x);
		}
		return res;
	}
	
	
	
	
	
}