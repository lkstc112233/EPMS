package obj.annualTable.list;

import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

public class RegionPair<T extends PracticeBasePairBase> extends Node<Region,T>
implements Comparable<RegionPair<T>>{

	public RegionPair(Region region) {
		super(region);
	}
		
	@Override
	public int compareTo(RegionPair<T> o) {
		if(o==null) return 1;
		Region thisR=this.getT();
		Region oR=o.getT();
		if(thisR==null ^ oR==null)
			return thisR==null?-1:1;
		if(this.getList().isEmpty()) {
			if(o.getList().isEmpty())
				return thisR.compareTo(oR);
			else
				return -1;
		}else {
			if(o.getList().isEmpty()) return 1;
			else {
				PracticeBase thisPB=this.getList().get(0).getKey();
				PracticeBase oPB=o.getList().get(0).getKey();
				boolean hx=thisPB.getHx();
				boolean hx2=oPB.getHx();
				if(hx && !hx2) return 1;
				if(!hx && hx2) return -1;
				if(hx && hx2)
					return thisR.compareTo(oR);
				else
					return thisPB.getProvince().compareTo(oPB.getProvince());
			}
		}
	}	
}
