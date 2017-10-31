package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("School")
public class School extends ListableBase{
	
	@SQLField(isKey=true)
	private String name;
	public String getName(){return this.name;}
	public void setName(String name){this.name=name==null||name.isEmpty()?null:name;}
	
	public School() throws SQLException {
		super();
	}
	
	public School(String name) throws SQLException{
		super();
		this.setName(name);
	}
}