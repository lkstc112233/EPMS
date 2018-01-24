package obj.annualTable.list;

import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

public final class DefaultComparator_LeafRegion<T> implements java.util.Comparator<Leaf<Region,T>>{
	
	
	@Override
	public int compare(Leaf<Region,T> o1, Leaf<Region,T> o2) {
		if(o1==null) return o2==null?0:-1;
		if(o2==null) return 1;
		Region r1=o1.getT();
		Region r2=o2.getT();
		if(r1==null ^ r2==null)
			return r1==null?-1:1;
		if(o1.getList().isEmpty()){
			if(o2.getList().isEmpty())
				return r1.compareTo(r2);
			else
				return -1;
		}else {
			if(o2.getList().isEmpty()) return 1;
			else {
				PracticeBase pb1=Pair.GetPracticeBase(o1.getList().get(0));
				PracticeBase pb2=Pair.GetPracticeBase(o2.getList().get(0));
				boolean hx=pb1.getHx();
				boolean hx2=pb2.getHx();
				if(hx && !hx2) return 1;
				if(!hx && hx2) return -1;
				if(hx && hx2)
					return r1.compareTo(r2);
				else
					return pb1.getProvince().compareTo(pb2.getProvince());
			}
		}
	}
	
}
