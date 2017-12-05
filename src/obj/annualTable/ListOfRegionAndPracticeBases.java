package obj.annualTable;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.staticObject.PracticeBase;

/**
 * there is a '<code>list</code>' which contains an '<code>ArrayList</code>'
 * with type '<code>Pair</code>'.
 *  '<code>Pair</code>'  has a '<code>region</code>' and a '<code>practiceBases</code>'
 *  which is an '<code>ArrayList</code>' with type'<code>PracticBase</code>'
 */
public class ListOfRegionAndPracticeBases{
	static public class Pair implements Comparable<Pair>{
		private Region region;
		private List<PracticeBase> practiceBases=new ArrayList<PracticeBase>();
			public int getSize(){return this.practiceBases.size();}
			public Region getRegion(){return this.region;}
			public List<PracticeBase> getPracticeBases(){return this.practiceBases;}
			public Pair(Region r){this.region=r;}
		@Override
		public int compareTo(Pair o) {
			if(o==null) return 1;
			if(region==null && o.region!=null) return -1;
			if(region!=null && o.region==null) return 1;
			if(region==null && o.region==null) return 0;
			boolean hx=this.practiceBases.get(0).getHx();
			boolean hx2=o.practiceBases.get(0).getHx();
			if(hx && !hx2) return 1;
			if(!hx && hx2) return -1;
			if(hx && hx2)
				return this.region.compareTo(o.region);
			else
				return this.practiceBases.get(0).getProvince()
						.compareTo(o.practiceBases.get(0).getProvince());
		}	
	}
	private List<Pair> list=new ArrayList<Pair>();
		public List<Pair> getList(){return list;}
		public int getSize(){return list.size();}
	
		

	public ListOfRegionAndPracticeBases(int year,boolean containsNullRegion) throws IllegalArgumentException, InstantiationException, SQLException{
		if(containsNullRegion)
			list.add(new Pair(null));
		List<Base[]> tmp=Base.list(
				new JoinParam(PracticeBase.class).append(containsNullRegion ? JoinParam.Type.LeftJoin : JoinParam.Type.InnerJoin,
						Region.class,
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Region.class,"practiceBase"),
						Field.getField(Region.class,"year"),
						Integer.valueOf(year)),
				new Restraint(Field.getField(PracticeBase.class,"hx")));
		for(Base[] bs:tmp){
			PracticeBase pb=null;
			Region r=null;
			if(bs!=null && bs.length>=2
					&& bs[0]!=null){
				pb=(PracticeBase)bs[0];
				if(bs[1]!=null) r=(Region)bs[1];
				if(containsNullRegion || r!=null)
					this.put(r,pb);
			}
		}
		Collections.sort(this.list);
	}
	
	public int[] indexOf(String practiceBaseName){
		int[] index=new int[]{-1,-1};
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(index[0]=0;index[0]<this.list.size();index[0]++){
			List<PracticeBase> pbs=this.list.get(index[0]).getPracticeBases();
			for(index[1]=0;index[1]<pbs.size();index[1]++){
				PracticeBase pb=pbs.get(index[1]);
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}
	public Pair get(String regionName){
		if(regionName==null || regionName.isEmpty()) return null;
		for(Pair p:this.list){
			Region t=p.region;
			if(t!=null && t.getName()!=null && t.getName().equals(regionName))
				return p;
		}
		return null;
	}
	public Pair get(Region r) {
		for(Pair p:this.list){
			Region t=p.region;
			if(r==null && t==null) return p;
			if(r!=null && t!=null && t.getName()!=null && t.getName().equals(r.getName()))
				return p;
		}
		return null;
	}
	public void put(Region r, PracticeBase pb) {
		if(pb==null) return;
		Pair tmp=this.get(r);
		if(tmp==null)
			this.list.add(tmp=new Pair(r));
		tmp.practiceBases.add(pb);
	}
}
