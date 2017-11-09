package obj.staticObject;


import obj.*;

@SQLTable("OuterPerson")
public class OuterPerson extends Base implements Base.ListableWithNoSave{
	@SQLField(value="校外人员编号",weight=1,isKey=true,notNull=true)
	private String id;			public String getId(){return id;}				public void setId(String a){this.id=a==null||a.isEmpty()?null:a;}
	@SQLField(value="姓名",weight=2,isKey=true,notNull=true)
	private String name;		public String getName(){return name;}			public void setName(String a){this.name=a;}
	@SQLField(value="校外人员职位",weight=10,notNull=true,source="OuterOffice.name")
	private String office;		public String getOffice(){return office;}		public void setOffice(String a){this.office=a==null||a.isEmpty()?null:a;}
	@SQLField(value="所在实习基地",weight=11,notNull=true,source="PracticeBase.name")
	private String practiceBase;public String getPracticeBase(){return practiceBase;}		public void setPracticeBase(String a){this.practiceBase=a==null||a.isEmpty()?null:a;}
	@SQLField(value="座机",weight=12)
	private String phone;		public String getPhone(){return phone;}			public void setPhone(String a){this.phone=a;}
	@SQLField(value="手机",weight=13)
	private String mobile;		public String getMobile(){return mobile;}		public void setMobile(String a){this.mobile=a;}
	@SQLField(value="邮箱",weight=14)
	private String email;		public String getEmail(){return email;}			public void setEmail(String a){this.email=a;}
	
	
	@Override
	public String getDescription() {
		return this.name+"("+this.practiceBase+")";
	}

	
	
}
