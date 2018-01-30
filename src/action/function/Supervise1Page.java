package action.function;

import obj.annualTable.Supervise;

public class Supervise1Page extends SupervisePage{
	private static final long serialVersionUID = -5281826453438689424L;

	@Deprecated
	public void setTypeIndex(int a) {}
		
	public Supervise1Page(){
		super();
		super.setTypeIndex(Supervise.getTypeList()[1]);
	}
	
}
