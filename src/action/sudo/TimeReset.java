package action.sudo;

import java.sql.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.*;
import obj.annualTable.Time;


public class TimeReset extends ActionSupport{
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
			return Manager.tips("出错了！",
					e,ERROR);
		}
		return "jump";
	}
	
	public String display(){
		return execute();
	}

	
	
}
