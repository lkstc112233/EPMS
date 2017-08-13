package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("ZZMM")
public class ZZMM extends StaticSource{
	
	@SQLField(isKey=true)
	private String name;
	public String getName(){return this.name;}
	public void setName(String name){this.name=name;}
	
	public ZZMM() throws SQLException {
		super();
	}
}