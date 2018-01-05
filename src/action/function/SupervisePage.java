package action.function;

import java.sql.SQLException;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.Field;
import obj.annualTable.*;
import obj.annualTable.ListOfRegionAndPracticeBaseAndInnerPerson.RegionPair.PracticeBasePair;

/**
 * 总领队和督导老师
 */
public class SupervisePage extends ActionSupport{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	

	private ListOfRegionAndPracticeBaseAndInnerPerson regionAndPracticeBaseAndInnerPerson;
	private int typeIndex=0;

	public ListOfRegionAndPracticeBaseAndInnerPerson getRegionAndPracticeBaseAndInnerPerson(){return this.regionAndPracticeBaseAndInnerPerson;}
	public String[] getSuperviseTypeNameList(){return Supervise.getTypeNameList();}
	public int getTypeIndex() {return this.typeIndex;}
	public void setTypeIndex(String a) {this.typeIndex=Field.s2i(a,0);}
	public void setTypeIndex(int a) {this.typeIndex=a;}

	//记忆化部件
	

	static public final String SessionListKey=action.function.teacher.Export.SessionListKey;
		
	public SupervisePage(){
		super();
		this.regionAndPracticeBaseAndInnerPerson=Manager.loadSession(ListOfRegionAndPracticeBaseAndInnerPerson.class, SessionListKey);
	}
	
	public String display(){
		System.out.println(">> Export:display > year="+this.getAnnual().getYear());
		this.regionAndPracticeBaseAndInnerPerson=null;
		try{
			this.regionAndPracticeBaseAndInnerPerson=new ListOfRegionAndPracticeBaseAndInnerPerson(
					this.getAnnual().getYear());
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return Manager.tips("数据库开小差去了！",e,NONE);
		}
		Manager.saveSession(SessionListKey,this.regionAndPracticeBaseAndInnerPerson);
		System.out.println(">> Export:display <NONE");
		return NONE;
	}

	
	
	private String practiceBaseName;
		public void setPracticeBaseName(String a) {this.practiceBaseName=a;}
	
	//保存信息
	@Override
	public String execute(){
		if(this.regionAndPracticeBaseAndInnerPerson==null)
			return Manager.tips("该项目不可用!",NONE);
		PracticeBasePair pair=this.regionAndPracticeBaseAndInnerPerson.get(practiceBaseName);
		if(pair==null)
			return Manager.tips("实习基地("+practiceBaseName+")选择不正确!",NONE);
		try {
			pair.getSupervise()[this.getTypeIndex()].update();
		} catch (IllegalArgumentException | SQLException e) {
			Manager.tips("保存失败!",e,display());
		} catch (IndexOutOfBoundsException e) {
			Manager.tips("督导选择错误!("+this.getTypeIndex()+")",e,display());
		}
		return Manager.tips("保存成功!",display());
	}
	
	
}
