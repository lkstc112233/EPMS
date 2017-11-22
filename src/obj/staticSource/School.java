package obj.staticSource;

import obj.*;

@SQLTable("School")
public class School extends Base{
	@SQLField(value="顺序号",weight=0)
	private int orderId=-1;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
	@SQLField(value="学院名称",weight=1,isKey=true,notNull=true)
	private String name;
	
	public String getName(){return this.name;}
	public void setName(String a){this.name=Field.s2S(a);}
	
	public School(String name){
		super();
		this.setName(name);
	}
	public School(){
		super();
	}
	
	@Override
	public String getDescription() {
		return this.name;
	}
	
	
}