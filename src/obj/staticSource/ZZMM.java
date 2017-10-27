package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("ZZMM")
public class ZZMM extends ListableBase{
	
	@SQLField(isKey=true)
	private String name;
	public String getName(){return this.name;}
	public void setName(String name){this.name=name==null||name.isEmpty()?null:name;}
	
	public ZZMM() throws SQLException {
		super();
	}
}