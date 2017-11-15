package obj;

import java.sql.*;

public class Restraint{
	static public enum Type{
		Smaller("<"),SmallerOrEqual("<="),
		Bigger(">"),BiggerOrEqual(">="),
		Equal("="),Like("LIKE");
		public final String operator;
		Type(String oper){this.operator=oper;}
		public String getKey(){return this.toString();}//Smaller
		public String getValue(){return this.operator;}//  <=
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public boolean checkBase(Object A,Object B){
			if(!(A instanceof Comparable && B instanceof Comparable)) return false;
			Comparable a=(Comparable)A;
			Comparable b=(Comparable)B;
			if(this==Smaller) return a.compareTo(b)<0;
			if(this==SmallerOrEqual) return a.compareTo(b)<=0;
			if(this==Bigger) return a.compareTo(b)>0;
			if(this==BiggerOrEqual) return a.compareTo(b)>=0;
			if(this==Equal) return a.equals(b);
			//TODO SQL Like
			if(this==Like) return a.equals(b);
			return false;
		}
	}
		public Type[] getTypeList(){return Type.values();}
	static public class Part{
		private Field field;
		private Type type;
		private Object value;
			public Field getField(){return field;}
			public Type getType(){return this.type;}
			public void setType(Type t){this.type=t;}
			public void setType(String s){
				this.type=null;
				if(s!=null && !s.isEmpty()) try{
					this.type=Type.valueOf(s);
				}catch(IllegalArgumentException e){e.printStackTrace();}
			}
			public Object getValue(){return this.value;}
			public void setValue(String s){
				this.value=s==null||s.isEmpty()?null:s;
			}
		public void copyTo(Part p){
			p.field=this.field;
			p.type=this.type;
			p.value=this.value;
		}
		public boolean checkAndSetBase(Base b,boolean setIfFalse) throws IllegalArgumentException{
			if(!this.field.getClazz().isAssignableFrom(b.getClass()))
				throw new IllegalArgumentException("Class not correct!");
			if(this.type.checkBase(this.field.get(b),value)) return true;
			if(setIfFalse){
		//TODO	if(this.type==Type.Equal)
					this.field.set(b,this.value);
			}
			return false;
		}
		
		public Part(Field f,Type t,Object s){field=f;type=t;value=s;}
		public Part(Field f,Object s){field=f;type=Type.Equal;value=s;}
			@Override public String toString(){
				return this.field.getSQLField(".")+" "+this.type.getValue()+" "+this.value;
			}
			public String getSQLString(){
				return this.field.getSQLField(".")+" "+this.type.getValue()+" ?";
			}
			public int setSQLParam(PreparedStatement pst,int parameterIndex) throws SQLException{
				if(this.type!=null)
					pst.setObject(parameterIndex++,this.getValue());
				return parameterIndex;
			}
	}
	static public class OrPart extends Part{
		public OrPart(Field f,Object[] s){
			super(f,s);
		}public OrPart(Field f,Type t,Object[] s){
			super(f,t,s);
		}
		public boolean checkAndSetBase(Base b,boolean setIfFalse)throws IllegalArgumentException{
			if(this.getField().getClazz().isAssignableFrom(b.getClass()))
				throw new IllegalArgumentException("Class not correct!");
			for(Object o:(Object[])this.getValue())
				if(this.getType().checkBase(this.getField().get(b),o))
					return true;
			if(setIfFalse){
		//TODO	if(this.type==Type.Equal)
					this.getField().set(b,((Object[])this.getValue())[0]);
			}
			return false;
		}
		public String toString(){
			StringBuilder sb=new StringBuilder();
			return sb.toString();
		}
		public String getSQLString(){
			StringBuilder sb=new StringBuilder("( ");
			boolean first=true;
			for(@SuppressWarnings("unused") Object o:(Object[])this.getValue()){
				if(first) first=false;
				else sb.append(" OR ");
				sb.append(this.getField().getSQLField("."));
				sb.append(" ");
				sb.append(this.getType().getValue());
				sb.append(" ?");
			}sb.append(" )");
			return sb.toString();
		}
		public int setSQLParam(PreparedStatement pst,int parameterIndex) throws SQLException{
			for(Object o:(Object[])this.getValue())
				pst.setObject(parameterIndex++,o);
			return parameterIndex;
		}
	}
	
	
	
	private Part[] where;		public Part[] getWhere(){return this.where;}
	private Field[] order;		public Field[] getOrder(){return this.order;}
	
	
	public String getSQLString(){
		StringBuilder sb=new StringBuilder();
		if(where!=null && where.length>0 && where[0]!=null){
			boolean first=true;
			for(Part w:this.where) if(w!=null && w.field!=null && w.type!=null) {
				if(first){sb.append(" WHERE ");first=false;}
				else sb.append(" AND ");
				sb.append(w.getSQLString());
			}
		}
		if(order!=null && order.length>0 && order[0]!=null){
			sb.append(" ORDER BY ");
			boolean first=true;
			for(Field f:this.order) if(f!=null){
				if(first) first=false;
				else sb.append(" AND ");
				sb.append(f.getSQLField("."));
			}
		}
		return sb.toString();
	}
	
	public Restraint(Part[] where,Field[] order){
		this.where=where;
		this.order=order;
	}
	
	public Restraint(Part[] where){
		this(where,(Field[])null);
	}public Restraint(Field[] order){
		this((Part[])null,order);
	}public Restraint(Part where,Field[] order){
		this(new Part[]{where},order);
	}public Restraint(Part[] where,Field order){
		this(where,new Field[]{order});
	}public Restraint(Part where,Field order){
		this(new Part[]{where},new Field[]{order});
	}
	
	public Restraint(Part where){
		this(where,(Field)null);
	}public Restraint(Field order){
		this((Part)null,new Field[]{order});
	}public Restraint(){
		this((Part)null,(Field)null);
	}

	public Restraint(Field f,Type t,Object v,Field[] order){
		this(new Part(f,t,v),order);
	}public Restraint(Field f,Type t,Object v,Field order){
		this(new Part(f,t,v),order);
	}public Restraint(Field f,Type t,Object v){
		this(new Part(f,t,v),(Field)null);
	}
	public Restraint(Field f,Object v,Field[] order){
		this(new Part(f,v),order);
	}public Restraint(Field f,Object v,Field order){
		this(new Part(f,v),order);
	}public Restraint(Field f,Object v){
		this(new Part(f,v),(Field)null);
	}
}
