package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("InnerOffice")
public class InnerOffice extends ListableBase{
	
	@SQLField(isKey=true)
	private String name;
	public String getName(){return this.name;}
	public void setName(String name){this.name=name;}
	
	public InnerOffice() throws SQLException {
		super();
	}
	
}