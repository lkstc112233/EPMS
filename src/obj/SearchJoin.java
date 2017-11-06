package obj;

import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.*;

import obj.ListableBase.JoinParam;
import obj.ListableBase.JoinParam.JoinParamPart;

public class SearchJoin {
	
	/**
	 * 类信息，包括类Class<T>、域List<Field>、以及关于域的其他信息PsString、KeyBoolean、SourceListDescription
	 * SourceListDescription是一个Map<String,String>，其key是实际引用值，value是描述信息
	 */
	public class ClassInfo{
		public final JoinParam param;
		private List<List<Field>> fields;
			public List<List<Field>> getFields(){return this.fields;}
		private final List<List<String>> allFieldsNameString;
			public List<List<String>> getAllFieldsNameString(){return this.allFieldsNameString;}
		private final List<List<String>> allFieldsDescriptionString;
			public List<List<String>> getAllFieldsDescriptionString(){return this.allFieldsDescriptionString;}
		private final List<List<Boolean>> allFieldsKeyBoolean;
			public List<List<Boolean>> getAllFieldsKeyBoolean(){return this.allFieldsKeyBoolean;}
		private final List<List<Map<String,String>>> allFieldsSourceListDescription;
			public List<List<Map<String,String>>> getAllFieldsSourceListDescription(){return this.allFieldsSourceListDescription;}
		public ClassInfo(JoinParam param){
			this.param=param;
			this.fields=new ArrayList<List<Field>>();
			this.allFieldsNameString=new ArrayList<List<String>>();
			this.allFieldsDescriptionString=new ArrayList<List<String>>();
			this.allFieldsKeyBoolean=new ArrayList<List<Boolean>>();
			this.allFieldsSourceListDescription=new ArrayList<List<Map<String,String>>>();
			for(JoinParamPart part:this.param.getList()){
				Class<? extends Base> clazz=part.getClazz();
				this.allFieldsNameString.add(Base.getAllFieldsNameString(clazz));
				this.allFieldsDescriptionString.add(Base.getAllFieldsDescriptionString(clazz));
				this.allFieldsKeyBoolean.add(Base.getAllFieldsKeyBoolean(clazz));
				this.allFieldsSourceListDescription.add(ListableBase.getAllFieldsSourceListDescription(clazz));
			}
		}
	}
	/**
	 * 查询限制条件
	 */
	static public interface SearchRestraint extends BaseRestraint{
		public abstract String[] getWhereFields();
		public abstract Object[] getWhereValues();
		public abstract String[] getOrderFields();
	}
	/**
	 * 结果集，其实就是一个ArrayList<T>
	 */
	public class Result extends ArrayList<Base[]>{
		private static final long serialVersionUID = -1043656163381266415L;	
	}
	
	
	
	//attributions
	private final ClassInfo classInfo;
		public ClassInfo getClassInfo(){return this.classInfo;}
	private SearchRestraint restraint=null;
		public void setRestraint(SearchRestraint r){this.restraint=r;}
		public SearchRestraint getRestraint(){return this.restraint;}
	private final Result result=new Result();
		public List<Base[]> getResult(){return this.result;}
		
	
	public SearchJoin(JoinParam param){
		this(param,null);
	}
	public SearchJoin(JoinParam param,SearchRestraint restraint){
		this.classInfo=new ClassInfo(param);
		this.restraint=restraint;
	}
	
	
	public void execute() throws SQLException, InstantiationException, IllegalAccessException{
		this.result.clear();
		List<Base[]> list=ListableBase.list(this.getClassInfo().param,
				this.restraint==null?null:this.restraint.getWhereFields(),
						this.restraint==null?null:this.restraint.getWhereValues(),
								this.restraint==null?null:this.restraint.getOrderFields());
		this.result.addAll(list);
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
		
		static public class AllRestraintPart{
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
			private Class<? extends Base> clazz;
				public String getTableName(){return Base.getSQLTableName(clazz);}
			private Triple[] triple;
				public Triple[] getTriple(){return this.triple;}
			private String orderField;
				public void setOrderField(String a){this.orderField=a;}
				public String getOrderField(){return this.orderField;}
				
			public AllRestraintPart(Class<? extends Base> clazz,List<Field> fields){
				this.clazz=clazz;
				this.triple=new Triple[fields.size()];
				for(int i=0;i<this.triple.length;i++)
					triple[i]=new Triple(fields.get(i),null,null);
			}
		}
		
		private AllRestraintPart[] parts;
			public AllRestraintPart[] getParts(){return this.parts;}
			
		public AllRestraint(SearchJoin.ClassInfo classInfo){
			this.parts=new AllRestraintPart[classInfo.param.size()];
			for(int i=0;i<this.parts.length;i++)
				parts[i]=new AllRestraintPart(classInfo.param.getClassByIndex(i),classInfo.getFields().get(i));
		}


		@Override
		public String[] getWhereFields() {
			List<String> res=new ArrayList<String>();
			for(AllRestraintPart part:this.parts)
				for(AllRestraintPart.Triple t:part.triple)
					res.add(t.getFieldName());
			String[] ans=new String[res.size()];
			for(int i=0;i<ans.length;i++) ans[i]=res.get(i);
			return ans;
		}

		@Override
		public Object[] getWhereValues() {
			List<Object> res=new ArrayList<Object>();
			for(AllRestraintPart part:this.parts)
				for(AllRestraintPart.Triple t:part.triple)
					res.add(t.getValue());
			Object[] ans=new Object[res.size()];
			for(int i=0;i<ans.length;i++) ans[i]=res.get(i);
			return ans;
		}

		@Override
		public String[] getOrderFields() {
			List<String> res=new ArrayList<String>();
			for(AllRestraintPart part:this.parts)
				res.add(part.orderField);
			String[] ans=new String[res.size()];
			for(int i=0;i<ans.length;i++) ans[i]=res.get(i);
			return ans;
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
	
}
