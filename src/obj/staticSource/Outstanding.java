package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("Outstanding")
public class Outstanding extends Base{
	@SQLField(value="顺序号",weight=0)
	private int orderId=-1;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
	@SQLField(value="优秀实习生类别",weight=1,isKey=true,notNull=true)
	private String type;
	
	public String getType(){return this.type;}
	public void setType(String a){this.type=Field.s2S(a);}
	
	
	public Outstanding() {
		super();
	}
	public Outstanding(String type) throws IllegalArgumentException, SQLException {
		this();
		try{
			for(Outstanding a:Base.list(Outstanding.class)) if(a.getType().equals(type)) {
				a.copyTo(this);
				return;
			}
		}catch(Exception e) {}
		this.setType(type);
		this.load();
	}
	
	
	@Override
	public String getDescription(){
		return this.type;
	}
}