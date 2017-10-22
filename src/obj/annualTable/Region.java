package obj.annualTable;

import java.sql.SQLException;
import java.sql.Timestamp;

import obj.*;

@SQLTable("Region")
public class Region extends AnnualBase{
	
	@SQLField(value="大区名称",isKey=true,ps="大区名称不能重复")
	private String name;
	@SQLField(value="实习基地",source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="总领队工号",source="InnerPerson.id")
	private String leaderId;
	@SQLField(value="入校时间",source="ZZMM.name")
	private Timestamp enterPracticeBaseTime;
	@SQLField(value="入校地点",source="Province.name")
	private String enterPracticeBasePlace;
	@SQLField(value="动员会时间")
	private Timestamp mobilizationTime;
	@SQLField(value="动员会地点",source="InnerPerson.id")
	private String mobilizationPlace;
	@SQLField(value="备注",ps="二进制储存")
	private String remark;


	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String practiceBase) {this.practiceBase = practiceBase;}
	public String getLeaderId() {return leaderId;}
	public void setLeaderId(String leaderId) {this.leaderId = leaderId;}
	public Timestamp getEnterPracticeBaseTime() {return enterPracticeBaseTime;}
	public void setEnterPracticeBaseTime(Timestamp enterPracticeBaseTime) {this.enterPracticeBaseTime = enterPracticeBaseTime;}
	public String getEnterPracticeBasePlace() {return enterPracticeBasePlace;}
	public void setEnterPracticeBasePlace(String enterPracticeBasePlace) {this.enterPracticeBasePlace = enterPracticeBasePlace;}
	public Timestamp getMobilizationTime() {return mobilizationTime;}
	public void setMobilizationTime(Timestamp mobilizationTime) {this.mobilizationTime = mobilizationTime;}
	public String getMobilizationPlace() {return mobilizationPlace;}
	public void setMobilizationPlace(String mobilizationPlace) {this.mobilizationPlace=mobilizationPlace;}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}


	
	public Region() throws SQLException {
		super();
	}
}