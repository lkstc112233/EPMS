package obj.staticSource;

import obj.*;

@SQLTable("ZZMM")
public class ZZMM extends Base{
	@SQLField(value="顺序号",weight=0)
	private int orderId;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
	@SQLField(value="政治面貌",weight=1,isKey=true,notNull=true)
	private String name;
	
	public String getName(){return this.name;}
	public void setName(String a){this.name=Field.s2S(a);}
	
	
	
	@Override
	public String getDescription() {
		return this.name;
	}
	

	
}