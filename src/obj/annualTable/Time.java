package obj.annualTable;

import java.sql.SQLException;
import java.sql.Timestamp;

import action.Manager;
import obj.*;
import obj.staticSource.ACCESS;

@SQLTable("Time")
public class Time extends AnnualBase{

	@SQLField(value="序号",weight=1,notNull=true)
	private int id;
	@SQLField(value="项目名称",weight=2,isKey=true,notNull=true,source="ACCESS.project")
	private String project;
	@SQLField(value="起始时间",weight=11)
	private Timestamp time1;
	@SQLField(value="终止时间",weight=12)
	private Timestamp time2;
	
	
	public int getId(){return id;}
	public void setId(int id){this.id=id;}
	public String getProject() {return project;}
	public void setProject(String project) {
		this.project = project;
	}
	public Timestamp getTime1() {return time1;}
	public void setTime1(Timestamp time) {this.time1 = time;
	Manager.RegularPeriod(time1,time2);}
	public void setTime1(String s){
		try{
			this.time1=Timestamp.valueOf(s);
			Manager.RegularPeriod(time1,time2);
		}catch(IllegalArgumentException e){
			this.time1=null;
		}
	}
	public Timestamp getTime2() {return time2;}
	public void setTime2(Timestamp time) {this.time2 = time;
	Manager.RegularPeriod(time1,time2);}
	public void setTime2(String s){
		try{
			this.time2=Timestamp.valueOf(s);
			Manager.RegularPeriod(time1,time2);
		}catch(IllegalArgumentException e){
			this.time2=null;
		}
	}
	public String getActionClass(){
		try{
			ACCESS a=ACCESS.getFromTime(this);//already load
			return a.getActionClass();
		}catch(Exception e){
			e.printStackTrace();
			return "ERROR";
		}
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
	
	
	
	@Override
	public String getDescription() {
		return this.id+"_"+this.project;
	}
	
}