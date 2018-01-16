package action.function.moneyPB;

import java.sql.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.annualTable.ListOfPracticeBaseAndMoney;
import obj.annualTable.MoneyPB;

/**
 * 经费-教育实习基地经费
 */
public class Export extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}


	private ListOfPracticeBaseAndMoney practiceBaseAndMoney;
	
	public ListOfPracticeBaseAndMoney getPracticeBaseAndMoney(){return this.practiceBaseAndMoney;}
	
	//记忆化部件
	public MoneyPB getMoneyPB() {return new MoneyPB();}

	static public final String SessionListKey="moneyPB_Export_list"; 
	static public final String ActionName="function_moneyPB_Export_display.action";
	
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
			return Manager.tips("数据库开小差去了！",e,NONE);
		}
		Manager.saveSession(SessionListKey,this.practiceBaseAndMoney);
		System.out.println(">> Export:display <NONE");
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
			return Manager.tips("实习基地选择错误!("+this.practiceBaseName+")",NONE);
		pair.getRegion().setMoneyBack(!pair.getRegion().getMoneyBack());
		try {
			pair.getRegion().update();
		} catch (IllegalArgumentException | SQLException e) {
			return Manager.tips("服务器开小差去了!",e,display());
		}
		return Manager.tips("修改成功!",display());
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
			return Manager.tips("清空成功!",display());
		else
			return Manager.tips("清空失败部分:"+error.toString(),display());
	}

	public String create() {
		MoneyPB base[];
		try {
			base = MoneyPB.getMoneyPBBase();
		} catch (Exception e) {
			return Manager.tips("读取教育实习经费标准失败!",e,NONE);
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
			return Manager.tips("增加成功!",display());
		else
			return Manager.tips("增加失败部分:"+error.toString(),display());
	}
	
}
