package obj.staticObject;

import java.sql.SQLException;

import obj.*;
import obj.annualTable.Student;

@SQLTable("PracticeBase")
public class PracticeBase extends Base implements Base.ListableWithNoSave{

	@SQLField(value="顺序号",weight=0)
	private int orderId=-1;	public void setOrderId(int a){this.orderId=a;}	public void setOrderId(String a) {this.orderId=Field.s2i(a,-1);}	public int getOrderId() {return this.orderId;}
	
	@SQLField(value="实习基地名称",weight=1,isKey=true,notNull=true)
	private String name;
	@SQLField(value="所处城市",weight=2,notNull=true,source="City.name")
	private String city;
	@SQLField(value="拒绝接收的民族",weight=10,ps="('汉族'),('蒙古族'),('回族'),('东乡族'),('维吾尔族'),('哈萨克族'),('土家族'),('藏族'),('壮族'),...")
	private String refuseNation;
	@SQLField(value="具体地址",weight=11)
	private String address;
	@SQLField(value="账户名称",weight=12)
	private String zhmc;
	@SQLField(value="开户行",weight=13)
	private String khh;
	@SQLField(value="账户",weight=14)
	private String zh;
	@SQLField(value="税务识别码",weight=15,ps="2017年7月1日起实施")
	private String swsbm;

	public String getName(){return name;}
	public void setName(String a){this.name=a;}
	public String getCity(){return city==null||city.isEmpty()?null:city;}
	public void setCity(String a){this.city=a;}
	public String getRefuseNation(){return refuseNation;}
	public void setRefuseNation(String a){this.refuseNation=a;}
	public String getAddress(){return address;}
	public void setAddress(String a){this.address=a;}
	public String getZhmc() {return zhmc;}
	public void setZhmc(String zhmc) {this.zhmc = zhmc;}
	public String getKhh() {return khh;}
	public void setKhh(String khh) {this.khh = khh;}
	public String getZh() {return zh;}
	public void setZh(String zh) {this.zh = zh;}
	public String getSwsbm() {return swsbm;}
	public void setSwsbm(String swsbm) {this.swsbm = swsbm;}

	
	@Override
	public String getDescription() {
		return this.name;
	}
	
	
	
	public PracticeBase() {
		super();
	}
	public PracticeBase(String name) throws IllegalArgumentException, SQLException {
		super();
		this.setName(name);
		this.load();
	}
	
	

	/**
	 * 检查当前Plan是否能放入该学生，不能放入时抛出IllegalArgumentException
	 * 能放入时返回true
	 * 该函数不会返回false，只会抛出异常
	 */
	public boolean check(Student stu) throws IllegalArgumentException{
		if(stu==null)
			throw new IllegalArgumentException("检测学生为空！");
		if(this.getRefuseNation()!=null && this.getRefuseNation().contains(stu.getNation()))
			throw new IllegalArgumentException("实习学校("+this.getName()+")拒绝接收民族("+stu.getNation()+")的学生！");
		return true;
	}
	
		
}
