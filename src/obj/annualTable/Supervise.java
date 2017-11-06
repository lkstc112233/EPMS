package obj.annualTable;

import java.sql.SQLException;
import java.sql.Timestamp;

import action.Manager;
import obj.ListableBase.ListableBaseWithNoSave;
import obj.*;

@SQLTable("Supervise")
public class Supervise extends AnnualBase implements ListableBaseWithNoSave{
	
	
	@SQLField(value="实习基地",isKey=true,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="督导老师工号",source="InnerPerson.id")
	private String supervisorId;
	@SQLField(value="督导类型",isKey=true,ps="1/2/3分别表示入校/中期/返校督导")
	private int superviseType;
	@SQLField(value="督导开始时间")
	private Timestamp time1;
	@SQLField(value="督导结束时间")
	private Timestamp time2;
	@SQLField(value="机票费",ps="小于100000")
	private float moneyPlane;
	@SQLField(value="机票费",ps="小于100000")
	private float moneyBooking;
	@SQLField(value="机票费",ps="小于100000")
	private float moneyInsurance;
	@SQLField(value="机票费",ps="小于100000")
	private float moneyTrain;
	@SQLField(value="机票费",ps="小于100000")
	private float moneyAccommodation;
	@SQLField(value="住宿天数",ps="小于128")
	private int   moneyAccommodationNum;
	@SQLField(value="其他费用",ps="小于100000")
	private float moneyElse;
	@SQLField(value="备注",ps="文本储存")
	private String remark;
	
	static int[] TypeList=new int[]{0,1,2};
		static public int[] getTypesList(){return Supervise.TypeList;}

	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String practiceBase) {this.practiceBase = practiceBase==null||practiceBase.isEmpty()?null:practiceBase;}
	public String getSupervisorId() {return supervisorId;}
	public void setSupervisorId(String supervisorId) {this.supervisorId = supervisorId==null||supervisorId.isEmpty()?null:supervisorId;}
	public int getSuperviseType(){return this.superviseType;}
	public void setSuperviseType(int a){this.superviseType=a;}
	public void setSuperviseType(String a){try{this.superviseType=Integer.valueOf(a);}catch(NumberFormatException e){e.printStackTrace();}}
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
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}

	public float getMoneyPlane() {return moneyPlane;}
	public void setMoneyPlane(float a) {this.moneyPlane = a;}
	public void setMoneyPlane(String a){try{this.moneyPlane=Float.parseFloat(a);}catch(NumberFormatException e){e.printStackTrace();}}
	public float getMoneyBooking() {return moneyBooking;}
	public void setMoneyBooking(float a) {this.moneyBooking = a;}
	public void setMoneyBooking(String a){try{this.moneyBooking=Float.parseFloat(a);}catch(NumberFormatException e){e.printStackTrace();}}
	public float getMoneyInsurance() {return moneyInsurance;}
	public void setMoneyInsurance(float a) {this.moneyInsurance = a;}
	public void setMoneyInsurance(String a){try{this.moneyInsurance=Float.parseFloat(a);}catch(NumberFormatException e){e.printStackTrace();}}
	public float getMoneyTrain() {return moneyTrain;}
	public void setMoneyTrain(float a) {this.moneyTrain = a;}
	public void setMoneyTrain(String a){try{this.moneyTrain=Float.parseFloat(a);}catch(NumberFormatException e){e.printStackTrace();}}
	public float getMoneyAccommodation() {return moneyAccommodation;}
	public void setMoneyAccommodation(float a) {this.moneyAccommodation = a;}
	public void setMoneyAccommodation(String a){try{this.moneyAccommodation=Float.parseFloat(a);}catch(NumberFormatException e){e.printStackTrace();}}
	public int getMoneyAccommodationNum() {return moneyAccommodationNum;}
	public void setMoneyAccommodationNum(int a) {this.moneyAccommodationNum = a;}
	public void setMoneyAccommodationNum(String a){try{this.moneyAccommodationNum=Integer.parseInt(a);}catch(NumberFormatException e){e.printStackTrace();}}
	public float getMoneyElse() {return moneyElse;}
	public void setMoneyElse(float a) {this.moneyElse = a;}
	public void setMoneyElse(String a){try{this.moneyElse=Float.parseFloat(a);}catch(NumberFormatException e){e.printStackTrace();}}


	public Supervise() throws SQLException {
		super();
	}
	
	
	
	
	
}