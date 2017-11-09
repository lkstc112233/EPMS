package obj.annualTable;

import java.sql.*;
import java.util.*;

import obj.*;
import obj.staticSource.ACCESS;
import token.Role;

public abstract class AnnualBase extends Base implements Base.ListableWithNoSave{
	
	@SQLField(value="年",weight=1,isKey=true,notNull=true)
	public Integer year;
		public Integer getYear(){return year;}
		public void setYear(int year){this.year=year;}
		public void setYear(String a) {year=Field.s2i(a);}
	
	
	/**
	 * 保证不返回null
	 */
	static public List<Time> listTime(Role role,int year,boolean setupIfEmpty)
			throws IllegalArgumentException, InstantiationException, SQLException{
		return AnnualBase.listTime(role, year, setupIfEmpty, true);
	}
	static private List<Time> listTime(Role role,int year,boolean setupIfEmpty,boolean first)
			throws IllegalArgumentException, InstantiationException, SQLException{
		List<Time> res=new ArrayList<Time>();
		//将Time和ACCESS表联合查询，check筛选出当前权限符合的条目，依次放入res即可
		List<Base[]> tmp=Base.list(
				new Base.JoinParam(Time.class).append(
						Base.JoinType.InnerJoin, ACCESS.class,
						Field.getField(Time.class,"project"),
						Field.getField(ACCESS.class,"project")),
				new Field[]{Field.getField(Time.class,"year"),
						Field.getField(ACCESS.class,role.toString())},
				new Object[]{Integer.valueOf(year),
						Boolean.TRUE},
				new Field[]{Field.getField(ACCESS.class,"id")}
				);
		if(setupIfEmpty && (tmp==null || tmp.isEmpty()) && first){
			persistence.DB.setupTimeTable(year);
			return listTime(role,year,false,first=false);
		}
		if(tmp!=null) for(Base[] t:tmp)
			if(t!=null && t.length>0 && t[0] instanceof Time)
				res.add((Time) t[0]);
		return res;
	}
	
	
	
}