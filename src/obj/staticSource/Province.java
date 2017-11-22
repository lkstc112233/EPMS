package obj.staticSource;

import obj.*;

@SQLTable("Province")
public class Province extends Base{
	@SQLField(value="顺序号",weight=0)
	private int orderId=-1;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
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