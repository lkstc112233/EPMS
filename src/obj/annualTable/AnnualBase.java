package obj.annualTable;

import java.sql.*;
import java.util.*;

import obj.*;
import obj.annualTable.Time;
import obj.staticSource.ACCESS;
import token.Role;

public abstract class AnnualBase extends Base implements Base.ListableWithNoSave{
	
	@SQLField(value="年",weight=0,isKey=true,notNull=true)
	public Integer year;
		public Integer getYear(){return year;}
		public void setYear(int year){this.year=year;}
		public void setYear(String a) {year=Field.s2i(a,0);}
	
	
	/**
	 * 保证不返回null
	 */
	static public List<Pair<Time,ACCESS>> listTime(Role role,int year,boolean setupIfEmpty)
			throws IllegalArgumentException, InstantiationException, SQLException{
		return AnnualBase.listTime(role, year, setupIfEmpty, true);
	}
	static private List<Pair<Time,ACCESS>> listTime(Role role,int year,boolean setupIfEmpty,boolean first)
			throws IllegalArgumentException, InstantiationException, SQLException{
		List<Pair<Time,ACCESS>> res=new ArrayList<Pair<Time,ACCESS>>();
		//将Time和ACCESS表联合查询，check筛选出当前权限符合的条目，依次放入res即可
		List<Base[]> tmp=Base.list(
				new JoinParam(Time.class).append(
						JoinParam.Type.InnerJoin, ACCESS.class,
						Field.getField(Time.class,"id"),
						Field.getField(ACCESS.class,"id")),
				new Restraint(new Restraint.Part[]{
						new Restraint.Part(
								Field.getField(Time.class,"year"),Integer.valueOf(year)),
						new Restraint.Part(
								Field.getField(ACCESS.class,role.toString()),Boolean.TRUE)
						},
						Field.getField(ACCESS.class,"id"))
				);
		if(first && setupIfEmpty && (tmp==null || tmp.isEmpty())){//first保证了不会调用第2次
			persistence.DB.setupTimeTable(year);
			return listTime(role,year,false,first=false);
		}
		if(tmp!=null) for(Base[] t:tmp)
			if(t!=null && t.length>=2 
			&& t[0]!=null && t[0] instanceof Time
			&& t[1]!=null && t[1] instanceof ACCESS)
				res.add(new Pair<Time,ACCESS>((Time)t[0],(ACCESS)t[1]));
		return res;
	}
	
	
	
}