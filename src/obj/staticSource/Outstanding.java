package obj.staticSource;

import obj.*;

@SQLTable("Outstanding")
public class Outstanding extends Base{
	
	@SQLField(value="优秀实习生类别",weight=1,isKey=true,notNull=true)
	private String type;
	
	public String getType(){return this.type;}
	public void setType(String type){this.type=type==null||type.isEmpty()?null:type;}
	
	
	@Override
	public String getDescription(){
		return this.type;
	}
}