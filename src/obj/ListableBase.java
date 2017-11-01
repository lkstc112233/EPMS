package obj;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

import obj.ListableBase.JoinParam.JoinParamPart;
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
	
	
	static protected List<Map<String,String>> getAllFieldsSourceListDescription(Class<? extends Base> clazz){
		List<Map<String,String>> res=new ArrayList<Map<String,String>>();
		for(Field f:Base.getFields(clazz)){
			String s=f.getAnnotation(SQLField.class).source();
			if(s!=null){
				String[] ss=s.split("\\.");
				Class<? extends Base> sourceClass=Base.getClassForName(ss[0]);
				if(sourceClass!=null && ListableBase.class.isAssignableFrom(sourceClass)){
					try {
						Method m=sourceClass.getMethod("list",sourceClass.getClass());
						Field field = Base.getField(sourceClass,ss[1]);
						@SuppressWarnings("unchecked")
						List<? extends Base> list=(List<? extends Base>) m.invoke(null,sourceClass);
						Map<String,String> map=new TreeMap<String,String>();
						field.setAccessible(true);
						for(Base b:list){
							Object o=field.get(b);
							String key=o==null?null:o.toString();
							String value=b.getSimpleToString();
							if(value==null)
								value=key;
							map.put(key,value);
						}
						res.add(map);
						continue;
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
			}
			res.add(null);
		}
		return res;
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
/*	@Override
	public void update(Field[] updateFields) throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update(updateFields);
		Class<? extends ListableBase> clazz=this.getClass();
		ListableBase.StaticList.remove(clazz);
	}*/
	@Override
	public void update(Base b) throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update(b);
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
		static public class JoinParamPart{
			private JoinType type;						public JoinType getType(){return type;}
			private Class<? extends Base> clazz;		public Class<? extends Base> getClazz(){return clazz;}
			private String lastOnFieldName;				public String getLastOnFieldName(){return this.lastOnFieldName;}
			private String thisOnFieldName;				public String getThisOnFieldName(){return this.thisOnFieldName;}
			private String[] onCheckFieldNames;			public String[] getOnCheckFieldNames(){return this.onCheckFieldNames;}
			private Object[] onCheckFieldValues;		public Object[] getOnCheckFieldValues(){return this.onCheckFieldValues;}
			public JoinParamPart(String field1Name,JoinType jp,Class<? extends Base> c,String field2Name,
					String[] onCheckFieldNames,Object[] onCheckFieldValues){
				this.type=jp;this.clazz=c;this.lastOnFieldName=field1Name;this.thisOnFieldName=field2Name;
				this.onCheckFieldNames=onCheckFieldNames==null?new String[0]:onCheckFieldNames;
				this.onCheckFieldValues=onCheckFieldValues==null?new Object[0]:onCheckFieldValues;
			}
			public String toString(Class<? extends Base> lastClazz){
				StringBuilder sb=new StringBuilder();
				sb.append(this.type.toString());
				sb.append(' ');
				Class<? extends Base> c1=lastClazz;
				Class<? extends Base> c2=this.clazz;
				String c1table=Base.getSQLTableName(c1);
				String c2table=Base.getSQLTableName(c2);
				sb.append(c2table);
				sb.append(" ON ( ");
				sb.append(c1table);sb.append('.');sb.append(this.lastOnFieldName);
				sb.append(" = ");
				sb.append(c2table);sb.append('.');sb.append(this.thisOnFieldName);
				for(String f:this.onCheckFieldNames){
					sb.append(" AND ");
					sb.append(c2table);sb.append('.');sb.append(f);
					sb.append(" = ?");
				}
				sb.append(" )");
				return sb.toString();
			}
		}
		private Class<? extends ListableBase> clazz;
		private List<JoinParamPart> list=new ArrayList<JoinParamPart>();
		public Class<? extends ListableBase> getClazz(){return clazz;}
		public List<JoinParamPart> getList(){return list;}
		
		public JoinParam(Class<? extends ListableBase> clazz){
			this.clazz=clazz;
		}
		public Class<? extends Base> getClassByIndex(int index){
			return this.list.get(index).getClazz();
		}
		public int size(){return this.list.size()+1;}
		public void append(String field1Name,JoinType jp,Class<? extends Base> c,String field2Name) throws NoSuchFieldException{
			this.append(field1Name,jp,c,field2Name,null,null);
		}
		public void append(String field1Name,JoinType jp,Class<? extends Base> c,String field2Name,
				String[] oncheckFieldNames,Object[] oncheckFieldValues) throws NoSuchFieldException{
			this.append(new JoinParamPart(field1Name,jp,c,field2Name,oncheckFieldNames,oncheckFieldValues));
		}
		public void append(JoinParamPart part){
			if(part!=null) this.list.add(part);
		}
		@Override
		public String toString(){
			StringBuilder sb=new StringBuilder();
			sb.append(Base.getSQLTableName(this.clazz));
			Class<? extends Base> lastClazz=this.clazz;
			for(JoinParamPart part:this.list){
				sb.append(' ');
				sb.append(part.toString(lastClazz));
				lastClazz=part.getClazz();
			}
			return sb.toString();
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
		for(JoinParamPart part:param.getList()){
			String _ct=Base.getSQLTableName(part.getClazz());
			for(Field f:Base.getFields(part.getClazz())){
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
		for(JoinParamPart part:param.getList())
			for(Object o:part.getOnCheckFieldValues())
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