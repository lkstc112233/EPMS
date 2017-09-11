package obj.annualTable;

import java.sql.SQLException;

import obj.*;

@SQLTable("Student")
public class Student extends AnnualTable{
	

	@SQLField(isKey=true,needImport=true,value="学号",description="学号不能重复")
	private String id;
	@SQLField(needImport=true,value="姓名",description="")
	private String name;
	@SQLField(needImport=true,value="手机",description="")
	private String mobile;
	@SQLField(needImport=true,value="邮箱",description="")
	private String email;
	@SQLField(source="Major",needImport=true,value="专业",description="")
	private String major;
	@SQLField(value="实习基地",description="")
	private String practiceBase;
	@SQLField(needImport=true,value="身份证号",description="")
	private String sfzh;
	@SQLField(source="ZZMM",needImport=true,value="政治面貌",description="")
	private String zzmm;
	@SQLField(source="Province",needImport=true,value="省份",description="")
	private String province;
	@SQLField(source="Nation",needImport=true,value="民族",description="")
	private String nation;
	@SQLField(needImport=true,value="回生源地",description="是否希望回生源地进行教育实习")
	private Boolean hxyx;
	@SQLField(needImport=false,value="指导老师工号",description="指导老师需要属于校内人员列表")
	private String teacherId;
	@SQLField(source="Outstanding",needImport=false,value="优秀实习生类别",description="")
	private String outstandingType;
	@SQLField(needImport=false,value="优秀实习生材料",description="二进制储存")
	private byte[] outstandingMaterial;
	
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getMobile() {return mobile;}
	public void setMobile(String mobile) {this.mobile = mobile;}
	public String getEmail() {return email;}
	public void setEmail(String email) {this.email = email;}
	public String getMajor() {return major;}
	public void setMajor(String major) {this.major = major;}
	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String practiceBase) {this.practiceBase = practiceBase;}
	public String getSfzh() {return sfzh;}
	public void setSfzh(String sfzh) {this.sfzh = sfzh;}
	public String getZzmm() {return zzmm;}
	public void setZzmm(String zzmm) {this.zzmm = zzmm;}
	public String getProvince() {return province;}
	public void setProvince(String province) {this.province = province;}
	public String getNation() {return nation;}
	public void setNation(String nation) {this.nation = nation;}
	public Boolean getHxyx() {return hxyx;}
	public void setHxyx(Boolean hxyx) {this.hxyx = hxyx;}	public void setHxyx(String hxyx){this.hxyx=Boolean.valueOf(hxyx);}
	public String getTeacherId() {return teacherId;}
	public void setTeacherId(String teacherId) {this.teacherId = teacherId;}
	public String getOutstandingType() {return outstandingType;}
	public void setOutstandingType(String outstandingType) {this.outstandingType = outstandingType;}
	public byte[] getOutstandingMaterial() {return outstandingMaterial;}
	public void setOutstandingMaterial(byte[] outstandingMaterial) {this.outstandingMaterial = outstandingMaterial;}


	public Student() throws SQLException {
		super();
	}
}