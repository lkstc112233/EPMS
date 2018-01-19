package obj.annualTable.list;

import java.sql.SQLException;

import obj.*;
import obj.annualTable.*;
import obj.staticObject.*;

public class List_Region_PracticeBase extends List<RegionPair<PracticeBasePairBase>>{
	public List_Region_PracticeBase(int year,boolean containsNullRegion)
			throws IllegalArgumentException, InstantiationException, SQLException{
		super();
		if(containsNullRegion)
			this.getList().add(new RegionPair<obj.Pair<PracticeBasePair,Region>>(null));
		java.util.List<Base[]> tmp=Base.list(
				new JoinParam(PracticeBasePair.class)
				.append(JoinParam.Type.LeftJoin,
						Region.class,
						Field.getField(Region.class,"practiceBase"),
						Field.getField(PracticeBasePair.class,"name"),
						Field.getField(Region.class,"year"),
						year),
				new Restraint(Field.getField(Student.class,"id"))
				);
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=2){
				PracticeBasePair pb=null;
				Region region=null;
				if(bs[0]!=null) pb=(PracticeBasePair)bs[0];
				if((containsNullRegion || bs[1]!=null) && pb!=null) region=(Region)bs[1];
				this.put(region,pb);
			}
		}
		java.util.Collections.sort(this.getList());
	}

	public int[] indexOf(String practiceBaseName){
		int[] index=new int[]{-1,-1};
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(index[0]=0;index[0]<this.getSize();index[0]++){
			RegionPair<Pair<PracticeBasePair, Region>> rp=this.getList().get(index[0]);
			for(index[1]=0;index[1]<rp.getList().size();index[1]++){
				PracticeBasePair pb=rp.getList().get(index[1]).getT().getKey();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}
	public Pair<PracticeBasePair, Region> get(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(RegionPair<Pair<PracticeBasePair, Region>> rp:this.getList())
			for(Node<Pair<PracticeBasePair, Region>, Pair<PracticeBasePair, Region>> p:rp.getList()){
				PracticeBasePair t=p.getKey();
				if(t!=null && t.getName()!=null && t.getName().equals(practiceBaseName))
					return p;
			}
		return null;
	}
	private Pair<PracticeBasePair, Region> get(PracticeBasePair pb){
		for(RegionPair<Pair<PracticeBasePair, Region>> rp:this.getList())
			for(Pair<PracticeBasePair, Region> p:rp.getList()){
				PracticeBasePair t=p.getKey();
				if(pb==null && t==null ||
						pb!=null && t!=null && t.getName()!=null && t.getName().equals(pb.getName()))
					return p;
			}
		return null;
	}
	public void put(Region region,PracticeBasePair pb) throws IllegalArgumentException, InstantiationException, SQLException {
		if(pb==null)
			return;
		PracticeBasePairBase tmp=this.get(pb);
		if(tmp==null){//需要新增一个PracticeBasePair
			for(RegionPair<PracticeBasePairBase> rp:this.getList()) {
				if(region==null && rp.getT()==null ||
						region!=null && rp.getT()!=null &&
						rp.getT().getName()!=null && rp.getT().getName().equals(region.getName())
						){
					rp.getList().add(tmp=new PracticeBasePairBase(region,pb));
					break;
				}
			}
			if(tmp==null) {
				//需要新增一个RegionPair
				RegionPair<PracticeBasePairBase> rp=
						new RegionPair<PracticeBasePairBase>(region);
				rp.getList().add(tmp=new PracticeBasePairBase(region,pb));
				this.getList().add(rp);
			}
		}
	}
}
