package obj.staticObject;


import obj.*;

@SQLTable("OuterPerson")
public class OuterPerson extends Base implements Base.ListableWithNoSave{
	@SQLField(value="顺序号",weight=0)
	private int orderId=-1;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
	@SQLField(value="姓名",weight=2,isKey=true,notNull=true)
	private String name;
	@SQLField(value="校外人员职位",weight=10,notNull=true,source="OuterOffice.name")
	private String office;
	@SQLField(value="所在实习基地",weight=11,notNull=true,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="座机",weight=12)
	private String phone;
	@SQLField(value="手机",weight=13)
	private String mobile;
	@SQLField(value="邮箱",weight=14)
	private String email;
	

	public String getName(){return name;}
	public void setName(String a){this.name=a;}
	public String getOffice(){return office;}
	public void setOffice(String a){this.office=Field.s2S(a);}
	public String getPracticeBase(){return practiceBase;}
	public void setPracticeBase(String a){this.practiceBase=Field.s2S(a);}
	public String getPhone(){return phone;}
	public void setPhone(String a){this.phone=a;}
	public String getMobile(){return mobile;}
	public void setMobile(String a){this.mobile=a;}
	public String getEmail(){return email;}	
	public void setEmail(String a){this.email=a;}
	
	
	
	@Override
	public String getDescription() {
		return this.name+"("+this.practiceBase+")";
	}

	
	
}
