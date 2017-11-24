package action;

import com.opensymphony.xwork2.ActionSupport;

public abstract class FunctionAction extends ActionSupport{
	private static final long serialVersionUID = 2832007385681831246L;
	
	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	public FunctionAction(){
		super();
	}
	
	
	/**
	 * 检查某部院系该项FunctionAction的完成进度
	 * @param school
	 * @return 0~100
	 */
	public abstract int checkProgress(obj.staticSource.School school);
	static public final int ProgressMin=0;
	static public final int ProgressMax=100;
	static public final int ProgressError_null=-1;
	static public final int ProgressError_SQL=-2;
	
	
}
