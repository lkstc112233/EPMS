package obj.annualTable;

import java.sql.SQLException;

import obj.*;

/**
 * MoneyPB		是给实习基地经费中针对不同实习基地产生不同住宿费、被褥费等费用的可持久化数据类型
 * MoneyMajor	是给部院系经费中的机动经费
 * MoneyMajorPB	是给部院系经费中针对不同实习基地产生不同补助标准的可持久化数据类型
 */
@SQLTable("MoneyPB")
public class MoneyMajorPB extends AnnualBase{
	@SQLField(value="序号",isKey=true,notNull=true,autoIncrease=true,weight=0)
	private int id;	public void setOrderId(int a){this.id=a;}	public void setOrderId(String a) {this.id=Field.s2i(a,-1);}	public int getOrderId() {return this.id;}

	@SQLField(value="实习基地",weight=1,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="误餐补助",weight=10)
	private float wcbz;
	@SQLField(value="交通补助",weight=11)
	private float jtbz;
	@SQLField(value="火车票补助",weight=12)
	private float train;
	@SQLField(value="实习教材及复印费",weight=13)
	private float jcfy;
	@SQLField(value="部院系机动经费",weight=14)
	private float moneyElse;
	@SQLField(value="备注",weight=20,ps="文本储存")
	private String remark;
	
	public float getSum() {
		return this.wcbz+this.jtbz+this.train+this.jcfy+this.moneyElse;
	}
	public void appendSum(MoneyMajorPB m) {
		this.wcbz+=m.wcbz;
		this.jtbz+=m.jtbz;
		this.train+=m.train;
		this.jcfy+=m.jcfy;
		this.moneyElse+=m.moneyElse;
	}
	public void multiply(int studentCount) {
		this.wcbz*=studentCount;
		this.jtbz*=studentCount;
		this.train*=studentCount;
		this.jcfy*=studentCount;
		this.moneyElse*=studentCount;
	}
	static public MoneyMajorPB[] getMoneyPBBase() throws IllegalArgumentException, InstantiationException, SQLException {
		MoneyMajorPB[] base=new MoneyMajorPB[2];
		for(int i=0;i<base.length;i++) 
			base[i]=Base.list(MoneyMajorPB.class,new Restraint(Field.getField(MoneyMajorPB.class,"year"),
					i)).get(0);
		return base;
	}

	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String a) {this.practiceBase=Field.s2S(a);}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}

	public float getWcbz() {return wcbz;}
	public void setWcbz(float a) {this.wcbz = a;}
	public void setWcbz(String a){this.wcbz=Field.s2f(a,0);}
	public float getJtbz() {return jtbz;}
	public void setJtbz(float a) {this.jtbz = a;}
	public void setJtbz(String a){this.jtbz=Field.s2f(a,0);}
	public float getTrain() {return train;}
	public void setTrain(float a) {this.train = a;}
	public void setTrain(String a){this.train=Field.s2f(a,0);}
	public float getJcfy() {return jcfy;}
	public void setJcfy(float a) {this.jcfy = a;}
	public void setJcfy(String a){this.jcfy=Field.s2f(a,0);}
	public float getMoneyElse() {return moneyElse;}
	public void setMoneyElse(float a) {this.moneyElse = a;}
	public void setMoneyElse(String a){this.moneyElse=Field.s2f(a,0);}



	@Override
	public String getDescription() {
		return this.getYear()+"-"+this.practiceBase;
	}
	
	
	
	
	
}