package obj.annualTable;

import java.sql.SQLException;

import obj.*;

@SQLTable("Region")
public class Plan extends AnnualBase {

	@SQLField(value="实习基地",isKey=true,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="专业",isKey=true,source="Major.name")
	private String major;
	@SQLField(value="人数")
	private int number;
	@SQLField(value="拒绝接收的民族")
	private String refuseNation;
	@SQLField(value="拒绝接收的性别")
	private int refuseSex;
	@SQLField(value="是否携带数字媒体设备")
	private boolean media;
	@SQLField(value="备注",ps="文本储存")
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
	
	
	public Plan() throws SQLException {
		super();
	}
	
	
	
		
}