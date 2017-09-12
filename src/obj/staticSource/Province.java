package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("Province")
public class Province extends ListableBase{
	
	@SQLField(isKey=true)
	private String name;
	public String getName(){return this.name;}
	public void setName(String name){this.name=name;}
	
	public Province() throws SQLException {
		super();
	}
}