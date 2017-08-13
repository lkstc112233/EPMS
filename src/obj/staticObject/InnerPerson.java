package obj.staticObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import obj.*;
import persistence.DB;

@SQLTable("InnerPerson")
public class InnerPerson extends Base{
	@SQLField(isKey=true)
	private String id;			public String getId(){return id;}				public void setId(String a){this.id=a;}
	@SQLField(isKey=true)
	private String password;	public String getPassword(){return password;}	public void setPassword(String a){this.password=a;}
	@SQLField
	private String name;		public String getName(){return name;}			public void setName(String a){this.name=a;}
	@SQLField
	private String office;		public String getOffice(){return office;}		public void setOffice(String a){this.office=a;}
	@SQLField
	private String school;		public String getSchool(){return school;}		public void setSchool(String a){this.school=a;}
	@SQLField
	private boolean retire;		public boolean getRetire(){return retire;}		public void setRetire(boolean a){this.retire=a;}
	@SQLField
	private String phone;		public String getPhone(){return phone;}			public void setPhone(String a){this.phone=a;}
	@SQLField
	private String mobile;		public String getMobile(){return mobile;}		public void setMobile(String a){this.mobile=a;}
	@SQLField
	private String email;		public String getEmail(){return email;}			public void setEmail(String a){this.email=a;}
	@SQLField
	private boolean available;	public boolean getAvailable(){return available;}public void setAvailable(boolean a){this.available=a;}

	private final PreparedStatement sql_checkPassword;
	private final PreparedStatement sql_updatePassword;
	
	
	public InnerPerson() throws SQLException{
		super();
		sql_checkPassword=DB.con().prepareStatement(
				"SELECT password FROM "+this.getSQLTabelName()+" WHERE id = ? AND available = true");
		sql_updatePassword=DB.con().prepareStatement(
				"UPDATE "+this.getSQLTabelName()+" SET password = ? WHERE id = ? AND password = ? AND available = true");
	}
	
	public boolean checkPassword(){
		try {
			this.sql_checkPassword.setString(1,id);
			ResultSet rs=sql_checkPassword.executeQuery();
			rs.last();
			int num=rs.getRow();
			if(num==1){
				rs.first();
				String passwordFromSQL=rs.getString(1);
				System.out.println("SQL查询到密码值:"+passwordFromSQL);
				return password.equals(passwordFromSQL);
			}else{
				System.err.println("InnerPerson表有"+num+"重id值！("+this.sql_checkPassword.toString()+")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void updatePassword(String newPassword) throws SQLException{
		this.sql_updatePassword.setString(1,newPassword);
		this.sql_updatePassword.setString(2,id);
		this.sql_updatePassword.setString(3,password);
		int num=this.sql_updatePassword.executeUpdate();
		if(num!=1){
			System.err.println("InnerPerson表修改了"+num+"重password值！("+this.sql_updatePassword.toString()+")");
		}
		this.password=newPassword;
	}
	
}
