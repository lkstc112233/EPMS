package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("Outstanding")
public class Outstanding extends StaticSource{
	
	@SQLField(isKey=true)
	private String type;
	public String getType(){return this.type;}
	public void setType(String type){this.type=type;}
	
	public Outstanding() throws SQLException {
		super();
	}
}