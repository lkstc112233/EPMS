package obj.annualTable.list;

import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

public class PracticeBaseWithRegion extends obj.Pair<PracticeBase,Region>{

	public PracticeBaseWithRegion(PracticeBase pb,Region region){
		super(pb,region);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(!(o instanceof obj.Pair)) return false;
		return this.getKey().equals(((obj.Pair)o).getKey());
	}
}
