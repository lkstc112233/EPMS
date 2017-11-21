package obj.annualTable;

import obj.*;

@SQLTable("Student")
public class Student extends AnnualBase{
	
	@SQLField(value="学号",weight=1,isKey=true,notNull=true,ps="学号不能重复")
	private String id;
	@SQLField(value="姓名",weight=10,notNull=true)
	private String name;
	@SQLField(value="手机",weight=11)
	private String mobile;
	@SQLField(value="邮箱",weight=12)
	private String email;
	@SQLField(value="专业",weight=13,notNull=true,source="Major.name")
	private String major;
	@SQLField(value="实习基地",weight=14,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="身份证号",weight=15,notNull=true)
	private String sfzh;
	@SQLField(value="政治面貌",weight=16,notNull=true,source="ZZMM.name")
	private String zzmm;
	@SQLField(value="省份",weight=17,notNull=true,source="Province.name")
	private String province;
	@SQLField(value="民族",weight=18,notNull=true,source="Nation.name")
	private String nation;
	@SQLField(value="回生源地",weight=19,ps="是否希望回生源地进行教育实习")
	private Boolean hxyx=true;
	@SQLField(value="指导老师工号",weight=20,source="InnerPerson.id",ps="指导老师需要属于校内人员列表")
	private String teacherId;
	@SQLField(value="优秀实习生类别",weight=21,autoInit=true,source="Outstanding.type")
	private String outstandingType;
	@SQLField(value="优秀实习生材料",weight=22,autoInit=true,ps="二进制储存")
	private byte[] outstandingMaterial;
	
	public String getId() {return id;}
	public void setId(String id) {this.id = id==null||id.isEmpty()?null:id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getMobile() {return mobile;}
	public void setMobile(String mobile) {this.mobile = mobile;}
	public String getEmail() {return email;}
	public void setEmail(String email) {this.email = email;}
	public String getMajor() {return major;}
	public void setMajor(String major) {this.major = major==null||major.isEmpty()?null:major;}
	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String practiceBase) {this.practiceBase = practiceBase==null||practiceBase.isEmpty()?null:practiceBase;}
	public String getSfzh() {return sfzh;}
	public void setSfzh(String sfzh) {this.sfzh = sfzh;}
	public String getZzmm() {return zzmm;}
	public void setZzmm(String zzmm) {this.zzmm = zzmm==null||zzmm.isEmpty()?null:zzmm;}
	public String getProvince() {return province;}
	public void setProvince(String province) {this.province = province==null||province.isEmpty()?null:province;}
	public String getNation() {return nation;}
	public void setNation(String nation) {this.nation = nation==null|nation.isEmpty()?null:nation;}
	public Boolean getHxyx() {return hxyx;}
	public void setHxyx(Boolean hxyx) {this.hxyx = hxyx;}	public void setHxyx(String hxyx){this.hxyx=Boolean.valueOf(hxyx);}
	public String getTeacherId() {return teacherId;}
	public void setTeacherId(String teacherId) {this.teacherId = teacherId==null||teacherId.isEmpty()?null:teacherId;}
	public String getOutstandingType() {return outstandingType;}
	public void setOutstandingType(String outstandingType) {this.outstandingType = outstandingType==null||outstandingType.isEmpty()?null:outstandingType;}
	public byte[] getOutstandingMaterial() {return outstandingMaterial;}
	public void setOutstandingMaterial(byte[] outstandingMaterial) {this.outstandingMaterial = outstandingMaterial==null||outstandingMaterial.length<=0?null:outstandingMaterial;}
	public void setOutstandingMaterial(String outstandingMaterial) {this.outstandingMaterial = outstandingMaterial==null||outstandingMaterial.isEmpty()?null:outstandingMaterial.getBytes();}
	
	@Override
	public String getDescription() {
		return this.getName()+"("+this.getId()+")";
	}
	
	
	/**
	 * 身份证号第17位，男性为奇数，女性为偶数
	 * @return 女性为0，男性为1
	 */
	public String getSex(){
		return (this.getSfzh().charAt(16)-'0')%2==0 ? "女" : "男";
	}

}