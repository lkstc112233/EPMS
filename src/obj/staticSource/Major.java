
package obj.staticSource;


import java.sql.SQLException;

import obj.*;

@SQLTable("Major")
public class Major extends Base{
	@SQLField(value="顺序号",weight=0)
	private int orderId;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
	@SQLField(value="专业名称",weight=1,isKey=true,notNull=true)
	private String name;
	@SQLField(value="学科",weight=2,ps="科目，用于简化显示，只能存放64长度，建议2-4个字")
	private String subject;
	@SQLField(value="所属学院",weight=10,isKey=true,notNull=true,source="School.name")
	private String school;

	public String getName(){return this.name;}
	public void setName(String a){this.name=Field.s2S(a);}
	public String getSubject(){return this.subject;}
	public void setSubject(String a){this.subject=Field.s2S(a);}
	public String getSchool(){return this.school;}
	public void setSchool(String a){this.school=Field.s2S(a);}
	
	
	public Major() {
		super();
	}
	
	@Override
	public String getDescription() {
		return this.getSubject();
	}
	
	public Major(String name) throws IllegalArgumentException, SQLException{
		super();
		this.setName(name);
		this.load();
	}
}