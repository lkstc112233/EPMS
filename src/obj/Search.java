package obj;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import persistence.DB;

public final class Search<T extends Base>{
	static public enum RestraintType{
		Smaller("<"),SmallerOrEqual("<="),
		Bigger(">"),BiggerOrEqual(">="),
		Equal("="),
		Like("LIKE");

		static public List<String> list(){
			List<String> res=new ArrayList<String>();
			for(RestraintType r:RestraintType.values()) res.add(r.toString());
			return res;
		}
		public final String operator;
		RestraintType(String oper){this.operator=oper;}
		public String getKey(){return this.toString();}
		public String getValue(){return this.operator;}
	}
	
	static public class Triple{
		public Field field;
		public RestraintType type;
		public String value;
		Triple(Field f,RestraintType t,String s){field=f;type=t;value=s;}
		public String getFieldName(){return this.field.getName();}
		public String getTypeName(){return String.valueOf(type);}	public void setTypeName(String s){this.type=RestraintType.valueOf(s);}
		public String getTypeOperation(){if(type==null) return "";return type.operator;}
		public RestraintType getType(){return this.type;}
		public void setType(RestraintType t){
			this.type=t;
		}
		public String getValue(){return this.value;}	public void setValue(String s){this.value=s;}
		public String toString(){
			return String.format("[Triple:%s(%s)%s]",this.getFieldName(),this.getTypeName(),this.getValue());
		}
	}
	
	private List<T> resultSet=new ArrayList<T>();
	private Triple[] restraint;
	
	//没有resultSet的setter函数
	public List<T> getResultSet(){return this.resultSet;}
	public Triple[] getRestraint(){return this.restraint;}
	//没有types的setter函数
	public RestraintType[] getRestraintTypeList(){return RestraintType.values();}
	
	
	public final Class<T> clazz;
		public String getTableName(){return this.clazz.getSimpleName();}
	
	public Search(Class<T> clazz) throws InstantiationException, IllegalAccessException{
		this.clazz=clazz;
		List<Field> fields=Base.getFields(clazz);
		this.restraint=new Triple[fields.size()];
		for(int i=0;i<this.restraint.length;i++)
			this.restraint[i]=new Triple(fields.get(i),null,null);
	} 
		
	/**
	 * 执行查询
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws InstantiationException 
	 */
	public List<T> execute() throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException{
		this.resultSet.clear();
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT ");
		boolean flag=true;
		for(Triple t:this.restraint){
			if(flag) flag=false;
			else sql.append(", ");
			sql.append(t.field.getName());
		}
		sql.append(" FROM ");
		sql.append(Base.getSQLTableName(clazz));
		flag=true;
		List<String> restraints=new ArrayList<String>();
		for(Triple t:this.restraint){
	//		System.out.println(t);
			if(t.type==null||t.value==null||t.value.isEmpty())
				continue;
			if(flag){
				flag=false;
				sql.append(" WHERE ");
			}
			else sql.append(" AND ");
			sql.append(t.field.getName());
			sql.append(' ');
			sql.append(t.type.operator);
			sql.append(" ?");
			restraints.add(t.value);
			System.out.println(t);
		}
		PreparedStatement pst=DB.con().prepareStatement(sql.toString());
		for(int i=0;i<restraints.size();i++)
			pst.setObject(i+1,restraints.get(i));
		System.out.println(">> Seacher:execute > SQL: "+pst.toString());
		ResultSet rs=pst.executeQuery();
		while(rs.next()){
			T instance=clazz.newInstance();
			for(Triple t:this.restraint){
				t.field.setAccessible(true);
				Object o=rs.getObject(t.field.getName());
				t.field.set(instance,o);
			}
			this.resultSet.add(instance);
		}
		return this.resultSet;
	}
	
	
	
	
	
	
	
}