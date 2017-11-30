package obj.staticObject;

import java.sql.*;

import action.Manager;
import obj.*;
import persistence.DB;

@SQLTable("InnerPerson")
public class InnerPerson extends Base implements Base.ListableWithNoSave{
	
	@SQLField(value="工号",weight=1,isKey=true,notNull=true)
	private String id;
	@SQLField(value="密码",weight=2,notNull=true)
	private String password;
	@SQLField(value="姓名",weight=3,notNull=true)
	private String name;
	@SQLField(value="校内人员类别",weight=10,notNull=true,source="InnerOffice.name")
	private String office;
	@SQLField(value="学院",weight=11,notNull=true,source="School.name")
	private String school;
	@SQLField(value="座机",weight=12)
	private String phone;
	@SQLField(value="手机",weight=13,notNull=true)
	private String mobile;
	@SQLField(value="邮箱",weight=14)
	private String email;
	
	
	public String getId(){return id;}
	public void setId(String a){this.id=Field.s2S(a);}
	public String getPassword(){return password;}
	public void setPassword(String a){this.password=Field.s2S(a);}
	public String getName(){return name;}
	public void setName(String a){this.name=a;}
	public String getOffice(){return office;}
	public void setOffice(String a){this.office=Field.s2s(a,"教师");}
	public String getSchool(){return school;}
	public void setSchool(String a){this.school=Field.s2S(a);}
	public String getPhone(){return phone;}
	public void setPhone(String a){this.phone=a;}
	public String getMobile(){return mobile;}
	public void setMobile(String a){this.mobile=a;}
	public String getEmail(){return email;}
	public void setEmail(String a){this.email=a;}
	
	
	static public final String UndefinedName="%未定%";
	

	public InnerPerson() {
		super();
	}
	public InnerPerson(String id) throws IllegalArgumentException, SQLException {
		this();
		try{
			for(InnerPerson a:Base.list(InnerPerson.class)) if(a.getId().equals(id)) {
				a.copyTo(this);
				return;
			}
		}catch(Exception e) {}
		this.setId(id);
		this.load();
	}
	
	
	public boolean checkPassword(){
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT password FROM ");
		sb.append(this.getSQLTableName());
		sb.append(" WHERE id = ?");
		try {
			PreparedStatement pst=DB.con().prepareStatement(sb.toString());
			pst.setString(1,id);
			ResultSet rs=pst.executeQuery();
			if(!rs.next()) return false;
			String passwordFromSQL=rs.getString(1);
			System.out.println("SQL查询到密码值:"+passwordFromSQL);
			return passwordFromSQL.equals(password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void updatePassword(String newPassword) throws IllegalArgumentException, SQLException{
		InnerPerson inner=new InnerPerson();
		this.copyTo(inner);
		inner.setPassword(newPassword);
		this.update(inner);
	}
	
	
	@Override
	/**
	 * load不会读取password，也不需要password
	 */
	public int load(boolean setFields) throws SQLException, IllegalArgumentException{
		if(!this.checkKeyField())
			throw new IllegalArgumentException("The key fields are not completed!");
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT ");
		boolean first=true;
		for(Field f:this.getFields()) if(!f.getName().equals("password")){
			if(first) first=false;
			else sb.append(",");
			sb.append(f.getName());
		}
		sb.append(" FROM ");
		sb.append(this.getSQLTableName());
		sb.append(" WHERE ");
		first=true;
		for(Field f:this.getFields()) if(f.isKey()){
			if(first) first=false;
			else sb.append(" AND ");
			sb.append(f.getName());
			sb.append(" = ?");
		}
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int SQLParameterIndex=1;
		for(Field f:this.getFields()) if(f.isKey())
			pst.setObject(SQLParameterIndex++,f.get(this));
		ResultSet rs=pst.executeQuery();
		rs.last();
		int num=rs.getRow();
		System.err.println("查询了"+num+"重值！("+pst.toString()+")");
		rs.first();
		if(num>0 && setFields){
			for(Field f:this.getFields()) if(!f.getName().equals("password"))
				if(!f.isKey())
					f.set(this,rs.getObject(f.getName()));
		}
		return num;
	}
	@Override
	/**
	 * update会需要原password正确
	 */
	public void update(Base base)throws SQLException, IllegalArgumentException{
		if(!this.checkPassword())
			throw new IllegalArgumentException("Password is NOT correct!");
		super.update(base);
	}
	@Override
	/**
	 * delete会需要password正确
	 */
	public void delete() throws IllegalArgumentException, SQLException{
		if(!this.checkPassword())
			throw new IllegalArgumentException("Password is NOT correct!");
		super.delete();
	}
	
	
	
	
	public boolean getIsSameSchool(){
		//TODO
		String user=Manager.getUser().getSchool();
		return "教务处".equals(user) || user.equals(this.school);
	}
	
	
	@Override
	public String getDescription() {
		return this.getName()+"("+this.getId()+")";
	}
}
