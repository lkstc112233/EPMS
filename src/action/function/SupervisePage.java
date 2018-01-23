package action.function;

import java.sql.SQLException;

import action.*;
import obj.Field;
import obj.annualTable.*;
import obj.annualTable.list.List_Region_PracticeBaseRegionLeaderSuperviseSupervisors;
import obj.annualTable.list.PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors;
import token.Role;

/**
 * 总领队和督导老师
 */
public class SupervisePage extends Action{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	

	private List_Region_PracticeBaseRegionLeaderSuperviseSupervisors list;
	private int typeIndex=0;

	public List_Region_PracticeBaseRegionLeaderSuperviseSupervisors getlist(){return this.list;}
	public String[] getSuperviseTypeNameList(){return Supervise.getTypeNameList();}
	public int getTypeIndex() {return this.typeIndex;}
	public void setTypeIndex(String a) {this.typeIndex=Field.s2i(a,0);}
	public void setTypeIndex(int a) {this.typeIndex=a;}

	//记忆化部件
	

	static public final String SessionListKey=action.function.teacher.Export.SessionListKey;
		
	public SupervisePage(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBaseRegionLeaderSuperviseSupervisors.class, SessionListKey);
	}
	
	public String display(){
		if(Manager.getUser()==null || Role.getRole(Manager.getUser())!=Role.jwc)
			return this.jumpBackWithTips("无权查看督导详细信息!");
		System.out.println(">> Export:display > year="+this.getAnnual().getYear());
		this.list=null;
		try{
			this.list=new List_Region_PracticeBaseRegionLeaderSuperviseSupervisors(
					this.getAnnual().getYear());
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return this.returnWithTips(NONE,"数据库开小差去了！",e);
		}
		Manager.saveSession(SessionListKey,this.list);
		System.out.println(">> Export:display <NONE");
		return NONE;
	}

	
	
	private String practiceBaseName;
		public void setPracticeBaseName(String a) {this.practiceBaseName=a;}
	
	//保存信息
	@Override
	public String execute(){
		if(Manager.getUser()==null || Role.getRole(Manager.getUser())!=Role.jwc)
			return this.jumpBackWithTips("无权查看督导详细信息!");
		if(this.list==null)
			return this.returnWithTips(NONE,"该项目不可用!");
		PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors  pair=this.list.get(practiceBaseName);
		if(pair==null)
			return this.returnWithTips(NONE,"实习基地("+practiceBaseName+")选择不正确!");
		try {
			pair.getSupervises()[this.getTypeIndex()].update();
		} catch (IllegalArgumentException | SQLException e) {
			return this.jumpToMethodWithTips("display","保存失败!",e);
		} catch (IndexOutOfBoundsException e) {
			return this.jumpToMethodWithTips("display","督导选择错误!("+this.getTypeIndex()+")",e);
		}
		return this.jumpToMethodWithTips("display","保存成功!");
	}
	
	
}
