package obj.annualTable;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import obj.*;

public abstract class AnnualBase extends ListableBase implements ListableBase.ListableBaseWithNoSave{
	
	@SQLField(isKey=true,needImport=false)
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
	static public List<Time> listTime(int year,boolean setupIfEmpty) throws NoSuchFieldException, SecurityException, SQLException{
		List<Time> res=list(Time.class,year);
		if(res.isEmpty() && setupIfEmpty){
			persistence.DB.setupTimeTable(year);
			initialize(Time.class,year);
			return listTime(year,false);
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
		AnnualBase.AnnualList.put(new Pair(clazz,year),res);
	}
	
	
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
	
	
	
	
}