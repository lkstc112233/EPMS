package obj.annualTable;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import obj.*;
import obj.staticSource.ACCESS;
import token.Role;

public abstract class AnnualBase extends ListableBase implements ListableBase.ListableBaseWithNoSave{
	
	@SQLField(value="年",isKey=true,needImport=false,needSorted=true)
	public Integer year;
		public int getYear(){return year;}
		public void setYear(int year){this.year=year;}
		public void setYear(String s) {
			if(s==null) return;
			try{
				year=Integer.parseInt(s);
			}catch(NumberFormatException e){
				e.printStackTrace();
				return;
			}
		}
	
		
	public AnnualBase() throws SQLException {
		super();
	}
		/*
	static class Pair{
		public Class<? extends AnnualBase> clazz;
		public Integer year;
		public Pair(Class<?extends AnnualBase> clazz,int year){this.clazz=clazz;this.year=year;}
		public int hashCode(){
			return clazz.hashCode()|year.hashCode();
		}
		public boolean equals(Object o){
			if(o==null) return false;
			if(!(o instanceof Pair)) return false;
			Pair p=(Pair)o;
			return (this.clazz.equals(p.clazz)
					&& this.year.equals(p.year));
		}
	}
	static private Map<Pair,List<AnnualBase>> AnnualList=new HashMap<Pair,List<AnnualBase>>();
	static public <T extends AnnualBase>  List<T> list(Class<T> clazz,int year) throws SQLException, NoSuchFieldException, SecurityException{
		List<AnnualBase> tmp=AnnualBase.AnnualList.get(new Pair(clazz,year));
		if(tmp==null){
			AnnualBase.initialize(clazz,year);
			return list(clazz,year);
		}
		List<T> res=new ArrayList<T>();
		for(AnnualBase t:tmp){
			if(clazz.isInstance(t)){
				T at=clazz.cast(t);
				if(at.getYear()==year)
					res.add(at);
			}
		}
		return res;
	}
	/**
	 * 保证不返回null
	 */
	static public List<Time> listTime(Role role,int year,boolean setupIfEmpty) throws NoSuchFieldException, SecurityException, SQLException{
		/*
		List<Time> res=list(Time.class,year);
		if(res.isEmpty() && setupIfEmpty){
			persistence.DB.setupTimeTable(year);
			initialize(Time.class,year);
			return listTime(year,false);
		}
		return res;
		*/
		List<Time> res=new ArrayList<Time>();
		if(setupIfEmpty){
			res=ListableBase.list(Time.class,
					new String[]{"year"},
					new Object[]{Integer.valueOf(year)});
			if(res.isEmpty()){
				persistence.DB.setupTimeTable(year);
				initialize(Time.class,year);
				return listTime(role,year,false);
			}
		}
		else{
			//将Time和ACCESS表联合查询，check筛选出当前权限符合的条目，依次放入res即可
			ListableBase.JoinParam param=new JoinParam(Time.class);
			param.append("project",ListableBase.JoinType.InnerJoin,ACCESS.class,"project");
			List<Base[]> tmp=null;
			try {
				tmp=ListableBase.list(param,new String[]{
						"Time.year","ACCESS."+role.toString()
				},new Object[]{
						Integer.valueOf(year),Boolean.TRUE
				},new String[]{"ACCESS.id"});
			} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
				return res;
			}
			if(tmp==null) return res;
			for(Base[] t:tmp) {
				if(t!=null && t.length>0 &&
						t[0] instanceof Time)
					res.add((Time) t[0]);
				else
					throw new RuntimeException("The result of ListableBase.list(Joinparam) is error!");
			}
		}
		return res;
	}
	static private <T extends AnnualBase> void initialize(Class<T> clazz,int year) throws SQLException, NoSuchFieldException, SecurityException{
		List<AnnualBase> res=new ArrayList<AnnualBase>();
		List<T> tmp=null;
		tmp=SQLCollection.selectAll(clazz,new Field[]{
				Base.getField(clazz,"year")},
				new Object[]{year});//主要代码，从数据库读取数据，这里不会调用Base.load()
		res.addAll(tmp);
	//	AnnualBase.AnnualList.put(new Pair(clazz,year),res);
	}
	
	/*
	@Override
	public void update() throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update();
		Class<? extends AnnualBase> clazz=this.getClass();
		AnnualBase.AnnualList.remove(new Pair(clazz,this.getYear()));
	}
	@Override
	public void update(Field[] updateFields) throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update(updateFields);
		Class<? extends AnnualBase> clazz=this.getClass();
		AnnualBase.AnnualList.remove(new Pair(clazz,this.getYear()));
	}
	@Override
	public void create() throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.create();
		Class<? extends AnnualBase> clazz=this.getClass();
		AnnualBase.AnnualList.remove(new Pair(clazz,this.getYear()));
	}
	*/
	
	
	
}