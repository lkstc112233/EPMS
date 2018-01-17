package action.sudo;

import java.sql.*;

import action.Action;
import obj.*;
import obj.annualTable.Time;

public class TimeReset extends Action{
	private static final long serialVersionUID = -2075656836784414352L;

	private action.Annual annual=new action.Annual();;
	public action.Annual getAnnual(){return this.annual;}

	private String jumpURL="sudo_TimeManager_display.action";
		public String getJumpURL() {return this.jumpURL;}
		public void setJumpURL(String a) {this.jumpURL=a;}

	
	@Override
	public String execute(){
		try {
			for(Time t:Base.list(Time.class,new Restraint(Field.getField(Time.class,"year"),this.getAnnual().getYear())))
				t.delete();
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return this.returnWithTips(ERROR,"出错了！",e);
		}
		return "menu";
	}
	
	public String display(){
		return execute();
	}

	
	
}
