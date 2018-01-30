package action.function;

import obj.annualTable.Supervise;

public class Supervise2Page extends SupervisePage{
	private static final long serialVersionUID = -5281826453438689424L;

	@Deprecated
	public void setTypeIndex(int a) {}
		
	public Supervise2Page(){
		super();
		super.setTypeIndex(Supervise.getTypeList()[2]);
	}
	
}
