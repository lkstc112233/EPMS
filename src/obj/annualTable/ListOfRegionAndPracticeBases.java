package obj.annualTable;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.annualTable.ListOfRegionAndPracticeBases.RegionPair.PracticeBasePair;
import obj.staticObject.PracticeBase;

/**
 * there is a '<code>list</code>' which contains an '<code>ArrayList</code>'
 * with type '<code>Pair</code>'.
 *  '<code>Pair</code>'  has a '<code>region</code>' and a '<code>practiceBases</code>'
 *  which is an '<code>ArrayList</code>' with type'<code>PracticBase</code>'
 */
public class ListOfRegionAndPracticeBases{
	static public class RegionPair implements Comparable<RegionPair>{
		private Region region;
		private List<PracticeBasePair> list=new ArrayList<PracticeBasePair>();
		public Region getRegion() {return this.region;}
		public int getSize() {return this.list.size();}
		public List<PracticeBasePair> getList(){return this.list;}
		public RegionPair(Region region) {this.region=region;}

		static public class PracticeBasePair{
			private Region region;
			private PracticeBase practiceBase;
			public Region getRegion() {return this.region;}
			public PracticeBase getPracticeBase(){return this.practiceBase;}
			public PracticeBasePair(Region r,PracticeBase pb){this.region=r;this.practiceBase=pb;}
		}
		@Override
		public int compareTo(RegionPair o) {
			if(o==null) return 1;
			boolean hx=this.list.get(0).practiceBase.getHx();
			boolean hx2=o.list.get(0).practiceBase.getHx();
			if(hx && !hx2) return 1;
			if(!hx && hx2) return -1;
			if(hx && hx2)
				return this.region.compareTo(o.region);
			else
				return this.list.get(0).practiceBase.getProvince()
						.compareTo(o.list.get(0).practiceBase.getProvince());
		}	
	}
	private List<RegionPair> list=new ArrayList<RegionPair>();
		public List<RegionPair> getList(){return list;}
		public int getSize(){return list.size();}
	
		

	public ListOfRegionAndPracticeBases(int year,boolean containsNullRegion) throws IllegalArgumentException, InstantiationException, SQLException{
		if(containsNullRegion)
			list.add(new RegionPair(null));
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
			RegionPair rp=this.list.get(index[0]);
			for(index[1]=0;index[1]<rp.getList().size();index[1]++){
				PracticeBase pb=rp.getList().get(index[1]).practiceBase;
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}

	public RegionPair getByRegion(Region region){
		return this.getByRegion(region==null?null:region.getName());
	}
	public RegionPair getByRegion(String regionName){
		for(RegionPair rp:this.list) if(rp.getRegion()==null && regionName==null || rp.getRegion()!=null && regionName!=null && rp.getRegion().getName().equals(regionName))
			return rp;
		return null;
	}
	public PracticeBasePair get(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(RegionPair rp:this.list)
			for(PracticeBasePair p:rp.list){
				PracticeBase t=p.practiceBase;
				if(t!=null && t.getName()!=null && t.getName().equals(practiceBaseName))
					return p;
			}
		return null;
	}
	public PracticeBasePair get(PracticeBase pb){
		for(RegionPair rp:this.list)
			for(PracticeBasePair p:rp.list){
				PracticeBase t=p.practiceBase;
				if(pb==null && t==null) return p;
				if(pb!=null && t!=null && t.getName()!=null && t.getName().equals(pb.getName()))
					return p;
			}
		return null;
	}
	public void put(Region region, PracticeBase pb) {
		if(pb==null) return;
		PracticeBasePair tmp=this.get(pb);
		if(tmp==null){//需要新增一个PracticeBasePair
			for(RegionPair rp:this.list) {
				if(rp.getRegion()==null && region==null ||
						rp.getRegion()!=null && region!=null
						&& rp.getRegion().getName().equals(region.getName())) {
					rp.getList().add(tmp=new PracticeBasePair(region,pb));
					break;
				}
			}
			if(tmp==null) {
				//需要新增一个RegionPair
				RegionPair rp=new RegionPair(region);
				rp.getList().add(tmp=new PracticeBasePair(region,pb));
				this.list.add(rp);
			}
		}
	}
}
