package obj.staticSource;

import java.sql.SQLException;
import java.sql.Timestamp;

import action.Manager;
import obj.*;
import obj.annualTable.Time;

@SQLTable("ACCESS")
public class ACCESS extends Base{
	
	@SQLField(value="序号",weight=1,notNull=true)
	private int id;
	@SQLField(value="项目名称",weight=2,isKey=true,notNull=true)
	private String project;
	@SQLField(value="动作名称",weight=10)
	private String actionClass="";
	@SQLField(value="起始时间",weight=11)
	private Timestamp time1;
	@SQLField(value="终止时间",weight=12)
	private Timestamp time2;
	@SQLField(value="学生权限",weight=13)
	private boolean xs;
	@SQLField(value="教学院长权限",weight=14)
	private boolean jxyz;
	@SQLField(value="教务员权限",weight=15)
	private boolean jwy;
	@SQLField(value="教师权限",weight=16)
	private boolean js;
	@SQLField(value="教务处权限",weight=17)
	private boolean jwc;
	@SQLField(value="领导权限",weight=18)
	private boolean ld;
	
	
	public int getId(){return id;}
	public void setId(int id){this.id=id;} public void setId(String a){this.id=a==null?null:Integer.valueOf(a);}
	public String getProject() {return project;}
	public void setProject(String project) {this.project = project==null||project.isEmpty()?null:project;}
	public String getActionClass(){return this.actionClass;}
	public void setActionClass(String actionClass){this.actionClass=actionClass==null||actionClass.isEmpty()?null:actionClass;}
	public Timestamp getTime1() {return time1;}
	public void setTime1(String s){
		try{
			this.time1=Timestamp.valueOf(s);
			Manager.RegularPeriod(time1,time2);
		}catch(IllegalArgumentException e){
			this.time1=null;
		}
	}
	public void setTime1(Timestamp time) {this.time1 = time;
	Manager.RegularPeriod(time1,time2);}
	public Timestamp getTime2() {return time2;}
	public void setTime2(String s){
		try{
			this.time2=Timestamp.valueOf(s);
			Manager.RegularPeriod(time1,time2);
		}catch(IllegalArgumentException e){
			this.time2=null;
		}
	}
	public void setTime2(Timestamp time) {this.time2 = time;
	Manager.RegularPeriod(time1,time2);}
	public boolean isXs() {return xs;}
	public void setXs(boolean xs) {this.xs = xs;}
	public void setXs(String a){this.xs=false;try{this.xs=Boolean.valueOf(a);}catch(IllegalArgumentException e){}}
	public boolean isJxyz() {return jxyz;}
	public void setJxyz(boolean jxyz) {this.jxyz = jxyz;}
	public void setJxyz(String a){this.jxyz=false;try{this.jxyz=Boolean.valueOf(a);}catch(IllegalArgumentException e){}}
	public boolean isJwy() {return jwy;}
	public void setJwy(boolean jwy) {this.jwy = jwy;}
	public void setJwy(String a){this.jwy=false;try{this.jwy=Boolean.valueOf(a);}catch(IllegalArgumentException e){}}
	public boolean isJs() {return js;}
	public void setJs(boolean js) {this.js = js;}
	public void setJs(String a){this.js=false;try{this.js=Boolean.valueOf(a);}catch(IllegalArgumentException e){}}
	public boolean isJwc() {return jwc;}
	public void setJwc(boolean jwc) {this.jwc = jwc;}
	public void setJwc(String a){this.jwc=false;try{this.jwc=Boolean.valueOf(a);}catch(IllegalArgumentException e){}}
	public boolean isLd() {return ld;}
	public void setLd(boolean ld) {this.ld = ld;}
	public void setLd(String a){this.ld=false;try{this.ld=Boolean.valueOf(a);}catch(IllegalArgumentException e){}}
	
	
	
	
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
	
	
	
	@Override
	public String getDescription() {
		return this.id+"_"+this.project;
	}
	
	
	
}