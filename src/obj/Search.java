package obj;

import java.lang.reflect.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import obj.staticSource.Major;
import obj.staticSource.School;
import persistence.DB;

public class Search<T extends Base> {
	
	/**
	 * 类信息，包括类Class<T>、域List<Field>、以及关于域的其他信息PsString、KeyBoolean、SourceListDescription
	 * SourceListDescription是一个Map<String,String>，其key是实际引用值，value是描述信息
	 */
	public class ClassInfo{
		public final Class<T> clazz;
			public String getTableName(){return Base.getSQLTableName(clazz);}
		private final List<Field> fields;
			public List<Field> getFields(){return this.fields;}
		private final List<String> allFieldsNameString;
			public List<String> getAllFieldsNameString(){return this.allFieldsNameString;}
		private final List<String> allFieldsDescriptionString;
			public List<String> getAllFieldsDescriptionString(){return this.allFieldsDescriptionString;}
		private final List<Boolean> allFieldsKeyBoolean;
			public List<Boolean> getAllFieldsKeyBoolean(){return this.allFieldsKeyBoolean;}
		private final List<Map<String,String>> allFieldsSourceListDescription;
			public List<Map<String,String>> getAllFieldsSourceListDescription(){return this.allFieldsSourceListDescription;}
		public ClassInfo(Class<T> clazz){
			this.clazz=clazz;
			this.fields=Base.getFields(clazz);
			this.allFieldsNameString=Base.getAllFieldsNameString(clazz);
			this.allFieldsDescriptionString=Base.getAllFieldsDescriptionString(clazz);
			this.allFieldsKeyBoolean=Base.getAllFieldsKeyBoolean(clazz);
			this.allFieldsSourceListDescription=ListableBase.getAllFieldsSourceListDescription(clazz);
		}
	}
	/**
	 * 查询限制条件
	 */
	static public interface SearchRestraint extends BaseRestraint{
		public abstract String getWhereString();
		public abstract List<Object> getWhereValues();
		public abstract String getOrderString();
	}
	/**
	 * 结果集，其实就是一个ArrayList<T>
	 */
	public class Result extends ArrayList<T>{
		private static final long serialVersionUID = -1043656163381266415L;	
	}
	
	
	
	//attributions
	private final ClassInfo classInfo;
		public ClassInfo getClassInfo(){return this.classInfo;}
	private SearchRestraint restraint=null;
		public void setRestraint(SearchRestraint r){this.restraint=r;}
		public SearchRestraint getRestraint(){return this.restraint;}
	private final Result result=new Result();
		public List<T> getResult(){return this.result;}
		
	
	public Search(Class<T> clazz){
		this(clazz,null);
	}
	public Search(Class<T> clazz,SearchRestraint restraint){
		this.classInfo=new ClassInfo(clazz);
		this.restraint=restraint;
	}
	
	
	public void execute() throws SQLException, InstantiationException, IllegalAccessException{
		this.result.clear();
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT ");
		boolean first=true;
		for(Field f:classInfo.fields){
			if(first) first=false;
			else sql.append(", ");
			sql.append(f.getName());
		}
		sql.append(" FROM ");
		sql.append(Base.getSQLTableName(classInfo.clazz));
		if(this.restraint!=null){
			sql.append(this.restraint.getWhereString());
			sql.append(this.restraint.getOrderString());
		}
		PreparedStatement pst=DB.con().prepareStatement(sql.toString());
		int parameterIndex=1;
		if(this.restraint!=null) for(Object v:this.restraint.getWhereValues())
			pst.setObject(parameterIndex++,v);
		ResultSet rs=pst.executeQuery();
		List<T> tmp=new ArrayList<T>();
		while(rs.next()){
			T instance=classInfo.clazz.newInstance();
			for(Field f:classInfo.fields){
				f.setAccessible(true);
				Object o=rs.getObject(f.getName());
				f.set(instance,o);
			}
			tmp.add(instance);
		}
		this.result.addAll(tmp);
	}
	
	
	
	
	
	//各类restraint
	static public class AllRestraint implements SearchRestraint{

		static public enum RestraintType{
			Smaller("<"),SmallerOrEqual("<="),
			Bigger(">"),BiggerOrEqual(">="),
			Equal("="),
			Like("LIKE");

			public final String operator;
			RestraintType(String oper){this.operator=oper;}
			public String getKey(){return this.toString();}
			public String getValue(){return this.operator;}
		}
		public RestraintType[] getRestraintTypeList(){return RestraintType.values();}
		
		static public class Triple{
			public Field field;
			public RestraintType type;
			public Object value;
			Triple(Field f,RestraintType t,Object s){field=f;type=t;value=s;}
			public Field getField(){return field;}
			public String getFieldName(){return field.getName();}
			public String getTypeName(){return String.valueOf(type);}	public void setTypeName(String s){this.type=RestraintType.valueOf(s);}
			public String getTypeOperation(){if(type==null) return "";return type.operator;}
			public RestraintType getType(){return this.type;}
			public void setType(RestraintType t){this.type=t;}
			public void setType(String s){this.type=s==null||s.isEmpty()?null:RestraintType.valueOf(s);}
			public Object getValue(){return this.value;}
			public void setValue(String s){
				this.value=s==null||s.isEmpty()?null:s;
			}
			public String toString(){
				return String.format("[Triple:%s(%s)%s]",this.field.getName(),this.getTypeName(),String.valueOf(this.getValue()));
			}
		}
		
		private Triple[] triple;
			public Triple[] getTriple(){return this.triple;}
		private String orderField;
			public void setOrderField(String a){this.orderField=a;}
			public String getOrderField(){return this.orderField;}
			
		public AllRestraint(@SuppressWarnings("rawtypes") Search.ClassInfo classinfo){
			this.triple=new Triple[classinfo.fields.size()];
			for(int i=0;i<this.triple.length;i++)
				triple[i]=new Triple((Field)classinfo.fields.get(i),null,null);
		}
			
		@Override
		public String getWhereString() {
			StringBuilder sql=new StringBuilder();
			boolean first=true;
			for(Triple t:this.triple){
				if(t.type==null||t.value==null)
					continue;
				if(first){
					first=false;
					sql.append(" WHERE ");
				}
				else sql.append(" AND ");
				sql.append(t.field.getName());
				sql.append(' ');
				sql.append(t.type.operator);
				sql.append(" ?");
			}
			return sql.toString();
		}

		@Override
		public List<Object> getWhereValues() {
			List<Object> res=new ArrayList<Object>();
			for(Triple t:this.triple){
				if(t.type==null||t.value==null)
					continue;
				res.add(t.value);
			}
			return res;
		}

		@Override
		public String getOrderString() {
			if(this.orderField==null||this.orderField.isEmpty()) return "";
			return " ORDER BY "+this.orderField;
		}
		@Override
		public boolean fitBase(Base b) {
			return true;
		}
		@Override
		public boolean fitAndSetBase(Base b){
			return true;
		}
		
	}
	static public class jwyRestraint implements SearchRestraint{
		private School school;
			public School getSchool(){return this.school;}
			public void setSchool(School a){this.school=a;}
		private int year;
			public int getYear(){return this.year;}
			public void setYear(int year){this.year=year;}
			public void setYear(String s){try{this.year=Integer.parseInt(s);}catch(NumberFormatException e){e.printStackTrace();};}
			
		private Field fieldYear=null,fieldSchool=null,fieldMajor=null;
		private List<Major> majors=null;
		
		public jwyRestraint(@SuppressWarnings("rawtypes") Search.ClassInfo classinfo,School school,int year) throws NoSuchFieldException, SQLException, IllegalArgumentException, IllegalAccessException{
			this.school=school;
			if(this.school!=null && !this.school.existAndLoad())
				this.school=null;
			this.year=year;
			for(Object o:classinfo.fields){
				Field f=(Field)o;
				if("year".equals(f.getName())){
					this.fieldYear=f;
					continue;
				}
				SQLField s=f.getAnnotation(SQLField.class);
				if(s==null||s.source()==null||s.source().isEmpty()) continue;
				String[] ss=s.source().split("\\.");
				if(ss[0].equals("School")){
					this.fieldSchool=f;
				}else if(ss[0].equals("Major")){
					this.fieldMajor=f;
				}
				if(this.fieldMajor!=null && this.fieldSchool!=null && this.fieldYear!=null)
					break;
			}
			if(this.school==null)
				this.fieldMajor=this.fieldSchool=null;
			if(this.fieldMajor!=null)
				this.majors=Major.list(Major.class,
						new String[]{"school"},
						new Object[]{this.school.getName()});
		}
			
		@Override
		public String getWhereString() {
			boolean first=true;
			StringBuilder sb=new StringBuilder();
			if(this.fieldMajor!=null){
				if(first){first=false;sb.append(" WHERE ");}
				else sb.append(" AND ");
				boolean g=true;
				for(@SuppressWarnings("unused") Major m:this.majors){
					if(g){g=false;sb.append(" ( ");}
					else sb.append(" OR ");
					sb.append("major = ?");
				}
			}
			if(this.fieldSchool!=null){
				if(first){first=false;sb.append(" WHERE ");}
				else sb.append(" AND ");
				sb.append("school = ?");
			}
			if(this.fieldYear!=null){
				if(first){first=false;sb.append(" WHERE ");}
				else sb.append(" AND ");
				sb.append("year = ?");
			}
			return sb.toString();
		}

		@Override
		public List<Object> getWhereValues() {
			List<Object> res=new ArrayList<Object>();
			if(this.fieldMajor!=null)
				for(Major m:this.majors)
					res.add(m.getName());
			if(this.fieldSchool!=null)
				res.add(school.getName());
			if(this.fieldYear!=null)
				res.add(year);
			return res;
		}

		@Override
		public String getOrderString() {
			return "";
		}
		private boolean fitBase(Base b,boolean set) {
			boolean res=true;
			if(this.fieldYear!=null){
				this.fieldYear.setAccessible(true);
				try {
					Object o=this.fieldYear.get(b);
					if(o!=null && o instanceof Integer){
						Integer y=(Integer)o;
						if(!y.equals(this.year))
							res=false;
					}
					if(set) this.fieldYear.set(b,this.year);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			if(this.fieldSchool!=null){
				this.fieldSchool.setAccessible(true);
				try {
					Object o=this.fieldSchool.get(b);
					if(o!=null){
						String y=String.valueOf(o);
						if(!this.school.getName().equals(y))
							res=false;
					}
					if(set) this.fieldSchool.set(b,this.school.getName());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			if(this.fieldMajor!=null){
				this.fieldMajor.setAccessible(true);
				try {
					Object o=this.fieldMajor.get(b);
					if(o!=null){
						String y=String.valueOf(o);
						boolean flag=false;
						for(Major m:this.majors){
							if(y.equals(m.getName())){flag=true;break;}
						}
						if(!flag) res=false;
					}
					if(set){
						Major m=this.majors.get(0);
						this.fieldMajor.set(b,m==null?null:m.getName());
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return res;
		}
		@Override
		public boolean fitBase(Base b){
			return this.fitBase(b,false);
		}
		@Override
		public boolean fitAndSetBase(Base b){
			return this.fitBase(b,true);
		}
	}
	
}
