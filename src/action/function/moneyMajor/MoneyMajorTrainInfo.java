package action.function.moneyMajor;

import java.sql.*;

import action.*;
import obj.annualTable.MoneyPB;

public class MoneyMajorTrainInfo extends Action{
	private static final long serialVersionUID = 5998268336475528662L;

	private MoneyPB[] base;
	public MoneyPB[] getBase() {
		if(this.base!=null) return this.base;
		try {
			return this.base=MoneyPB.getMoneyPBBase();
		} catch (Exception e) {
			return this.base=null;
		}
	}

	public MoneyMajorTrainInfo(){
		super();
	}
	
	@Override
	public String execute(){
		if(this.getBase()==null)
			return this.jumpToMethodWithTips("display","项目未初始化!");
		try {
			for(MoneyPB m:this.base)
				m.update();
		} catch (IllegalArgumentException | SQLException e) {
			return this.returnWithTips(NONE,"服务器开了点小差！",e);
		}
		return this.jumpToMethodWithTips("display","修改成功！");
	}
		
	/**
	 * 用于显示
	 */
	public String display(){
		if(this.getBase()==null)
			return create();
		return NONE;
	}
	
	
	
	public String create(){
		this.base=new MoneyPB[2];
		try {
			for(int i=0;i<base.length;i++) { 
				base[i]=new MoneyPB();
				base[i].setYear(i);
				if(!base[i].existAndLoad())
					base[i].create();
			}
		} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
			return this.returnWithTips(NONE,"创建教育实习经费标准条目失败!",e);
		}
		return this.returnWithTips(NONE,"创建教育实习经费标准条目成功!");
	}
	
	
}
