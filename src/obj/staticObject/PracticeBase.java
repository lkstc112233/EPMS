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
	@SQLField(value="所处城市",weight=2,notNull=true,source="Province.name")
	private String province;
	@SQLField(value="回生源地实习大区",weight=3,notNull=true)
	private boolean hx=true;
	@SQLField(value="拒绝接收的民族",weight=10,notNull=true,ps="('汉族'),('蒙古族'),('回族'),('东乡族'),('维吾尔族'),('哈萨克族'),('土家族'),('藏族'),('壮族'),...")
	private String refuseNation="";
	@SQLField(value="具体地址",weight=11,notNull=true)
	private String address="";
	@SQLField(value="是否非签约基地",weight=12,notNull=true)
	private boolean status=false;
	@SQLField(value="账户名称",weight=22,notNull=true)
	private String zhmc="";
	@SQLField(value="开户行",weight=23,notNull=true)
	private String khh="";
	@SQLField(value="账户",weight=24,notNull=true)
	private String zh="";
	@SQLField(value="税务识别码",weight=25,notNull=true,ps="2017年7月1日起实施")
	private String swsbm="";
	@SQLField(value="备注",weight=1001,ps="文本储存")
	private String remark;

	public String getName(){return name;}
	public void setName(String a){this.name=a;}
	public String getProvince(){return province==null||province.isEmpty()?null:province;}
	public void setProvince(String a){this.province=Field.s2s(a,"");}
	public boolean getHx() {return this.hx;}
	public void setHx(boolean a) {this.hx=a;}
	public void setHx(String a) {this.hx=Field.s2b(a,true);}
	public String getRefuseNation(){return refuseNation==null||refuseNation.isEmpty()?"":refuseNation;}
	public void setRefuseNation(String a){this.refuseNation=Field.s2s(a,"");}
	public String getAddress(){return address;}
	public void setAddress(String a){this.address=Field.s2s(a,"");}
	public boolean getStatus() {return this.status;}
	public void setStatus(boolean a) {this.status=a;}
	public void setStatus(String a) {this.status=Field.s2b(a,false);}
	public String getZhmc() {return zhmc;}
	public void setZhmc(String a) {this.zhmc=Field.s2s(a,"");}
	public String getKhh() {return khh;}
	public void setKhh(String a) {this.khh=Field.s2s(a,"");}
	public String getZh() {return zh;}
	public void setZh(String a) {this.zh=Field.s2s(a,"");}
	public String getSwsbm() {return swsbm;}
	public void setSwsbm(String a) {this.swsbm=Field.s2s(a,"");}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}

	
	@Override
	public String getDescription() {
		return this.name;
	}
	
	
	
	public PracticeBase() {
		super();
	}
	public PracticeBase(String name) throws IllegalArgumentException, SQLException {
		this();
		try {
			for(PracticeBase a:Base.list(PracticeBase.class)) if(a.getName().equals(name)){
				a.copyTo(this);
				return;
			}
		}catch(Exception e) {}
		this.setName(name);
		this.load();
	}
	
	

	/**
	 * 检查当前PracticeBase是否能放入该学生，不能放入时抛出IllegalArgumentException
	 * 能放入时返回true
	 * 该函数不会返回false，只会抛出异常
	 */
	public boolean check(Student stu) throws IllegalArgumentException{
		if(stu==null)
			throw new IllegalArgumentException("检测学生为空！");
		if(this.getRefuseNation()!=null && this.getRefuseNation().contains(stu.getNation()))
			throw new IllegalArgumentException("实习学校("+this.getName()+")拒绝接收民族("+stu.getNation()+")的学生！");
		if(this.getHx() && !stu.getHxyx())
			throw new IllegalArgumentException("实习学校("+this.getName()+")拒绝接收不想回生源地实习的学生！");
		if(!this.getHx() && stu.getHxyx())
			throw new IllegalArgumentException("实习学校("+this.getName()+")拒绝接收想回生源地实习的学生！");
		if(this.getHx() && stu.getHxyx())
			if(!this.getProvince().equals(stu.getProvince()))
				throw new IllegalArgumentException("实习学校("+this.getName()+")位于"+this.getProvince()+"拒绝接收生源地"+stu.getProvince()+"的学生！");
		return true;
	}
	
		
}
