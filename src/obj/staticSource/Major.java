package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("Major")
public class Major extends StaticSource{
	
	@SQLField(isKey=true)
	private String name;
	@SQLField
	private String school;

	public String getName(){return this.name;}
	public void setName(String name){this.name=name;}
	public String getSchool(){return this.school;}
	public void setSchool(String school){this.school=school;}
	
	public Major() throws SQLException {
		super();
	}
}