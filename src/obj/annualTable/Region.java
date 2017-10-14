package obj.annualTable;

import java.sql.SQLException;
import java.sql.Timestamp;

import obj.*;

@SQLTable("Student")
public class Region extends AnnualBase{
	
	@SQLField(value="大区名称",isKey=true,description="学号不能重复")
	private String name;
	@SQLField(value="实习基地",source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="总领队工号",source="InnerPerson.id")
	private String leaderId;
	@SQLField(value="入校时间",source="ZZMM.name")
	private Timestamp enterPracticeBaseTime;
	@SQLField(value="入校地点",source="Province.name")
	private String enterPracticeBasePlace;
	@SQLField(value="动员会时间",description="是否希望回生源地进行教育实习")
	private Timestamp mobilizationTime;
	@SQLField(value="动员会地点",source="InnerPerson.id",description="指导老师需要属于校内人员列表")
	private String teacherId;
	@SQLField(value="备注",description="二进制储存")
	private byte[] remark;


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
	public String getTeacherId() {return teacherId;}
	public void setTeacherId(String teacherId) {this.teacherId = teacherId;}
	public byte[] getRemark() {return remark;}
	public void setRemark(byte[] remark) {this.remark = remark;}


	
	public Region() throws SQLException {
		super();
	}
}