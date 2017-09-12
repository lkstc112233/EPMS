package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("Nation")
public class Nation extends ListableBase{
	
	@SQLField(isKey=true)
	private String name;
	public String getName(){return this.name;}
	public void setName(String name){this.name=name;}
	
	public Nation() throws SQLException {
		super();
	}
}