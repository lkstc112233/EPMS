package obj;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import persistence.DB;

@SuppressWarnings("unchecked")
public abstract class Base {
	static public final String packageNames[]={"staticSource","staticObject"};
	static public Class<? extends Base> getClassForName(String name){
		if(name==null||name.length()<=0)
			return null;
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
	
	static private Map<Class<? extends Base>,String> SQL_table=new HashMap<Class<? extends Base>,String>();
	static private Map<Class<? extends Base>,PreparedStatement> SQL_load=new HashMap<Class<? extends Base>,PreparedStatement>();
	static private Map<Class<? extends Base>,PreparedStatement> SQL_update=new HashMap<Class<? extends Base>,PreparedStatement>();
	static private Map<Class<? extends Base>,PreparedStatement> SQL_delete=new HashMap<Class<? extends Base>,PreparedStatement>();
	static private Map<Class<? extends Base>,PreparedStatement> SQL_insert=new HashMap<Class<? extends Base>,PreparedStatement>();
	
	private final String sql_table;				public String getSQLTabelName(){return this.sql_table;}
	private final PreparedStatement sql_load;
	private final PreparedStatement sql_update;
	private final PreparedStatement sql_delete;
	private final PreparedStatement sql_insert;
	
	protected Base() throws SQLException{
		Class<? extends Base> clazz=this.getClass();
		String _sql_table=SQL_table.get(clazz);
		PreparedStatement _sql_load=SQL_load.get(clazz);
		PreparedStatement _sql_update=SQL_update.get(clazz);
		PreparedStatement _sql_delete=SQL_delete.get(clazz);
		PreparedStatement _sql_insert=SQL_insert.get(clazz);
		if(_sql_table==null ||
				_sql_load==null ||
				_sql_update==null ||
				_sql_delete==null ||
				_sql_insert==null){
			Base.initialize(clazz);
			_sql_table=SQL_table.get(clazz);
			_sql_load=SQL_load.get(clazz);
			_sql_update=SQL_update.get(clazz);
			_sql_delete=SQL_delete.get(clazz);
			_sql_insert=SQL_insert.get(clazz);
		}
		this.sql_table=_sql_table;
		this.sql_load=_sql_load;
		this.sql_update=_sql_update;
		this.sql_delete=_sql_delete;
		this.sql_insert=_sql_insert;
	}
	
	static private void initialize(Class<? extends Base> clazz) throws SQLException{
		if(SQL_table.containsKey(clazz) &&
				SQL_load.containsKey(clazz) &&
				SQL_update.containsKey(clazz) &&
				SQL_delete.containsKey(clazz) &&
				SQL_insert.containsKey(clazz))
			return;
		String sql_table=clazz.getAnnotation(SQLTable.class).value();
		PreparedStatement sql_load;
		PreparedStatement sql_update;
		PreparedStatement sql_delete;
		PreparedStatement sql_insert;
		StringBuilder load_select=new StringBuilder();
		StringBuilder update_set=new StringBuilder();
		StringBuilder sql_where=new StringBuilder();
		StringBuilder insert=new StringBuilder();
		StringBuilder insert_values=new StringBuilder();
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			f.setAccessible(true);
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			if(s.isKey()){
				if(sql_where.length()>0)
					sql_where.append(" AND ");
				sql_where.append(f.getName());
				sql_where.append(" = ? ");
			}else{
				if(load_select.length()>0)
					load_select.append(',');
				load_select.append(f.getName());
				if(update_set.length()>0)
					update_set.append(',');
				update_set.append(f.getName());
				update_set.append(" = ? ");
			}
			if(insert.length()>0)
				insert.append(',');
			insert.append(f.getName());
			if(insert_values.length()>0)
				insert_values.append(',');
			insert_values.append('?');
		}
		sql_load=DB.con().prepareStatement(
				"SELECT "+load_select.toString()
				+" FROM "+sql_table
				+" WHERE "+sql_where.toString());
		sql_update=DB.con().prepareStatement(
				"UPDATE "+sql_table
				+" SET "+update_set.toString()
				+" WHERE "+sql_where.toString());
		sql_delete=DB.con().prepareStatement(
				"DELETE FROM "+sql_table
				+" WHERE "+sql_where.toString());
		sql_insert=DB.con().prepareStatement(
				"INSERT INTO  "+sql_table
				+" ("+insert.toString()
				+") VALUES ("+insert_values.toString()
				+")");
		SQL_table.put(clazz,sql_table);
		SQL_load.put(clazz,sql_load);
		SQL_update.put(clazz,sql_update);
		SQL_delete.put(clazz,sql_delete);
		SQL_insert.put(clazz,sql_insert);
	}
	
	
	static public Field getField(Class<? extends Base> clazz,String fieldName) throws NoSuchFieldException{
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()){
			try{
				return c.getDeclaredField(fieldName);
			}catch(NoSuchFieldException|SecurityException e){
			}
		}
		throw new NoSuchFieldException(fieldName);
	}
	static public Field hasAvailable(Class<? extends Base> clazz){
		try {
			return getField(clazz,"available");
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}
	public Field hasAvailable(){
		return Base.hasAvailable(this.getClass());
	}
	
	@Override
	public String toString(){
		Class<? extends Base> clazz=this.getClass();
		String sql_table=this.sql_table;
		StringBuilder sb=new StringBuilder();
		sb.append('[');
		sb.append(clazz.getSimpleName());
		sb.append('(');
		sb.append(sql_table);
		sb.append(')');
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			sb.append(',');
			if(s.isKey()) sb.append('*');
			sb.append(f.getName());
			sb.append('(');
			try {
				sb.append(f.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				sb.append("?");
			}
			sb.append(')');
		}
		sb.append(']');
		return sb.toString();
	}
	
	public boolean checkKeyNull() throws IllegalArgumentException, IllegalAccessException{
		Class<? extends Base> clazz=this.getClass();
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			if(s.isKey()){
				if(f.get(this)==null)
					return true;
			}
		}
		return false;
	}

	public void load() throws SQLException, IllegalArgumentException, IllegalAccessException{
		Class<? extends Base> clazz=this.getClass();
		int SQLParameterIndex=1;
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			if(s.isKey()){
				//if(f.getType().isAssignableFrom(Number.class))
					this.sql_load.setObject(SQLParameterIndex++,f.get(this));
			}
		}
		ResultSet rs=sql_load.executeQuery();
		rs.last();
		int num=rs.getRow();
		if(num!=1)
			System.err.println("查询到"+num+"重值！("+this.sql_load.toString()+")");
		rs.first();
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			if(!s.isKey())
				f.set(this,rs.getObject(f.getName()));
		}
	}
	public void update()throws SQLException, IllegalArgumentException, IllegalAccessException{
		Class<? extends Base> clazz=this.getClass();
		int SQLParameterIndex=1;
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			if(!s.isKey())
				this.sql_update.setObject(SQLParameterIndex++,f.get(this));
		}
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			if(s.isKey())
				this.sql_update.setObject(SQLParameterIndex++,f.get(this));
		}
		int num=this.sql_update.executeUpdate();
		if(num!=1)
			System.err.println("更新了"+num+"重值！("+this.sql_update.toString()+")");
	}
	public void update(Field[] updateFields)throws SQLException, IllegalArgumentException, IllegalAccessException{
		Class<? extends Base> clazz=this.getClass();
		StringBuilder sql_set=new StringBuilder();
		StringBuilder sql_where=new StringBuilder();
		for(Field f:updateFields){
			if(f==null || f.getDeclaringClass()!=clazz)
				continue;
			f.setAccessible(true);
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			if(sql_set.length()>0)
				sql_set.append(',');
			sql_set.append(f.getName());
			sql_set.append(" = ?");
		}
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			if(s.isKey()){
				if(sql_where.length()>0)
					sql_where.append(" AND ");
				sql_where.append(f.getName());
				sql_where.append(" = ? ");
			}
		}
		PreparedStatement sqlps=DB.con().prepareStatement(
				"UPDATE "+this.getSQLTabelName()
				+" SET "+sql_set.toString()
				+" WHERE "+sql_where.toString());
		int SQLParameterIndex=1;
		for(Field f:updateFields){
			if(f==null || f.getDeclaringClass()!=clazz)
				continue;
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			sqlps.setObject(SQLParameterIndex++,f.get(this));
		}
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			if(s.isKey())
				sqlps.setObject(SQLParameterIndex++,f.get(this));
		}
		int num=sqlps.executeUpdate();
		if(num!=1)
			System.err.println("更新了"+num+"重值！("+sqlps.toString()+")");
	}
	public void delete() throws IllegalArgumentException, IllegalAccessException, SQLException{
		Field available=this.hasAvailable();
		if(available==null){
			//delete it
			Class<? extends Base> clazz=this.getClass();
			int SQLParameterIndex=1;
			for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
				SQLField s=f.getAnnotation(SQLField.class);
				if(s==null) continue;
				f.setAccessible(true);
				if(s.isKey()){
					this.sql_load.setObject(SQLParameterIndex++,f.get(this));
				}
			}
			int num=sql_delete.executeUpdate();
			if(num!=1)
				System.err.println("删除了"+num+"重值！("+sql_delete.toString()+")");
		}else{
			//UPDATE available = false
			available.setAccessible(true);
			available.set(this,false);
			this.update(new Field[]{available});
		}
	}
	public void create()throws SQLException, IllegalArgumentException, IllegalAccessException{
		Class<? extends Base> clazz=this.getClass();
		int SQLParameterIndex=1;
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			f.setAccessible(true);
			this.sql_insert.setObject(SQLParameterIndex++,f.get(this));
		}
		int num=this.sql_insert.executeUpdate();
		if(num!=1)
			System.err.println("更新了"+num+"重值！("+this.sql_insert.toString()+")");
	}
}
