package obj.staticSource;


import obj.*;

@SQLTable("Major")
public class Major extends Base{
	
	@SQLField(value="专业名称",weight=1,isKey=true,notNull=true)
	private String name;
	@SQLField(value="所属学院",weight=10,isKey=true,notNull=true,source="School.name")
	private String school;

	public String getName(){return this.name;}
	public void setName(String name){this.name=name==null||name.isEmpty()?null:name;}
	public String getSchool(){return this.school;}
	public void setSchool(String school){this.school=school==null||school.isEmpty()?null:school;}
	
	@Override
	public String getDescription() {
		return this.getName();
	}
}