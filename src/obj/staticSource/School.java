package obj.staticSource;

import java.sql.SQLException;

import obj.*;

@SQLTable("School")
public class School extends Base{
	@SQLField(value="顺序号",weight=0)
	private int orderId=-1;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}

	@SQLField(value="学院名称",weight=1,isKey=true,notNull=true)
	private String name;
	@SQLField(value="简称",weight=10,notNull=true)
	private String subName="";//TODO

	public String getName(){return this.name;}
	public void setName(String a){this.name=Field.s2S(a);}
	public String getSubName(){return this.subName;}
	public void setSubName(String a){this.subName=Field.s2S(a);}
	
	public School(){
		super();
	}
	public School(String name) throws IllegalArgumentException, SQLException{
		this();
		try{
			for(School a:Base.list(School.class)) if(a.getName().equals(name)) {
				a.copyTo(this);
				return;
			}
		}catch(Exception e) {}
		this.setName(name);
		this.load();
	}
	
	@Override
	public String getDescription() {
		return this.name;
	}
	
	
}