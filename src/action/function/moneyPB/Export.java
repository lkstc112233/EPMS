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
	
	public ListOfPracticeBaseAndMoney getpracticeBaseAndMoney(){return this.practiceBaseAndMoney;}
	
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
	private String type;
		public void setType(String a) {this.type=a;}
		public String getType() {return this.type;}
		
	@Override
	public String execute(){
		String res="修改成功!";
		if("Region".equals(this.type)) {
			//保存Region信息：moneyBack
			ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair=
					this.practiceBaseAndMoney.get(this.practiceBaseName);
			if(pair==null)
				return Manager.tips("实习基地选择错误!("+this.practiceBaseName+")",NONE);
			try {
				pair.getRegion().update();
			} catch (IllegalArgumentException | SQLException e) {
				return Manager.tips("服务器开小差去了!",e,display());
			}
		}else if("PracticeBase".equals(this.type)) {
			//保存PracticeBase信息：remark
			ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair=
					this.practiceBaseAndMoney.get(this.practiceBaseName);
			if(pair==null)
				return Manager.tips("实习基地选择错误!("+this.practiceBaseName+")",NONE);
			try {
				pair.getPracticeBase().update();
			} catch (IllegalArgumentException | SQLException e) {
				return Manager.tips("服务器开小差去了!",e,display());
			}
		}
		return Manager.tips(res,display());
	}
	
	
	
}
