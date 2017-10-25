package obj.staticSource;

import java.sql.SQLException;
import java.sql.Timestamp;

import action.Manager;
import obj.*;
import obj.annualTable.Time;

@SQLTable("ACCESS")
public class ACCESS extends ListableBase{
	
	@SQLField(needSorted=true)
	private int id;
	@SQLField(isKey=true)
	private String project;
	@SQLField
	private String actionClass="";
	@SQLField
	private Timestamp time1;
	@SQLField
	private Timestamp time2;
	@SQLField
	private Boolean 学生;
	@SQLField
	private Boolean 教学院长;
	@SQLField
	private Boolean 教务员;
	@SQLField
	private Boolean 教师;
	@SQLField
	private Boolean 教务处;
	@SQLField
	private Boolean 领导;
	
	
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id=id;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getActionClass(){
		return this.actionClass;
	}
	public void setActionClass(String actionClass){
		this.actionClass=actionClass;
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
	public Boolean is学生() {
		return 学生;
	}
	public void set学生(Boolean 学生) {
		this.学生 = 学生;
	}
	public Boolean is教学院长() {
		return 教学院长;
	}
	public void set教学院长(Boolean 教学院长) {
		this.教学院长 = 教学院长;
	}
	public Boolean is教务员() {
		return 教务员;
	}
	public void set教务员(Boolean 教务员) {
		this.教务员 = 教务员;
	}
	public Boolean is教师() {
		return 教师;
	}
	public void set教师(Boolean 教师) {
		this.教师 = 教师;
	}
	public Boolean is教务处() {
		return 教务处;
	}
	public void set教务处(Boolean 教务处) {
		this.教务处 = 教务处;
	}
	public Boolean is领导() {
		return 领导;
	}
	public void set领导(Boolean 领导) {
		this.领导 = 领导;
	}



	public ACCESS() throws SQLException {
		super();
	}
	
	static public ACCESS getFromTime(Time t) throws SQLException, IllegalArgumentException, IllegalAccessException{
		if(t==null) return null;
		ACCESS a=new ACCESS();
		a.setId(t.getId());
		a.setProject(t.getProject());
		a.load();
		a.setTime1(t.getTime1());
		a.setTime2(t.getTime2());
		return a;
	}
	
	
	
}