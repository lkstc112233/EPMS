package obj.annualTable.list;

import obj.annualTable.Region;
import obj.annualTable.Supervise;
import obj.staticObject.InnerPerson;
import obj.staticObject.PracticeBase;

public class PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors extends
Pair<PracticeBaseWithRegion,Pair<InnerPerson,Pair<Supervise[],InnerPerson[]>>>{

	public PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors(PracticeBase pb,Region region,InnerPerson leader,Supervise[] sup,InnerPerson[] supervisors){
		super(new PracticeBaseWithRegion(pb,region),
				new Pair<>(leader,new Pair<>(sup,supervisors)));
	}
	
	@SuppressWarnings("deprecation")
	public PracticeBase getPracticeBase() {return super.getKey().getKey();}
	@SuppressWarnings("deprecation")
	public Region getRegion() {return super.getKey().getValue();}
	@SuppressWarnings("deprecation")
	public InnerPerson getLeader() {return super.getValue().getKey();}
	@SuppressWarnings("deprecation")
	public Supervise[] getSupervises() {return super.getValue().getValue().getKey();}
	@SuppressWarnings("deprecation")
	public InnerPerson[] getSupervisors() {return super.getValue().getValue().getValue();}
	

}
