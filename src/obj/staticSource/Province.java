package obj.staticSource;

import obj.*;

@SQLTable("Province")
public class Province extends Base{
	
	@SQLField(value="省份名称",weight=1,isKey=true,notNull=true)
	private String name;
	
	public String getName(){return this.name;}
	public void setName(String a){this.name=Field.s2S(a);}
	
	public Province(String name){
		super();
		this.name=name;
	}
	public Province(){
		super();
	}
	
	
	@Override
	public String getDescription() {
		return this.name;
	}
}