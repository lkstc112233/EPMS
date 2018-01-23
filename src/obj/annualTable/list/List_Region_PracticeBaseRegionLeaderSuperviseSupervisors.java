package obj.annualTable.list;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import obj.*;
import obj.annualTable.*;
import obj.staticObject.*;

public class List_Region_PracticeBaseRegionLeaderSuperviseSupervisors extends ListTree<Leaf<Region,PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors>>{
	public List_Region_PracticeBaseRegionLeaderSuperviseSupervisors(int year)
			throws IllegalArgumentException, InstantiationException, SQLException{
		this(year,new DefaultComparator_LeafRegion<PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors>());
	}
	public List_Region_PracticeBaseRegionLeaderSuperviseSupervisors(int year,
			Comparator<? super Leaf<Region,PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors>> comparator)
			throws IllegalArgumentException, InstantiationException, SQLException{
		super();
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
				Supervise supervise=null;
				PracticeBase pb=null;
				Region region=null;
				if(bs[0]!=null) pb=(PracticeBase)bs[0];
				if(bs[1]!=null && pb!=null) region=(Region)bs[1];
				if(bs[2]!=null && pb!=null && region!=null) leader=(InnerPerson)bs[2];
				if(bs[3]!=null && pb!=null && region!=null) supervise=(Supervise)bs[3];
				if(leader==null || supervise==null) continue;
				try {
					this.put(region,pb,leader,supervise,new InnerPerson(supervise.getSupervisorId()));
				}catch(IllegalArgumentException | SQLException e) {
					e.printStackTrace();
				}
			}
		}
		this.getList().sort(comparator);
	}

	public int[] indexOf(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(int[] index=new int[]{0,0};index[0]<this.getSize();index[0]++){
			Leaf<Region, PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors> rp=this.getList().get(index[0]);
			for(index[1]=0;index[1]<rp.getList().size();index[1]++){
				PracticeBase pb=rp.getList().get(index[1]).getPracticeBase();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}
	public PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors get(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(Leaf<Region, PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors> rp:this.getList())
			for(PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors pair:rp.getList()){
				PracticeBase pb=pair.getPracticeBase();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return pair;
			}
		return null;
	}
	public void put(Region region,PracticeBase pb,InnerPerson leader,Supervise supervise,InnerPerson supervisor) throws IllegalArgumentException, InstantiationException, SQLException {
		if(region==null || pb==null || supervise==null)
			return;
		Leaf<Region, PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors>tmp=
				new Leaf<Region, PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors>(
						region);
		tmp=this.insert(region,tmp);
		PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors pbrlss=
				new PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors(
						pb,region,leader,new Supervise[3],new InnerPerson[3]);
		pbrlss=tmp.insert(pbrlss);
		int type=supervise.getSuperviseType();
		pbrlss.getSupervisors()[type]=supervisor;
		pbrlss.getSupervises()[type]=supervise;
	}
}
