package obj.annualTable;

import java.sql.SQLException;
import java.sql.Timestamp;

import action.Manager;
import obj.*;
import obj.staticSource.ACCESS;

@SQLTable("Time")
public class Time extends AnnualBase{

	@SQLField(needSorted=true)
	private int id;
	@SQLField(isKey=true,source="ACCESS.project")
	private String project;
	@SQLField
	private Timestamp time1;
	@SQLField
	private Timestamp time2;
	
	private String project_pinyin;
	
	public int getId(){return id;}
	public void setId(int id){this.id=id;}
	public String getProject() {return project;}
	public void setProject(String project) {
		this.project = project;
		this.project_pinyin=ACCESS.map.get(this.project);
	}
	public Timestamp getTime1() {return time1;}
	public void setTime1(Timestamp time) {this.time1 = time;
	Manager.RegularPeriod(time1,time2);}
	public void setTime1(String s){
		this.time1=Timestamp.valueOf(s);
		Manager.RegularPeriod(time1,time2);
	}
	public Timestamp getTime2() {return time2;}
	public void setTime2(Timestamp time) {this.time2 = time;
	Manager.RegularPeriod(time1,time2);}
	public void setTime2(String s){
		this.time2=Timestamp.valueOf(s);
		Manager.RegularPeriod(time1,time2);
	}
	public String getProject_pinyin(){
		if(this.project_pinyin==null)
			this.setProject(this.getProject());
		return this.project_pinyin;
	}
	
	public Time() throws SQLException {
		super();
	}
	
	@SuppressWarnings("deprecation")
	static public Time getFromACCESS(int year,ACCESS a) throws SQLException{
		if(a==null) return null;
		Time t=new Time();
		t.setId(a.getId());
		t.setProject(a.getProject());
		t.setYear(year);
		if(a.getTime1()!=null){
			Timestamp at=new Timestamp(a.getTime1().getTime());
			at.setYear(year-1900);
			t.setTime1(at);
		}else
			t.setTime1((Timestamp)null);
		if(a.getTime2()!=null){
			Timestamp at=new Timestamp(a.getTime2().getTime());
			at.setYear(year-1900);
			t.setTime2(at);
		}else
			t.setTime2((Timestamp)null);
		return t;
	}
	
}