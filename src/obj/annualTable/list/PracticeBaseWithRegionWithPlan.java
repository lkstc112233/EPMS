package obj.annualTable.list;

import obj.annualTable.Plan;
import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

public class PracticeBaseWithRegionWithPlan extends Pair<PracticeBaseWithRegion,Plan>{

	public PracticeBaseWithRegionWithPlan(PracticeBase pb,Region region,Plan plan){
		super(new PracticeBaseWithRegion(pb,region),plan);
	}

	@SuppressWarnings("deprecation")
	public PracticeBase getPracticeBase() {return super.getKey().getKey();}
	@SuppressWarnings("deprecation")
	public Region getRegion() {return super.getKey().getValue();}
	@SuppressWarnings("deprecation")
	public Plan getPlan() {return super.getValue();}

	
	
}
