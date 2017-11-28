package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("Province")
public class Province extends Base{
	@SQLField(value="顺序号",weight=0)
	private int orderId=-1;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
	@SQLField(value="省份名称",weight=1,isKey=true,notNull=true)
	private String name;
	
	public String getName(){return this.name;}
	public void setName(String a){this.name=Field.s2S(a);}
	
	public Province(){
		super();
	}
	public Province(String name) throws IllegalArgumentException, SQLException{
		this();
		try{
			for(Province a:Base.list(Province.class)) if(a.getName().equals(name)) {
				a.copyTo(this);
				return;
			}
		}catch(Exception e) {}
		this.name=name;
		this.load();
	}
	
	
	@Override
	public String getDescription() {
		return this.name;
	}
}