package obj.restraint;

import obj.Base;
import obj.Restraint;

public abstract class BaseRestraint{
	
	private Restraint restraint;
		public Restraint getRestraint(){return restraint;}
		
		protected void setRestraint(Restraint restraint){
			this.restraint=restraint;
		}
		
		
	public abstract boolean checkBase(Base b,boolean setIfFalse);
}
