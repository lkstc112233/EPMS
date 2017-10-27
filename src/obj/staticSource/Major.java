package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("Major")
public class Major extends ListableBase{
	
	@SQLField(isKey=true)
	private String name;
	@SQLField(source="School.name")
	private String school;

	public String getName(){return this.name;}
	public void setName(String name){this.name=name==null||name.isEmpty()?null:name;}
	public String getSchool(){return this.school;}
	public void setSchool(String school){this.school=school==null||school.isEmpty()?null:school;}
	
	public Major() throws SQLException {
		super();
	}
}