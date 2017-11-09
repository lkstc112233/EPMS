package obj;

public class Restraint{
	static public enum Type{
		Smaller("<"),SmallerOrEqual("<="),
		Bigger(">"),BiggerOrEqual(">="),
		Equal("="),Like("LIKE");
		public final String operator;
		Type(String oper){this.operator=oper;}
		public String getKey(){return this.toString();}//Smaller
		public String getValue(){return this.operator;}//  <=
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
		
		public Part(Field f,Type t,Object s){field=f;type=t;value=s;}
		public Part(Field f,Object s){field=f;type=Type.Equal;value=s;}
			@Override public String toString(){
				return this.field.toString()+" "+this.type.getValue()+" "+this.value;
			}
			public String getSQLString(){
				return this.field.toString()+" "+this.type.getValue()+" ?";
			}
	}
	
	private Part[] where;		public Part[] getWhere(){return this.where;}
	private Field[] order;		public Field[] getOrder(){return this.order;}
	
	
	public String getSQLString(){
		StringBuilder sb=new StringBuilder();
		if(where!=null && where.length>0 && where[0]!=null){
			sb.append(" WHERE ");
			boolean first=true;
			for(Part w:this.where){
				if(first) first=false;
				else sb.append(" AND ");
				sb.append(w.getSQLString());
			}
		}
		if(order!=null && order.length>0 && order[0]!=null){
			sb.append(" ORDER BY ");
			boolean first=true;
			for(Field f:this.order){
				if(first) first=false;
				else sb.append(" AND ");
				sb.append(f.toString());
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
