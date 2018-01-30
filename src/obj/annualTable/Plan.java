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
	private String refuseSex=null;
	@SQLField(value="是否携带数字媒体设备",weight=13)
	private boolean media=false;
	@SQLField(value="备注",weight=14,ps="文本储存")
	private String remark;

	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String practiceBase) {this.practiceBase = practiceBase==null||practiceBase.isEmpty()?null:practiceBase;}
	public String getMajor() {return major;}
	public void setMajor(String a) {this.major=Field.s2S(a);}
	public int getNumber() {return number;}
	public void setNumber(int number) {this.number = number;}
	public void setNumber(String a){this.number=Field.s2i(a,0);}
	public String getRefuseNation() {return refuseNation==null||refuseNation.isEmpty()?"":refuseNation;}
	public void setRefuseNation(String a) {this.refuseNation=Field.s2s(a,"");}
	public String getRefuseSex() {return refuseSex==null||refuseSex.isEmpty()?"":refuseSex;}
	public void setRefuseSex(String a) {this.refuseSex=Field.s2s(a,"");}
	public boolean getMedia() {return media;}
	public void setMedia(boolean media) {this.media = media;}
	public void setMedia(String a){this.media=Field.s2b(a,false);}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}
	
	
	
	
	@Override
	public String getDescription() {
		return this.getYear()+"-"+this.practiceBase+"_"+this.major+"_"+this.number;
	}
	
	
	/**
	 * 检查当前Plan是否能放入该学生，不能放入时抛出IllegalArgumentException
	 * 能放入时返回true
	 * 该函数不会返回false，只会抛出异常
	 */
	public boolean check(Student stu,int alreadyStudentCount) throws IllegalArgumentException{
		if(stu==null)
			throw new IllegalArgumentException("检测学生为空！");
		if(alreadyStudentCount>=this.getNumber())
			throw new IllegalArgumentException("人数超限！(已有"+this.getNumber()+"人)");
		if(this.getRefuseSex()!=null && this.getRefuseSex().contains(stu.getSex()))
			throw new IllegalArgumentException("实习学校("+this.getPracticeBase()+")拒绝接收性别("+stu.getSex()+")的学生！");
		if(this.getRefuseNation()!=null && this.getRefuseNation().contains(stu.getNation()))
			throw new IllegalArgumentException("规划中实习学校("+this.getPracticeBase()+")拒绝接收民族("+stu.getNation()+")的学生！");
		return true;
	}
	
	
	
		
}