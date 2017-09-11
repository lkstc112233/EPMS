package obj;
import java.lang.reflect.*;

import java.sql.*;
import java.util.*;

import persistence.DB;

@SuppressWarnings("unchecked")
public class SQLCollection {
	static public final SQLIO io=new POI();
	
	
	static public <T extends Base> T cast(Class<T> clazz,Object obj) throws ClassCastException{
		if(obj==null) return null;
		return clazz.cast(obj);
	}
	
	static public <T extends Base>
	List<T> selectAll(Class<T> clazz,Field[] checkFields,Object[] checkObjects) throws SQLException{
		return selectAll(clazz,checkFields,checkObjects,-1);
	}
	static public <T extends Base>
	List<T> selectAll(Class<T> clazz,Field[] checkFields,Object[] checkObjects,int limitNumber) throws SQLException{
		List<T> res=new ArrayList<T>();
		StringBuilder sql_select=new StringBuilder();
		StringBuilder sql_sorted=new StringBuilder();
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			f.setAccessible(true);
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			if(sql_select.length()>0)
				sql_select.append(',');
			sql_select.append(f.getName());
			if(s.needSorted()){
				if(sql_sorted.length()>0)
					sql_sorted.append(',');
				sql_sorted.append(f.getName());
			}
		}
		StringBuilder sql_where=new StringBuilder();
		int cnt=0;
		if(checkFields!=null && checkFields.length>0){
			for(Field f:checkFields){
				if(f==null || f.getDeclaringClass()!=clazz)
					continue;
				f.setAccessible(true);
				SQLField s=f.getAnnotation(SQLField.class);
				if(s==null) continue;
				if(sql_where.length()>0)
					sql_where.append(" AND ");
				sql_where.append(f.getName());
				sql_where.append(" = ?");
				if(++cnt>checkObjects.length)
					throw new IndexOutOfBoundsException("selectALl(checkFieds' size is bigger than checkObjects' size)!");
			}
		}
		PreparedStatement sql_ps=DB.con().prepareStatement(
				"SELECT "+sql_select.toString()
				+" FROM "+Base.getSQLTableName(clazz)
				+(sql_where.length()<=0?"":(" WHERE "+sql_where.toString()))
				+(limitNumber<0?"":("LIMIT "+limitNumber))
				+(sql_sorted.length()<=0?"":(" ORDER BY "+sql_sorted.toString()))
				);
		if(checkFields!=null && checkFields.length>0){
			int SQLParameterIndex=1;
			for(Field f:checkFields){
				if(f==null || f.getDeclaringClass()!=clazz)
					continue;
				f.setAccessible(true);
				SQLField s=f.getAnnotation(SQLField.class);
				if(s==null) continue;
				sql_ps.setObject(SQLParameterIndex,checkObjects[SQLParameterIndex]);
				SQLParameterIndex++;
			}
		}
		ResultSet rs=sql_ps.executeQuery();
		while(rs.next()){
			T t=null;
			try {
				t=clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
			boolean ok=true;
			for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
				f.setAccessible(true);
				SQLField s=f.getAnnotation(SQLField.class);
				if(s==null) continue;
				Object o=rs.getObject(f.getName());
				try {
					try{
						f.set(t,f.getType().cast(o));
					}catch(ClassCastException e){
						f.set(t,o);
					}
				} catch (ClassCastException |IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					ok=false;
					break;
				}
			}
			if(!ok)
				continue;
			res.add(t);
		}
		return res;
	}
	
	/*
	 * 关于壳的操作，壳是Base包装，内容是String[]
	 *
	/**
	 * 脱壳操作，对单一String类型的key值(isKey==true)
	 *
	static public <T extends Base>
	List<String> getOutOne(List<T> list){
		List<String> res=new ArrayList<String>();
		if(list==null || list.isEmpty())
			return res;
		Field out=null;
		for(Class<? extends Base> c=list.get(0).getClass();c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			f.setAccessible(true);
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			if(s.isKey()){
				if(out==null){
					out=f;
					if(out.getType()==String.class)
						break;
				}else if(f.getType()==String.class){
					out=f;
					break;
				}
			}
		}
		if(out==null) return res;
		out.setAccessible(true);
		for(T t:list){
			try {
				Object o=out.get(t);
				res.add(o==null?"":o.toString());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
		}
		return res;
	}
	/**
	 * 脱壳操作，对所有SQLFeild值
	 *
	static public <T extends Base>
	List<String[]> getOutAll(List<T> list,String[] labels){
		List<String[]> res=new ArrayList<String[]>();
		if(list==null || list.isEmpty())
			return res;
		List<Field> flist=new ArrayList<Field>();
		for(Class<? extends Base> c=list.get(0).getClass();c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			f.setAccessible(true);
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			flist.add(f);
		}
		labels=new String[flist.size()];
		for(int i=0;i<flist.size();i++)
			labels[i]=flist.get(i).getName();
		for(T t:list){
			String[] one=new String[labels.length];
			try {
				for(int i=0;i<flist.size();i++){
					Object o=flist.get(i).get(t);
					one[i]=o==null?"":o.toString();
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
			res.add(one);
		}
		return res;
	}

	/**
	 * 脱壳操作，对一个对象所有SQLFeild值
	 *
	static public <T extends Base>
	String[] getOutAll(T t,String[] labels){
		String[] res=null;
		if(t==null)
			return res;
		List<Field> flist=new ArrayList<Field>();
		for(Class<? extends Base> c=t.getClass();c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			f.setAccessible(true);
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			flist.add(f);
		}
		labels=new String[flist.size()];
		for(int i=0;i<flist.size();i++)
			labels[i]=flist.get(i).getName();
		res=new String[labels.length];
		try {
			for(int i=0;i<flist.size();i++){
				Object o=flist.get(i).get(t);
				res[i]=o==null?"":o.toString();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}
	/**
	 * 加壳操作，把一个String[]和clazz对应起来
	 *
	static public <T extends Base>
	T getIn(Class<T> clazz,String[] obj,String[] labels) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException{
		T t=clazz.newInstance();
		for(int i=0;i<labels.length;i++){
			Field f=Base.getField(clazz,labels[i]);
			f.setAccessible(true);
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			try {
				f.set(t,obj[i]);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return t;
	}
	/**
	 * 加壳操作，把多个String[]和clazz对应起来
	 *
	static public <T extends Base>
	List<T> getIn(Class<T> clazz,List<String[]> objs,String[] labels) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException{
		List<T> res=new ArrayList<T>();
		Field[] fs=new Field[labels.length];
		for(int i=0;i<labels.length;i++){
			fs[i]=Base.getField(clazz,labels[i]);
			fs[i].setAccessible(true);
		}
		for(String[] obj:objs){
			T t=clazz.newInstance();
			for(int i=0;i<fs.length;i++){
				SQLField s=fs[i].getAnnotation(SQLField.class);
				if(s==null) continue;
				try {
					fs[i].set(t,obj[i]);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			res.add(t);
		}
		return res;
	}
	//*/
	
	
}
