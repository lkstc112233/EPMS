package obj.annualTable.list;

import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

public class PracticeBaseWithRegion extends Pair<PracticeBase,Region>{

	public PracticeBaseWithRegion(PracticeBase pb,Region region){
		super(pb,region);
	}
	
	@SuppressWarnings("deprecation")
	public PracticeBase getPracticeBase() {return super.getKey();}
	@SuppressWarnings("deprecation")
	public Region getRegion() {return super.getValue();}
	
	
}
