package obj.annualTable;

import obj.*;

@SQLTable("Plan")
public class Plan extends AnnualBase {

	@SQLField(value="实习基地",weight=1,isKey=true,notNull=true,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="专业",weight=2,isKey=true,notNull=true,source="Major.name")
	private String major;
	@SQLField(value="人数",weight=10)
	private int number=0;
	@SQLField(value="拒绝接收的民族",weight=11)
	private String refuseNation=null;
	@SQLField(value="拒绝接收的性别",weight=12)
	private int refuseSex=0;
	@SQLField(value="是否携带数字媒体设备",weight=13)
	private boolean media=false;
	@SQLField(value="备注",weight=14,ps="文本储存")
	private String remark;

	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String practiceBase) {this.practiceBase = practiceBase==null||practiceBase.isEmpty()?null:practiceBase;}
	public String getMajor() {return major;}
	public void setMajor(String major) {this.major = major==null||major.isEmpty()?null:major;}
	public int getNumber() {return number;}
	public void setNumber(int number) {this.number = number;}
	public String getRefuseNation() {return refuseNation;}
	public void setRefuseNation(String refuseNation) {this.refuseNation = refuseNation;}
	public int getRefuseSex() {return refuseSex;}
	public void setRefuseSex(int refuseSex) {this.refuseSex = refuseSex;}
	public boolean isMedia() {return media;}
	public void setMedia(boolean media) {this.media = media;}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}
	
	
	
	
	@Override
	public String getDescription() {
		return this.practiceBase+"_"+this.major+"_"+this.number;
	}
	
	
	
		
}