package obj.staticSource;

import java.sql.SQLException;
import java.sql.Timestamp;

import action.Manager;
import obj.*;
import obj.annualTable.Time;

@SQLTable("ACCESS")
public class ACCESS extends Base{
	static public final int jysx=100;
	static public final int[] supervise=new int[] {101,102,103};
	

	@SQLField(value="序号",weight=1,isKey=true,notNull=true)
	private int id;
	@SQLField(value="项目名称",weight=2,notNull=true)
	private String project;
	@SQLField(value="动作名称",weight=10)
	private String actionClass;
	@SQLField(value="起始时间",weight=11)
	private Timestamp time1;
	@SQLField(value="终止时间",weight=12)
	private Timestamp time2;
	@SQLField(value="学生权限",weight=13,notNull=true)
	private boolean xs=false;
	@SQLField(value="教育实习联系人权限",weight=14,notNull=true)
	private boolean lxr=false;
	@SQLField(value="教师权限",weight=16,notNull=true)
	private boolean js=false;
	@SQLField(value="教务处权限",weight=17,notNull=true)
	private boolean jwc=true;
	@SQLField(value="领导权限",weight=18,notNull=true)
	private boolean ld=false;
	
	public void setId(int a){this.id=a;}
	public void setId(String a) {this.id=Field.s2i(a,-1);}
	public int getId() {return this.id;}
	public String getProject() {return project;}
	public void setProject(String a) {this.project=Field.s2s(a,"");}
	public String getActionClass(){return this.actionClass;}
	public void setActionClass(String a){this.actionClass=Field.s2S(a);}
	public Timestamp getTime1() {return time1;}
	public void setTime1(String a){
		this.time1=Field.s2TS(a);
		Manager.RegularPeriod(time1,time2);
	}
	public void setTime1(Timestamp time) {
		this.time1=time;
		Manager.RegularPeriod(time1,time2);
	}
	public Timestamp getTime2() {return time2;}
	public void setTime2(String a){
		this.time2=Field.s2TS(a);
		Manager.RegularPeriod(time1,time2);
	}
	public void setTime2(Timestamp time) {
		this.time2=time;
		Manager.RegularPeriod(time1,time2);
	}
	public boolean getXs() {return xs;}
	public void setXs(boolean xs) {this.xs = xs;}
	public void setXs(String a){this.xs=Field.s2b(a,false);}
	public boolean getLxr() {return lxr;}
	public void setLxr(boolean lxr) {this.lxr = lxr;}
	public void setLxr(String a){this.lxr=Field.s2b(a,false);}
	public boolean getJs() {return js;}
	public void setJs(boolean js) {this.js = js;}
	public void setJs(String a){this.js=Field.s2b(a,false);}
	public boolean getJwc() {return jwc;}
	public void setJwc(boolean jwc) {this.jwc = jwc;}
	public void setJwc(String a){this.jwc=Field.s2b(a,true);}
	public boolean getLd() {return ld;}
	public void setLd(boolean ld) {this.ld = ld;}
	public void setLd(String a){this.ld=Field.s2b(a,false);}
	
	
	public ACCESS() {
		super();
	}
	public ACCESS(int id) throws IllegalArgumentException, SQLException {
		this();
		try{
			for(ACCESS a:Base.list(ACCESS.class)) if(a.getProject().equals(project)) {
				a.copyTo(this);
				return;
			}
		}catch(Exception e) {}
		this.setId(id);
		this.load();
	}
	
	
	
	static public ACCESS getFromTime(Time t) throws SQLException, IllegalArgumentException, IllegalAccessException{
		if(t==null) return null;
		ACCESS a=new ACCESS();
		a.setId(t.getId());
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