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
	@SQLField(value="回生源地实习意向",weight=19,ps="是否希望回生源地进行教育实习")
	private boolean hxyx=true;
	@SQLField(value="学科小组长",weight=20,ps="只代表部院系推荐意见")
	private boolean recommend=false;
	@SQLField(value="指导老师工号",weight=100,source="InnerPerson.id",ps="指导老师需要属于校内人员列表")
	private String teacherId;
	@SQLField(value="优秀实习生类别",weight=101,autoInit=true,source="Outstanding.type")
	private String outstandingType;
	@SQLField(value="优秀实习生材料",weight=102,autoInit=true,ps="二进制储存")
	private byte[] outstandingMaterial;
	
	public String getId() {return id;}
	public void setId(String a) {this.id=Field.s2S(a);}
	public String getName() {return name;}
	public void setName(String name) {this.name=name;}
	public String getMobile() {return mobile;}
	public void setMobile(String mobile) {this.mobile = mobile;}
	public String getEmail() {return email;}
	public void setEmail(String email) {this.email = email;}
	public String getMajor() {return major;}
	public void setMajor(String a) {this.major=Field.s2S(a);}
	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String a) {this.practiceBase=Field.s2S(a);}
	public String getSfzh() {return sfzh;}
	public void setSfzh(String sfzh) {this.sfzh = sfzh;}
	public String getZzmm() {return zzmm;}
	public void setZzmm(String a) {this.zzmm=Field.s2S(a);}
	public String getProvince() {return province;}
	public void setProvince(String a) {this.province=Field.s2S(a);}
	public String getNation() {return nation;}
	public void setNation(String a) {this.nation=Field.s2S(a);}
	public boolean getHxyx() {return hxyx;}
	public void setHxyx(boolean a) {this.hxyx = a;}
	public void setHxyx(String a){this.hxyx=Field.s2b(a,true);}
	public boolean getRecommend() {return recommend;}
	public void setRecommend(boolean a) {this.recommend = a;}
	public void setRecommend(String a){this.recommend=Field.s2b(a,false);}
	public String getTeacherId() {return teacherId;}
	public void setTeacherId(String a) {this.teacherId=Field.s2s(a,null);}
	public String getOutstandingType() {return outstandingType;}
	public void setOutstandingType(String a) {this.outstandingType=Field.s2s(a,null);}
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