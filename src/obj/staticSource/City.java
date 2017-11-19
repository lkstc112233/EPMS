package obj.staticSource;

import obj.*;

@SQLTable("City")
public class City extends Base{
	
	@SQLField(value="城市名称",weight=1,isKey=true,notNull=true)
	private String name;
	@SQLField(value="所属省份",weight=10,isKey=true,notNull=true,source="Province.name")
	private String province;

	public String getName(){return this.name;}
	public void setName(String a){this.name=Field.s2S(a);}
	public String getProvince(){return this.province;}
	public void setProvince(String a){this.province=Field.s2S(a);}
	
	
	@Override
	public String getDescription() {
		return this.name;
	}
}