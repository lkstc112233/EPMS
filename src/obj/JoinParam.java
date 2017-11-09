package obj;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class JoinParam{
	static public String ListFieldPrefix="__LF__";
	
	static public enum Type{
		InnerJoin,LeftJoin,RightJoin;
		@Override
		public String toString(){
			return this.name().replaceAll("Join"," Join").toUpperCase();
		}
	}
	
	static public class Part{
		private Type type;						public Type getType(){return type;}
		private Class<? extends Base> clazz;		public Class<? extends Base> getClazz(){return clazz;}
		private Set<Field> fields;					public Set<Field> getFields(){return fields;}
		private Field[] lastOnFields;			public Field[] getLastOnFields(){return this.lastOnFields;}
		private Field[] thisOnFields;			public Field[] getThisOnFields(){return this.thisOnFields;}
		private Field[] onCheckFields;			public Field[] getOnCheckFields(){return this.onCheckFields;}
		private Object[] onCheckFieldsValue;		public Object[] getOnCheckFieldsValue(){return this.onCheckFieldsValue;}
		private Part(Class<? extends Base> c,Set<Field> fields){this(null,c,fields,null,null,null,null);}
		public Part(Type jp,Class<? extends Base> c,Set<Field> fields,
				Field[] lastOnFields,Field[] thisOnFields,
				Field[] onCheckFields,Object[] onCheckFieldsValue){
			this.type=jp;this.clazz=c;
			this.fields=fields==null?new TreeSet<Field>():fields;
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
	private List<Part> list=new ArrayList<Part>();
		public List<Part> getList(){return list;}
	/*	public List<Field> getAllFields(){
			List<Field> res=new ArrayList<Field>();
			for(Part p:this.list) res.addAll(p.getFields());
			return res;
		}*/
	
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
	}public JoinParam append(Type jp,Class<? extends Base> c,Set<Field> fields,
			Field[] lastOnFields,Field[] thisOnFields,
			Field[] onCheckFields,Object[] onCheckFieldsValue){
		return this.append(new Part(
				jp,c,fields,lastOnFields,thisOnFields,onCheckFields,onCheckFieldsValue));
	}public JoinParam append(Type jp,Class<? extends Base> c,Set<Field> fields,
			Field[] lastOnFields,Field[] thisOnFields,
			Field onCheckFields,Object onCheckFieldsValue){
		return this.append(new Part(
				jp,c,fields,lastOnFields,thisOnFields,new Field[]{onCheckFields},new Object[]{onCheckFieldsValue}));
	}public JoinParam append(Type jp,Class<? extends Base> c,Set<Field> fields,
			Field[] lastOnFields,Field[] thisOnFields){
		return this.append(new Part(
				jp,c,fields,lastOnFields,thisOnFields,(Field[])null,(Object[])null));
	}public JoinParam append(Type jp,Class<? extends Base> c,Set<Field> fields,
			Field lastOnFields,Field thisOnFields,
			Field[] onCheckFields,Object[] onCheckFieldsValue){
		return this.append(new Part(
				jp,c,fields,new Field[]{lastOnFields},new Field[]{thisOnFields},onCheckFields,onCheckFieldsValue));
	}public JoinParam append(Type jp,Class<? extends Base> c,Set<Field> fields,
			Field lastOnFields,Field thisOnFields,
			Field onCheckFields,Object onCheckFieldsValue){
		return this.append(new Part(
				jp,c,fields,new Field[]{lastOnFields},new Field[]{thisOnFields},new Field[]{onCheckFields},new Object[]{onCheckFieldsValue}));
	}public JoinParam append(Type jp,Class<? extends Base> c,Set<Field> fields,
			Field lastOnFields,Field thisOnFields){
		return this.append(new Part(
				jp,c,fields,new Field[]{lastOnFields},new Field[]{thisOnFields},(Field[])null,(Object[])null));
	}public JoinParam append(Type jp,Class<? extends Base> c,Set<Field> fields){
		return this.append(new Part(
				jp,c,fields,(Field[])null,(Field[])null,(Field[])null,(Object[])null));
	}/*无fields*/
	public JoinParam append(Type jp,Class<? extends Base> c,
			Field[] lastOnFields,Field[] thisOnFields,
			Field[] onCheckFields,Object[] onCheckFieldsValue){
		return this.append(new Part(
				jp,c,Field.getFieldsSet(c),lastOnFields,thisOnFields,onCheckFields,onCheckFieldsValue));
	}public JoinParam append(Type jp,Class<? extends Base> c,
			Field[] lastOnFields,Field[] thisOnFields,
			Field onCheckFields,Object onCheckFieldsValue){
		return this.append(new Part(
				jp,c,Field.getFieldsSet(c),lastOnFields,thisOnFields,new Field[]{onCheckFields},new Object[]{onCheckFieldsValue}));
	}public JoinParam append(Type jp,Class<? extends Base> c,
			Field[] lastOnFields,Field[] thisOnFields){
		return this.append(new Part(
				jp,c,Field.getFieldsSet(c),lastOnFields,thisOnFields,null,null));
	}public JoinParam append(Type jp,Class<? extends Base> c,
			Field lastOnFields,Field thisOnFields,
			Field[] onCheckFields,Object[] onCheckFieldsValue){
		return this.append(new Part(
				jp,c,Field.getFieldsSet(c),new Field[]{lastOnFields},new Field[]{thisOnFields},onCheckFields,onCheckFieldsValue));
	}public JoinParam append(Type jp,Class<? extends Base> c,
			Field lastOnFields,Field thisOnFields,
			Field onCheckFields,Object onCheckFieldsValue){
		return this.append(new Part(
				jp,c,Field.getFieldsSet(c),new Field[]{lastOnFields},new Field[]{thisOnFields},new Field[]{onCheckFields},new Object[]{onCheckFieldsValue}));
	}public JoinParam append(Type jp,Class<? extends Base> c,
			Field lastOnFields,Field thisOnFields){
		return this.append(new Part(
				jp,c,Field.getFieldsSet(c),new Field[]{lastOnFields},new Field[]{thisOnFields},null,null));
	}public JoinParam append(Type jp,Class<? extends Base> c){
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
	
}