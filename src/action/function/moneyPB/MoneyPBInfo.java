package action.function.moneyPB;

import java.sql.SQLException;

import com.opensymphony.xwork2.ActionSupport;

import action.*;
import obj.*;
import obj.annualTable.*;

/**
 * 导出实习生名单
 */
public class MoneyPBInfo extends ActionSupport{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private ListOfPracticeBaseAndMoney practiceBaseAndStudents;
	
	public ListOfPracticeBaseAndMoney getPracticeBaseAndStudents(){return this.practiceBaseAndStudents;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public MoneyPBInfo(){
		super();
		this.practiceBaseAndStudents=Manager.loadSession(ListOfPracticeBaseAndMoney.class,SessionListKey);
	}

	private String jumpURL=Export.ActionName;
		public String getJumpURL() {return this.jumpURL;}
		public void setJumpURL(String a) {this.jumpURL=a;}

	
	private String practiceBaseName;
		public void setPracticeBaseName(String a) {this.practiceBaseName=Field.s2S(a);}
		public String getPracticeBaseName() {return this.practiceBaseName;}
	private ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair;
		public ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair getPair(){return this.pair;}
	
	public String display() {
		if(this.practiceBaseAndStudents==null)
			return Manager.tips("该项目未初始化!","jump");
		this.pair=this.practiceBaseAndStudents.get(this.practiceBaseName);
		if(this.pair==null)
			return Manager.tips("实习基地名称有误!","jump");
		System.out.println(">> MoneyPBInfo:display <NONE");
		return NONE;
	}
	
	private int index=-1;
		public void setIndex(String a) {this.index=Field.s2i(a,-1);}
	
	@Override
	public String execute(){
		if(this.pair==null)
			return Manager.tips("实习基地名称有误!","jump");
		if(index<0||index>=this.pair.getSize())
			return Manager.tips("条目选择有误!",NONE);
		try {
			pair.getMoneys().get(index).update();
		} catch (IllegalArgumentException | SQLException e) {
			return Manager.tips("数据库开小差去了!",e,NONE);
		}
		return Manager.tips("修改成功!",display());
	}
	
	private MoneyPB newMoneyPB=new MoneyPB();
		public MoneyPB getMoneyPB() {return this.newMoneyPB;}

	public String create(){
		if(this.pair==null)
			return Manager.tips("实习基地名称有误!","jump");
		this.newMoneyPB.setYear(this.annual.year);
		this.newMoneyPB.setPracticeBase(this.practiceBaseName);
		try {
			this.newMoneyPB.create();
			this.pair.getMoneys().add(this.newMoneyPB);
			this.pair.getSum().appendSum(this.newMoneyPB);
		} catch (IllegalArgumentException | IllegalAccessException | SQLException | NullPointerException e) {
			return Manager.tips("数据库开小差去了!",e,NONE);
		}
		this.newMoneyPB=new MoneyPB();
		return Manager.tips("新增成功!",display());
	}



}
