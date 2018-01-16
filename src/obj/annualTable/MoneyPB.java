package obj.annualTable;

import java.sql.SQLException;

import obj.*;

@SQLTable("MoneyPB")
public class MoneyPB extends AnnualBase{
	@SQLField(value="序号",isKey=true,notNull=true,autoIncrease=true,weight=0)
	private int id;	public void setOrderId(int a){this.id=a;}	public void setOrderId(String a) {this.id=Field.s2i(a,-1);}	public int getOrderId() {return this.id;}
	
	@SQLField(value="实习基地",weight=1,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="原任课教师指导实习酬金",weight=10)
	private float rkjs;
	@SQLField(value="原班主任指导实习酬金",weight=11)
	private float bzr;
	@SQLField(value="有关干部酬金",weight=12)
	private float gbcj;
	@SQLField(value="实验员协助工作酬金",weight=13)
	private float syy;
	@SQLField(value="教育实习管理费",weight=14)
	private float glf;
	@SQLField(value="住宿费",weight=15)
	private float zsf;
	@SQLField(value="被褥费",weight=16)
	private float brf;
	@SQLField(value="其他",weight=17)
	private float moneyElse;
	@SQLField(value="备注",weight=20,ps="文本储存")
	private String remark;
	
	static private final int[] TypeList=new int[]{0,1,2};
		static public int[] getTypeList(){return MoneyPB.TypeList;}
	static private final String[] TypeNameList=new String[] {"入校督导","中期督导","返校督导"};
		static public String[] getTypeNameList() {return MoneyPB.TypeNameList;}

	public float getSum() {
		return this.rkjs+this.bzr+this.gbcj+this.syy+this.glf+this.zsf+this.brf+this.moneyElse;
	}
	public void appendSum(MoneyPB m) {
		this.rkjs+=m.rkjs;
		this.bzr+=m.bzr;
		this.gbcj+=m.gbcj;
		this.syy+=m.syy;
		this.glf+=m.glf;
		this.zsf+=m.zsf;
		this.brf+=m.brf;
		this.moneyElse+=m.moneyElse;
	}
	public void multiply(int studentCount,int syyCount,boolean accommodation) {
		this.rkjs*=studentCount;
		this.bzr*=studentCount;
		this.gbcj*=studentCount;
		this.syy*=syyCount;
		this.glf*=studentCount;
		this.zsf*=accommodation?0:studentCount;
		this.brf*=accommodation?0:studentCount;
		this.moneyElse*=studentCount;
	}
	static public MoneyPB[] getMoneyPBBase() throws IllegalArgumentException, InstantiationException, SQLException {
		MoneyPB[] base=new MoneyPB[2];
		for(int i=0;i<base.length;i++) 
			base[i]=Base.list(MoneyPB.class,new Restraint(Field.getField(MoneyPB.class,"year"),
					i)).get(0);
		return base;
	}
		
	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String a) {this.practiceBase=Field.s2S(a);}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}

	public float getRkjs() {return rkjs;}
	public void setRkjs(float a) {this.rkjs = a;}
	public void setRkjs(String a){this.rkjs=Field.s2f(a,0);}
	public float getBzr() {return bzr;}
	public void setBzr(float a) {this.bzr = a;}
	public void setBzr(String a){this.bzr=Field.s2f(a,0);}
	public float getGbcj() {return gbcj;}
	public void setGbcj(float a) {this.gbcj = a;}
	public void setGbcj(String a){this.gbcj=Field.s2f(a,0);}
	public float getSyy() {return syy;}
	public void setSyy(float a) {this.syy = a;}
	public void setSyy(String a){this.syy=Field.s2f(a,0);}
	public float getGlf() {return glf;}
	public void setGlf(float a) {this.glf = a;}
	public void setGlf(String a){this.glf=Field.s2f(a,0);}
	public float getZsf() {return zsf;}
	public void setZsf(float a) {this.zsf = a;}
	public void setZsf(String a){this.zsf=Field.s2f(a,0);}
	public float getBrf() {return brf;}
	public void setBrf(float a) {this.brf = a;}
	public void setBrf(String a){this.brf=Field.s2f(a,0);}
	public float getMoneyElse() {return moneyElse;}
	public void setMoneyElse(float a) {this.moneyElse = a;}
	public void setMoneyElse(String a){this.moneyElse=Field.s2f(a,0);}



	@Override
	public String getDescription() {
		return this.getYear()+"-"+this.practiceBase;
	}
	
	
	
	
	
}