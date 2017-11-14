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
	
	public boolean checkBases(Base bs[],boolean setIfFalse){
		boolean res=true;
		for(Base b:bs){
			if(!this.checkBase(b,setIfFalse))
				res=false;
		}
		return res;
	}
}
