package obj.annualTable;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.annualTable.ListOfRegionAndPracticeBaseAndInnerPerson.RegionPair.PracticeBasePair;
import obj.staticObject.InnerPerson;
import obj.staticObject.PracticeBase;

/**
 */
public class ListOfRegionAndPracticeBaseAndInnerPerson{
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
			private InnerPerson leader;
			private InnerPerson[] supervisor;
				public Region getRegion() {return this.region;}
				public PracticeBase getPracticeBase(){return this.practiceBase;}
				public InnerPerson getLeader() {return this.leader;}
				public InnerPerson[] getSupervisor() {return this.supervisor;}
			public PracticeBasePair(Region r,PracticeBase pb,InnerPerson leader,InnerPerson[] supervisor){
				this.region=r;this.practiceBase=pb;this.leader=leader;this.supervisor=supervisor;
			}
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
	
		

	public ListOfRegionAndPracticeBaseAndInnerPerson(int year) throws IllegalArgumentException, InstantiationException, SQLException{
		List<Base[]> tmp=Base.list(
				new JoinParam(PracticeBase.class)
				.append(JoinParam.Type.InnerJoin,
						Region.class,
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Region.class,"practiceBase"),
						Field.getField(Region.class,"year"),
						Integer.valueOf(year))
				.append(JoinParam.Type.LeftJoin,
						InnerPerson.class,
						Field.getField(Region.class,"leaderId"),
						Field.getField(InnerPerson.class,"id"))
				.append(JoinParam.Type.LeftJoin,
						Supervise.class,
						Field.getField(Supervise.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Supervise.class,"year"),
						Integer.valueOf(year)),
				new Restraint(Field.getField(PracticeBase.class,"hx")));
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=4){
				InnerPerson leader=null;
				Supervise sup=null;
				PracticeBase pb=null;
				Region region=null;
				if(bs[0]!=null) pb=(PracticeBase)bs[0];
				if(bs[1]!=null && pb!=null) region=(Region)bs[1];
				if(bs[2]!=null && pb!=null && region!=null) leader=(InnerPerson)bs[2];
				if(bs[3]!=null && pb!=null && region!=null) sup=(Supervise)bs[3];
				if(leader==null || sup==null) continue;
				try {
					this.put(region,pb,leader,sup,new InnerPerson(sup.getSupervisorId()));
				}catch(IllegalArgumentException | SQLException e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(this.list);
	}

	public PracticeBasePair get(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(RegionPair rp:this.list) if(rp.getRegion()!=null)
			for(PracticeBasePair p:rp.list){
				PracticeBase t=p.practiceBase;
				if(t!=null && t.getName()!=null && t.getName().equals(practiceBaseName))
					return p;
			}
		return null;
	}
	public PracticeBasePair get(PracticeBase pb){
		for(RegionPair rp:this.list) if(rp.getRegion()!=null)
			for(PracticeBasePair p:rp.list){
				PracticeBase t=p.practiceBase;
				if(pb==null && t==null) return p;
				if(pb!=null && t!=null && t.getName()!=null && t.getName().equals(pb.getName()))
					return p;
			}
		return null;
	}
	public void put(Region region,PracticeBase pb,InnerPerson leader,Supervise sup,InnerPerson supervisor) throws IllegalArgumentException, InstantiationException, SQLException {
		if(region==null || pb==null)
			return;
		PracticeBasePair tmp=this.get(pb);
		if(tmp==null){//需要新增一个PracticeBasePair
			for(RegionPair rp:this.list) {
				if(rp.getRegion().getName()!=null && rp.getRegion().getName().equals(region.getName())) {
					rp.getList().add(tmp=new PracticeBasePair(region,pb,leader,new InnerPerson[Supervise.TypeList.length]));
					break;
				}
			}
			if(tmp==null) {
				//需要新增一个RegionPair
				RegionPair rp=new RegionPair(region);
				rp.getList().add(tmp=new PracticeBasePair(region,pb,leader,new InnerPerson[Supervise.TypeList.length]));
				this.list.add(rp);
			}
		}
		tmp.getSupervisor()[sup.getSuperviseType()]=supervisor;
	}
}
