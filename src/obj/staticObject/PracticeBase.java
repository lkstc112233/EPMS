package obj.staticObject;

import obj.*;

@SQLTable("PracticeBase")
public class PracticeBase extends ListableBase implements ListableBase.ListableBaseWithNoSave{

	@SQLField(isKey=true,needImport=true,value="实习基地名称")
	private String name;
	@SQLField(value="拒绝接收的民族",needImport=true,ps="('汉族'),('蒙古族'),('回族'),('东乡族'),('维吾尔族'),('哈萨克族'),('土家族'),('藏族'),('壮族'),...")
	private String refuseNation;
	@SQLField(value="所处城市",source="City.name",needImport=true)
	private String city;
	@SQLField(value="具体地址",needImport=true)
	private String address;
	@SQLField(value="账户名称",needImport=true)
	private String zhmc;
	@SQLField(value="开户行",needImport=true)
	private String khh;
	@SQLField(value="账户",needImport=true)
	private String zh;
	@SQLField(value="税务识别码",needImport=true,ps="2017年7月1日起实施")
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
	
	
	public PracticeBase() throws java.sql.SQLException{
		super();
	}
		
}
