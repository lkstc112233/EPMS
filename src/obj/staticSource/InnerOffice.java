package obj.staticSource;


import java.sql.SQLException;

import obj.*;

@SQLTable("InnerOffice")
public class InnerOffice extends Base{
	@SQLField(value="顺序号",weight=0)
	private int orderId=-1;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
	@SQLField(value="校内人员类别",weight=1,isKey=true,notNull=true)
	private String name;
	@SQLField(value="是否属于部院系分支",weight=10,notNull=true)
	private boolean isSchool;
	
	public String getName(){return this.name;}
	public void setName(String a){this.name=Field.s2S(a);}
	public boolean getIsSchool() {return this.isSchool;}
	public void setIsSchool(boolean a) {this.isSchool=a;}
	public void setIsSchool(String a) {this.isSchool=Field.s2b(a,true);}
	

	public InnerOffice() {
		super();
	}
	public InnerOffice(String name) throws IllegalArgumentException, SQLException {
		super();
		this.setName(name);
		this.load();
	}
	
	
	@Override
	public String getDescription() {
		return this.name;
	}
	
}