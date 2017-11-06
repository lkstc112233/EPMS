package obj.staticObject;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import action.Manager;
import obj.*;
import persistence.DB;

@SQLTable("InnerPerson")
public class InnerPerson extends ListableBase implements ListableBase.ListableBaseWithNoSave{
	@SQLField(value="工号",isKey=true,needImport=true)
	private String id;			public String getId(){return id;}				public void setId(String a){this.id=a==null||a.isEmpty()?null:a;}
	@SQLField(value="密码",isKey=true,needImport=true)
	private String password;	public String getPassword(){return password;}	public void setPassword(String a){this.password=a==null||a.isEmpty()?null:a;}
	@SQLField(value="姓名",needImport=true)
	private String name;		public String getName(){return name;}			public void setName(String a){this.name=a;}
	@SQLField(value="校内人员类别",source="InnerOffice.name",needImport=true)
	private String office;		public String getOffice(){return office;}		public void setOffice(String a){this.office=a==null||a.isEmpty()?null:a;}
	@SQLField(value="学院",source="School.name",needImport=true)
	private String school;		public String getSchool(){return school;}		public void setSchool(String a){this.school=a==null||a.isEmpty()?null:a;}
	@SQLField(value="座机",needImport=true)
	private String phone;		public String getPhone(){return phone;}			public void setPhone(String a){this.phone=a;}
	@SQLField(value="手机",needImport=true)
	private String mobile;		public String getMobile(){return mobile;}		public void setMobile(String a){this.mobile=a;}
	@SQLField(value="邮箱",needImport=true)
	private String email;		public String getEmail(){return email;}			public void setEmail(String a){this.email=a;}

	private final PreparedStatement sql_checkPassword;
	private final PreparedStatement sql_updatePassword;
	
	
	public InnerPerson() throws SQLException{
		super();
		sql_checkPassword=DB.con().prepareStatement(
				"SELECT password FROM "+this.getSQLTabelName()+" WHERE id = ?");
		sql_updatePassword=DB.con().prepareStatement(
				"UPDATE "+this.getSQLTabelName()+" SET password = ? WHERE id = ? AND password = ?");
	}
	public InnerPerson(String id) throws IllegalArgumentException, IllegalAccessException, SQLException{
		this();
		this.setId(id);
		this.load();
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
				return passwordFromSQL.equals(password);
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
	
	@Override
	public int load(boolean setFields) throws SQLException, IllegalArgumentException, IllegalAccessException{
		if(this.getId()==null||this.getId().isEmpty())
			throw new IllegalArgumentException("The key fields are not completed!");
		Class<? extends Base> clazz=this.getClass();
		java.util.List<Field> fs=this.getFields();
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT ");
		boolean first=true;
		for(Field f:fs){
			if(f.getName().equals("password")) continue;
			f.setAccessible(true);
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
		}
		sb.append(" FROM ");
		sb.append(Base.getSQLTableName(clazz));
		sb.append(" WHERE id = ?");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		pst.setObject(1,this.getId());
		ResultSet rs=pst.executeQuery();
		rs.last();
		int num=rs.getRow();
		if(num!=1)
			System.err.println("查询到"+num+"重值！("+pst.toString()+")");
		rs.first();
		if(num>0 && setFields){
			for(Field f:this.getFields()){
				if(f.getName().equals("password")) continue;
				SQLField s=f.getAnnotation(SQLField.class);
				if(s==null) continue;
				f.setAccessible(true);
				if(!s.isKey())
					f.set(this,rs.getObject(f.getName()));
			}
			this.setPassword(null);
		}
		return num;
	}
	
	public boolean getIsSameSchool(){
		//TODO
		String user=Manager.getUser().getSchool();
		return "教务处".equals(user) || user.equals(this.school);
	}
}
