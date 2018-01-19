package obj.annualTable;

import obj.*;

/**
 * MoneyPB		是给实习基地经费中针对不同实习基地产生不同住宿费、被褥费等费用的可持久化数据类型
 * MoneyMajor	是给部院系经费中的机动经费
 * MoneyMajorPB	是给部院系经费中针对不同实习基地产生不同补助标准的可持久化数据类型
 */
@SQLTable("MoneyPB")
public class MoneyMajor extends AnnualBase{
	@SQLField(value="序号",isKey=true,notNull=true,autoIncrease=true,weight=0)
	private int id;	public void setOrderId(int a){this.id=a;}	public void setOrderId(String a) {this.id=Field.s2i(a,-1);}	public int getOrderId() {return this.id;}
	
	@SQLField(value="专业",weight=1,source="Major.name")
	private String major;
	@SQLField(value="部院系机动经费",weight=14)
	private float moneyElse;
	@SQLField(value="备注",weight=20,ps="文本储存")
	private String remark;
	
	public float getSum() {
		return this.moneyElse;
	}
		
	public String getMajor() {return major;}
	public void setMajor(String a) {this.major=Field.s2S(a);}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}

	public float getMoneyElse() {return moneyElse;}
	public void setMoneyElse(float a) {this.moneyElse = a;}
	public void setMoneyElse(String a){this.moneyElse=Field.s2f(a,0);}



	@Override
	public String getDescription() {
		return this.getYear()+"-"+this.major;
	}
	
	
	
	
	
}