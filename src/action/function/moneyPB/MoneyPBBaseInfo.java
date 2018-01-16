package action.function.moneyPB;

import java.sql.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.annualTable.MoneyPB;

public class MoneyPBBaseInfo extends ActionSupport{
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

	private String jumpURL=Export.ActionName;
		public String getJumpURL() {return this.jumpURL;}
		public void setJumpURL(String a) {this.jumpURL=a;}
		
	
	public MoneyPBBaseInfo(){
		super();
	}
	
	@Override
	public String execute(){
		if(this.getBase()==null)
			return Manager.tips("项目未初始化!",display());
		try {
			for(MoneyPB m:this.base)
				m.update();
		} catch (IllegalArgumentException | SQLException e) {
			return Manager.tips("服务器开了点小差！",e,NONE);
		}
		return Manager.tips("修改成功！",display());
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
			return Manager.tips("创建教育实习经费标准条目失败!",e,"jump");
		}
		return Manager.tips("创建教育实习经费标准条目成功!",NONE);
	}
	
	
}
