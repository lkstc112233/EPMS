package obj.staticSource;


import obj.*;

@SQLTable("InnerOffice")
public class InnerOffice extends Base{
	@SQLField(value="顺序号",weight=0)
	private int orderId;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
	@SQLField(value="校内人员类别",weight=1,isKey=true,notNull=true)
	private String name;
	
	public String getName(){return this.name;}
	public void setName(String a){this.name=Field.s2S(a);}
	
	
	@Override
	public String getDescription() {
		return this.name;
	}
	
}