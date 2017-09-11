package obj.staticObject;

import obj.*;

@SQLTable("PracticeBase")
public class PracticeBase extends Base{

	@SQLField(isKey=true,needImport=true,value="实习基地名称",description="")
	private String name;
	@SQLField(isKey=false,needImport=true,value="拒绝接收的民族",description="('汉族'),('蒙古族'),('回族'),('东乡族'),('维吾尔族'),('哈萨克族'),('土家族'),('藏族'),('壮族'),...")
	private String refuseNation;
	@SQLField(isKey=false,needImport=true,value="实习基地名称",description="")
	private String address;
	@SQLField
	private boolean available=true;
		public boolean getAvailable(){return available;}public void setAvailable(boolean a){this.available=a;}

	public String getName(){return name;}
	public void setName(String a){this.name=a;}
	public String getRefuseNation(){return refuseNation;}
	public void setRefuseNation(String a){this.refuseNation=a;}
	public String getAddress(){return address;}
	public void setAddress(String a){this.address=a;}
	
	
	public PracticeBase() throws java.sql.SQLException{
		super();
	}
		
}
