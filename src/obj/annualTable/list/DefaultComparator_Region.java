package obj.annualTable.list;

import java.util.Comparator;

import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

@SuppressWarnings("rawtypes")
public class DefaultComparator_Region implements Comparator{

	
	
	@Override
	public int compare(Object o1, Object o2) {
		if(o1==null)
			return o2==null?0:-1;
		if(o2==null) return 1;
		if(o1 instanceof Region && o2 instanceof Region) {
			Region r1=(Region)o1;
			Region r2=(Region)o2;
			if(r1.getName()==null)
				return r2.getName()==null?r1.compareTo(r2):-1;
			if(r2.getName()==null) return 1;
			return r1.getName().compareTo(r2.getName());
		}
		PracticeBase p1=Pair.GetPracticeBase(o1);
		PracticeBase p2=Pair.GetPracticeBase(o2);
		if(p1!=null && p2!=null) {
			if(p1.getName()==null && p2.getName()!=null) return -1;
			return p1.getName().compareTo(p2.getName());
		}
		return Integer.compare(o1.hashCode(),o2.hashCode());
	}
	
}
