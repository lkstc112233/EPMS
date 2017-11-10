package obj;

import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unchecked")
public class Field implements Comparable<Field>{
	
	public final Class<? extends Base> clazz;
		public Class<? extends Base> getClazz(){return clazz;}
	public final java.lang.reflect.Field field;
	public final SQLField s;
	@Override
	public String toString(){
		return Base.getSQLTableName(this.getClazz())+"."+this.getName();
	}
	public String getSQLField(){
		return Base.getSQLTableName(this.getClazz())+"."+this.getName();
	}
	
	protected Field(Class<? extends Base> clazz,java.lang.reflect.Field field){
		this.clazz=clazz;
		this.field=field;
		this.s=this.field.getAnnotation(SQLField.class);
		if(s==null) throw new IllegalArgumentException("The Field is NOT the SQLField!");
	}
	static public Field getField(Class<? extends Base> clazz,String fieldName){
		for(Field f:Field.getFields(clazz)) if(f.getName().equals(fieldName))
			return f;
		return null;
	}
	
	static private Map<Class<? extends Base>,Field[]> MapFields=new HashMap<Class<? extends Base>,Field[]>();
	static public Set<Field> getFieldsSet(Class<? extends Base> clazz){
		Set<Field> res=new TreeSet<Field>();
		for(Field f:Field.getFields(clazz)) res.add(f);
		return res;
	}
	static public Field[] getFields(Class<? extends Base> clazz){
		if(Field.MapFields.containsKey(clazz)) return Field.MapFields.get(clazz);
		Set<Field> tmp=new TreeSet<Field>();
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(java.lang.reflect.Field f:c.getDeclaredFields()){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			tmp.add(new Field(clazz,f));
		}
		Field[] res=new Field[tmp.size()];
		int i=0;
		for(Field f:tmp) res[i++]=f;
		Field.MapFields.put(clazz,res);
		return res;
	}
	
	/**
	 * Override
	 */
	static public boolean nullValue(Object value){
		return value==null || String.valueOf(value).isEmpty();
	}
	public Object get(Base b) throws IllegalArgumentException{
		field.setAccessible(true);
		Object o=null;
		try{o=field.get(b);}catch(IllegalAccessException e){e.printStackTrace();}
		if(Field.nullValue(o))
			this.set(b,o=null);
		return o;
	}
	public Base set(Base b,Object value) throws IllegalArgumentException{
		field.setAccessible(true);
		if(Field.nullValue(value)) value=null;
		try{field.set(b,value);
		}catch(IllegalAccessException e){e.printStackTrace();}
		return b;
	}
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(!(o instanceof Field)) return false;
		Field f=(Field)o;
		return f.field.equals(this.field);
	}
	@Override
	public int compareTo(Field f){
		return Integer.compare(this.getWeigth(),f==null?Integer.MIN_VALUE:f.getWeigth());
	}
	/**
	 * SQLField
	 */
	public String	getName(){return field.getName();}
	public String	name(){return field.getName();}
	public boolean	getIsKey(){return s.isKey();}
	public boolean	isKey(){return s.isKey();}
	public int		getWeigth(){return s.weight();}
	public int		weigth(){return s.weight();}
	public String	getDescription(){return s.value();}
	public String	value(){return s.value();}
	public String	description(){return s.value();}
	public String	getPs(){return s.ps();}
	public String	ps(){return s.ps();}
	public boolean	getAutoInit(){return s.autoInit();}
	public boolean	autoInit(){return s.autoInit();}
	public boolean	getNotNull(){return s.notNull();}
	public boolean	notNull(){return s.notNull();}
	public Field	getSource(){return this.source();}
	public Field	source(){
		String[] ss=s.source().split("\\.");
		if(ss.length<=1) return null;
		return Field.getField(Base.getClassForName(ss[0]),ss[1]);
	}
	
	

	/**
	 * 获取该Field的source对应列表
	 */
	public List<? extends Base> getSourceList(Class<? extends Base> clazz){
		try{
			return Base.list(this.source().getClazz());
		}catch (IllegalArgumentException | InstantiationException | SQLException e){
			e.printStackTrace();
		}return null;
	}
	
	
	
	static public Integer s2i(String a){
		if(a==null||a.isEmpty()) return null;
		try{
			return Integer.valueOf(a);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}return null;
	}
	
	
}
