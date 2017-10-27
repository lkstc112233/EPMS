package obj.staticObject;

import java.sql.SQLException;

import obj.*;

@SQLTable("OuterPerson")
public class OuterPerson extends ListableBase implements ListableBase.ListableBaseWithNoSave{
	@SQLField(value="校外人员编号",isKey=true)
	private String id;			public String getId(){return id;}				public void setId(String a){this.id=a==null||a.isEmpty()?null:a;}
	@SQLField(value="姓名",needImport=true)
	private String name;		public String getName(){return name;}			public void setName(String a){this.name=a;}
	@SQLField(value="校外人员职位",source="OuterOffice.name",needImport=true)
	private String office;		public String getOffice(){return office;}		public void setOffice(String a){this.office=a==null||a.isEmpty()?null:a;}
	@SQLField(value="所在实习基地",source="PracticeBase.name",needImport=true)
	private String practiceBase;public String getPracticeBase(){return practiceBase;}		public void setPracticeBase(String a){this.practiceBase=a==null||a.isEmpty()?null:a;}
	@SQLField(value="座机",needImport=true)
	private String phone;		public String getPhone(){return phone;}			public void setPhone(String a){this.phone=a;}
	@SQLField(value="手机",needImport=true)
	private String mobile;		public String getMobile(){return mobile;}		public void setMobile(String a){this.mobile=a;}
	@SQLField(value="邮箱",needImport=true)
	private String email;		public String getEmail(){return email;}			public void setEmail(String a){this.email=a;}

	
	
	public OuterPerson() throws SQLException{
		super();
		
	}
}
