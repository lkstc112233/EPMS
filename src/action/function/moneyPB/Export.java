package action.function.moneyPB;

import java.sql.*;

import action.*;
import obj.annualTable.*;
import obj.annualTable.list.Leaf;
import obj.annualTable.list.List_Region_PracticeBaseRegionMoneyPB_MoneyPB;
import obj.annualTable.list.Node;
import obj.annualTable.list.PracticeBaseWithRegionWithMoneyPB;

/**
 * 经费-教育实习基地经费
 */
public class Export extends Action{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}


	private List_Region_PracticeBaseRegionMoneyPB_MoneyPB list;
	
	public List_Region_PracticeBaseRegionMoneyPB_MoneyPB getList(){return this.list;}
	
	//记忆化部件
	public MoneyPB getMoneyPB() {return new MoneyPB();}

	static public final String SessionListKey="moneyPB_Export_List";
	
	public Export(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBaseRegionMoneyPB_MoneyPB.class,SessionListKey);
	}
	
	/**
	 * 用于显示
	 */
	public String display(){
		System.out.println(">> Export:display > year="+this.getAnnual().getYear());
		this.list=null;
		try{
			this.list=new List_Region_PracticeBaseRegionMoneyPB_MoneyPB(
					this.getAnnual().getYear());
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return this.returnWithTips(NONE,"数据库开小差去了！",e);
		}
		Manager.resetSession();
		Manager.saveSession(SessionListKey,this.list);
		return NONE;
	}
	
	
	
	private String practiceBaseName;
		public void setPracticeBaseName(String a) {this.practiceBaseName=a;}
		public String getPracticeBaseName() {return this.practiceBaseName;}
		
	@Override
	public String execute(){
		//保存Region信息：moneyBack
		Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB>  pair=
				this.list.getByPracticeBaseName(this.practiceBaseName);
		if(pair==null)
			return this.returnWithTips(NONE,"实习基地选择错误!("+this.practiceBaseName+")");
		pair.getT().getRegion().setMoneyBack(!pair.getT().getRegion().getMoneyBack());
		try {
			pair.getT().getRegion().update();
		} catch (IllegalArgumentException | SQLException e) {
			return this.returnWithTips(NONE,"服务器开小差去了!",e);
		}
		return this.jumpToMethodWithTips("display","修改成功!");
	}
	
	public String delete() {
		StringBuilder error=new StringBuilder();
		for(Node<Region, Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB>> rp:this.list.getList()) {
			for(Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB> pair:rp.getList()) {
				for(MoneyPB money:pair.getList()) {
					try {
						money.delete();
					} catch (IllegalArgumentException | SQLException e) {
						error.append("\n"+pair.getT().getPracticeBase().getName()+
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
			return this.returnWithTips(NONE,"读取教育实习经费标准失败!",e);
		}
		StringBuilder error=new StringBuilder();
		for(Node<Region, Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB>> rp:this.list.getList()) {
			for(Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB> pair:rp.getList()) {
				try {
					MoneyPB money=base[pair.getT().getPracticeBase().getProvince().contains("北京")?0:1];
					money=(MoneyPB)money.clone();
					money.multiply(
							pair.getT().getNumberOfStudent(),
							pair.getT().getNumberOfStudentSYY(),
							pair.getT().getRegion().getAccommodation());
					money.setPracticeBase(pair.getT().getPracticeBase().getName());
					money.setYear(this.annual.getYear());
					money.create();
				} catch (IllegalArgumentException | SQLException | IllegalAccessException e) {
					error.append("\n"+pair.getT().getPracticeBase().getName()+
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
