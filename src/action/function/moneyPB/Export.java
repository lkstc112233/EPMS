package action.function.moneyPB;

import java.sql.*;

import action.*;
import obj.annualTable.*;

/**
 * 经费-教育实习基地经费
 */
public class Export extends Action{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}


	private ListOfPracticeBaseAndMoney practiceBaseAndMoney;
	
	public ListOfPracticeBaseAndMoney getPracticeBaseAndMoney(){return this.practiceBaseAndMoney;}
	
	//记忆化部件
	public MoneyPB getMoneyPB() {return new MoneyPB();}

	static public final String SessionListKey="moneyPB_Export_List";
	
	public Export(){
		super();
		this.practiceBaseAndMoney=Manager.loadSession(ListOfPracticeBaseAndMoney.class,SessionListKey);
	}
	
	/**
	 * 用于显示
	 */
	public String display(){
		System.out.println(">> Export:display > year="+this.getAnnual().getYear());
		this.practiceBaseAndMoney=null;
		try{
			this.practiceBaseAndMoney=new ListOfPracticeBaseAndMoney(
					this.getAnnual().getYear());
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return this.returnWithTips(NONE,"数据库开小差去了！",e);
		}
		Manager.saveSession(SessionListKey,this.practiceBaseAndMoney);
		return NONE;
	}
	
	
	
	private String practiceBaseName;
		public void setPracticeBaseName(String a) {this.practiceBaseName=a;}
		public String getPracticeBaseName() {return this.practiceBaseName;}
		
	@Override
	public String execute(){
		//保存Region信息：moneyBack
		ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair=
				this.practiceBaseAndMoney.get(this.practiceBaseName);
		if(pair==null)
			return this.returnWithTips(NONE,"实习基地选择错误!("+this.practiceBaseName+")");
		pair.getRegion().setMoneyBack(!pair.getRegion().getMoneyBack());
		try {
			pair.getRegion().update();
		} catch (IllegalArgumentException | SQLException e) {
			return this.returnWithTips(NONE,"服务器开小差去了!",e);
		}
		return this.jumpToMethodWithTips("display","修改成功!");
	}
	
	public String delete() {
		StringBuilder error=new StringBuilder();
		for(ListOfPracticeBaseAndMoney.RegionPair rp:this.practiceBaseAndMoney.getList()) {
			for(ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair:rp.getList()) {
				for(MoneyPB money:pair.getMoneys()) {
					try {
						money.delete();
					} catch (IllegalArgumentException | SQLException e) {
						error.append("\n"+pair.getPracticeBase().getName()+
								"清空失败("+e.getMessage()+")");
					}
				}
			}
		}
		if(error.length()<=0)
			return this.jumpToMethodWithTips("display","清空成功!");
		else
			return this.jumpToMethodWithTips("display","清空失败部分:"+error.toString());
	}

	public String create() {
		MoneyPB base[];
		try {
			base = MoneyPB.getMoneyPBBase();
		} catch (Exception e) {
			return this.jumpBackWithTips("读取教育实习经费标准失败!",e);
		}
		StringBuilder error=new StringBuilder();
		for(ListOfPracticeBaseAndMoney.RegionPair rp:this.practiceBaseAndMoney.getList()) {
			for(ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair:rp.getList()) {
				try {
					MoneyPB money=base[pair.getPracticeBase().getProvince().contains("北京")?0:1];
					money=(MoneyPB)money.clone();
					money.multiply(
							pair.getNumberOfStudent(),
							pair.getNumberOfStudentSYY(),
							pair.getRegion().getAccommodation());
					money.setPracticeBase(pair.getPracticeBase().getName());
					money.setYear(this.annual.getYear());
					money.create();
				} catch (IllegalArgumentException | SQLException | IllegalAccessException e) {
					error.append("\n"+pair.getPracticeBase().getName()+
							"增加失败("+e.getMessage()+")");
				}
			}
		}
		if(error.length()<=0)
			return this.jumpToMethodWithTips("display","增加成功!");
		else
			return this.jumpToMethodWithTips("display","增加失败部分:"+error.toString());
	}
	
}
