package obj;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@SuppressWarnings("unchecked")
public class Field implements Comparable<Field>{
	
	static public String orderIdFieldName="orderId";
	
	public final Class<? extends Base> clazz;
		public Class<? extends Base> getClazz(){return clazz;}
	public final java.lang.reflect.Field field;
	public final SQLField s;
	@Override
	public String toString(){
		return this.getClazz().getSimpleName()+"."+this.getName();
	}
	public String getSQLField(String dl){
		if(dl==null) dl="";
		return Base.getSQLTableName(this.getClazz())+dl+this.getName();
	}
	public String getAllName(){
		return this.getSQLField(".");
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
	static public Field[] getFields(Class<? extends Base> clazz,String fieldName,String... fieldNames){
		Field[] fs=new Field[fieldNames.length+1];
		for(int i=0;i<fs.length;i++)
			fs[i]=i==0?Field.getField(clazz,fieldName):Field.getField(clazz,fieldNames[i-1]);
		return fs;
	}
	
	static private Map<Class<? extends Base>,Field[]> MapFields=new HashMap<Class<? extends Base>,Field[]>();
	static public Set<Field> getFieldsSet(Class<? extends Base> clazz){
		Set<Field> res=new TreeSet<Field>();
		for(Field f:Field.getFields(clazz)) res.add(f);
		return res;
	}
	static public Field[] getFields(Class<? extends Base> clazz){
		if(Field.MapFields.containsKey(clazz))
			return Field.MapFields.get(clazz);
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
	public Base setBySetter(Base b,Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		field.setAccessible(true);
		if(Field.nullValue(value)) value=null;
		String methodName="set"+this.getName().substring(0,1).toUpperCase()+this.getName().substring(1);
		java.lang.reflect.Method m=null;
		boolean string=false;
		try {
			m=b.getClass().getMethod(methodName,value.getClass());
		} catch (NoSuchMethodException | SecurityException | NullPointerException e) {
			try{
				m=b.getClass().getMethod(methodName,String.class);
				string=true;
			} catch (NoSuchMethodException | SecurityException | NullPointerException ee) {
				try{
					m=b.getClass().getMethod(methodName,Object.class);
				} catch (NoSuchMethodException | SecurityException eee) {
				}
			}
		}if(m==null) throw new NoSuchMethodException("Cannot find the method with name\""+methodName+"\"");
		m.invoke(b,(string&&value!=null)?String.valueOf(value):value);
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
		int cmp=Integer.compare(this.getWeigth(),f==null?Integer.MIN_VALUE:f.getWeigth());
		if(cmp!=0) return cmp;
		return Integer.compare(this.field.hashCode(),f.field.hashCode());
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
	static public class Pair{
		String key,value;
		public Pair(Object a,String b){key=a==null?null:String.valueOf(a);value=b;}
		public String getKey(){return key;}
		public String getValue(){return value;}
	}
	static private Map<Field,List<Pair>> MapSourceList=new HashMap<Field,List<Pair>>();
	public List<Pair> getSourceList(){
		if(MapSourceList.containsKey(this)) return MapSourceList.get(this);
		Field sourceField=this.source();
		if(sourceField==null) return null;
		List<Pair> res=new ArrayList<Pair>();
		for(Base b:this.sourceList())
			res.add(new Pair(sourceField.get(b),b.getDescription()));
		MapSourceList.put(this,res);
		return res;
	}
	public List<? extends Base> sourceList(){
		Field sourceField=this.source();
		if(sourceField==null) return null;
		try{
			return Base.list(sourceField.getClazz());
		}catch (IllegalArgumentException | InstantiationException | SQLException e){
			e.printStackTrace();
		}return null;
	}
	

	static public String o2s(Object a,String defaultResult,String boolTrue,String boolFalse){
		if(a==null) return defaultResult;
		if(a instanceof Boolean) return ((Boolean)a)?boolTrue:boolFalse;
		return Field.o2S(a);
	}
	static public String o2S(Object a){
		return Field.s2S(a==null?null:a.toString());
	}
	static public String s2S(String a){
		return a==null||a.isEmpty()?null:a;
	}
	static public int s2i(String a,int defaultResult){
		Integer res=s2I(a);
		return res==null?defaultResult:res;
	}
	static public Integer s2I(String a){
		if(a==null||a.isEmpty()) return null;
		try{
			return Integer.valueOf(a);
		}catch(Exception e){
			e.printStackTrace();
		}return null;
	}
	static public boolean s2b(String a,boolean defaultResult){
		Boolean res=s2B(a);
		return res==null?defaultResult:res;
	}
	static public Boolean s2B(String a){
		if(a==null||a.isEmpty()) return null;
		try{
			return Boolean.valueOf(a);
		}catch(Exception e){
			e.printStackTrace();
		}return null;
	}
	static public Timestamp s2TS(String a){
		if(a==null||a.isEmpty()) return null;
		try{
			return Timestamp.valueOf(a);
		}catch(Exception e){
			e.printStackTrace();
		}return null;
	}
	
}
