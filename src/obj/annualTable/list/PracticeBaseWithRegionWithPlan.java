package obj.annualTable.list;

import obj.annualTable.Plan;
import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

public class PracticeBaseWithRegionWithPlan extends obj.Pair<PracticeBaseWithRegion,Plan>{

	public PracticeBaseWithRegionWithPlan(PracticeBase pb,Region region,Plan plan){
		super(new PracticeBaseWithRegion(pb,region),plan);
	}
	
	public PracticeBase getPracticeBaes() {return this.first().first();}
	public Region getRegion() {return this.first().second();}
	public Plan getPlan() {return this.second();}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(!(o instanceof obj.Pair)) return false;
		return this.getKey().equals(((obj.Pair)o).getKey());
	}
}
