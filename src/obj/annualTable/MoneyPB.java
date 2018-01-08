package obj.annualTable;

import obj.*;

@SQLTable("MoneyPB")
public class MoneyPB extends AnnualBase{
	@SQLField(value="实习基地",weight=1,isKey=true,notNull=true,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="备注",weight=12,ps="文本储存")
	private String remark;
	@SQLField(value="原任课教师指导实习酬金",weight=13)
	private float rkjs;
	@SQLField(value="原班主任指导实习酬金",weight=14)
	private float bzr;
	@SQLField(value="有关干部酬金",weight=15)
	private float gbcj;
	@SQLField(value="实验员协助工作酬金",weight=16)
	private float syy;
	@SQLField(value="教育实习管理费",weight=17)
	private float glf;
	@SQLField(value="住宿费",weight=18)
	private float zsf;
	@SQLField(value="被褥费",weight=19)
	private float brf;
	@SQLField(value="其他",weight=20)
	private float moneyElse;
	
	static private final int[] TypeList=new int[]{0,1,2};
		static public int[] getTypeList(){return MoneyPB.TypeList;}
	static private final String[] TypeNameList=new String[] {"入校督导","中期督导","返校督导"};
		static public String[] getTypeNameList() {return MoneyPB.TypeNameList;}

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