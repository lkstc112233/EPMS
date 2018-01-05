package obj.annualTable;

import java.sql.Timestamp;

import action.Manager;
import obj.*;

@SQLTable("Supervise")
public class Supervise extends AnnualBase{
	@SQLField(value="实习基地",weight=1,isKey=true,notNull=true,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="督导类型",weight=2,isKey=true,notNull=true,ps="0/1/2分别表示入校/中期/返校督导")
	private int superviseType;
	@SQLField(value="督导老师工号",weight=3,source="InnerPerson.id")
	private String supervisorId;
	@SQLField(value="督导开始时间",weight=10)
	private Timestamp time1;
	@SQLField(value="督导结束时间",weight=11)
	private Timestamp time2;
	@SQLField(value="备注",weight=12,ps="文本储存")
	private String remark;
	@SQLField(value="机票费",weight=13,ps="小于100000")
	private float moneyPlane;
	@SQLField(value="订票费",weight=14,ps="小于100000")
	private float moneyBooking;
	@SQLField(value="保险费",weight=15,ps="小于100000")
	private float moneyInsurance;
	@SQLField(value="火车票",weight=16,ps="小于100000")
	private float moneyTrain;
	@SQLField(value="住宿费",weight=17,ps="小于100000")
	private float moneyAccommodation;
	@SQLField(value="住宿天数",weight=18,ps="小于128")
	private int   moneyAccommodationNum;
	@SQLField(value="其他费用",weight=19,ps="小于100000")
	private float moneyElse;
	
	static private final int[] TypeList=new int[]{0,1,2};
		static public int[] getTypeList(){return Supervise.TypeList;}
	static private final String[] TypeNameList=new String[] {"入校督导","中期督导","返校督导"};
		static public String[] getTypeNameList() {return Supervise.TypeNameList;}

	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String a) {this.practiceBase=Field.s2S(a);}
	public String getSupervisorId() {return supervisorId;}
	public void setSupervisorId(String a) {this.supervisorId=Field.s2S(a);}
	public int getSuperviseType(){return this.superviseType;}
	public void setSuperviseType(int a){this.superviseType=a;}
	public void setSuperviseType(String a){this.superviseType=Field.s2i(a,0);}
	public Timestamp getTime1() {return time1;}
	public void setTime1(Timestamp time) {this.time1 = time;
	Manager.RegularPeriod(time1,time2);}
	public void setTime1(String a){
		this.time1=Field.s2TS(a);
		Manager.RegularPeriod(time1,time2);
	}
	public Timestamp getTime2() {return time2;}
	public void setTime2(Timestamp time) {this.time2 = time;
	Manager.RegularPeriod(time1,time2);}
	public void setTime2(String a){
		this.time2=Field.s2TS(a);
		Manager.RegularPeriod(time1,time2);
	}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}

	public float getMoneyPlane() {return moneyPlane;}
	public void setMoneyPlane(float a) {this.moneyPlane = a;}
	public void setMoneyPlane(String a){this.moneyPlane=Field.s2f(a,0);}
	public float getMoneyBooking() {return moneyBooking;}
	public void setMoneyBooking(float a) {this.moneyBooking = a;}
	public void setMoneyBooking(String a){this.moneyBooking=Field.s2f(a,0);}
	public float getMoneyInsurance() {return moneyInsurance;}
	public void setMoneyInsurance(float a) {this.moneyInsurance = a;}
	public void setMoneyInsurance(String a){this.moneyInsurance=Field.s2f(a,0);}
	public float getMoneyTrain() {return moneyTrain;}
	public void setMoneyTrain(float a) {this.moneyTrain = a;}
	public void setMoneyTrain(String a){this.moneyTrain=Field.s2f(a,0);}
	public float getMoneyAccommodation() {return moneyAccommodation;}
	public void setMoneyAccommodation(float a) {this.moneyAccommodation = a;}
	public void setMoneyAccommodation(String a){this.moneyAccommodation=Field.s2f(a,0);}
	public int getMoneyAccommodationNum() {return moneyAccommodationNum;}
	public void setMoneyAccommodationNum(int a) {this.moneyAccommodationNum = a;}
	public void setMoneyAccommodationNum(String a){this.moneyAccommodationNum=Field.s2i(a,0);}
	public float getMoneyElse() {return moneyElse;}
	public void setMoneyElse(float a) {this.moneyElse = a;}
	public void setMoneyElse(String a){this.moneyElse=Field.s2f(a,0);}



	@Override
	public String getDescription() {
		return this.superviseType+"_"+this.practiceBase+"("+this.supervisorId+")";
	}
	
	
	
	
	
}