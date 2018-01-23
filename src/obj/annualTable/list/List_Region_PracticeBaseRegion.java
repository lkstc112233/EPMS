package obj.annualTable.list;

import java.sql.SQLException;
import java.util.Comparator;

import obj.*;
import obj.annualTable.*;
import obj.staticObject.*;

public class List_Region_PracticeBaseRegion extends List<Leaf<Region,PracticeBaseWithRegion>>{
	public List_Region_PracticeBaseRegion(int year,boolean containsNullRegion)
			throws IllegalArgumentException, InstantiationException, SQLException{
		this(year,containsNullRegion,new DefaultComparator_LeafRegion<PracticeBaseWithRegion>());
	}
	public List_Region_PracticeBaseRegion(int year,boolean containsNullRegion,
			Comparator<? super Leaf<Region,PracticeBaseWithRegion>> comparator)
			throws IllegalArgumentException, InstantiationException, SQLException{
		super();
		if(containsNullRegion)
			this.getList().add(new Leaf<Region,PracticeBaseWithRegion>(null));
		java.util.List<Base[]> tmp=Base.list(
				new JoinParam(PracticeBase.class)
				.append(JoinParam.Type.LeftJoin,
						Region.class,
						Field.getField(Region.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Region.class,"year"),
						year),
				new Restraint(Field.getField(PracticeBase.class,"hx"))
				);
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=2){
				PracticeBase pb=null;
				Region region=null;
				if(bs[0]!=null) pb=(PracticeBase)bs[0];
				if((containsNullRegion || bs[1]!=null) && pb!=null) region=(Region)bs[1];
				this.put(region,pb);
			}
		}
		this.getList().sort(comparator);
	}

	public int[] indexOf(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(int[] index=new int[]{0,0};index[0]<this.getSize();index[0]++){
			Leaf<Region, PracticeBaseWithRegion> rp=this.getList().get(index[0]);
			for(index[1]=0;index[1]<rp.getList().size();index[1]++){
				PracticeBase pb=rp.getList().get(index[1]).getKey();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}
	
	public Leaf<Region,PracticeBaseWithRegion> getByRegionName(String regionName){
		if(regionName==null || regionName.isEmpty()) return null;
		for(Leaf<Region,PracticeBaseWithRegion> rp:this.getList()){
			Region r=rp.getT();
			if(r!=null && r.getName()!=null && r.getName().equals(regionName))
				return rp;
		}
		return null;
	}
	public PracticeBaseWithRegion getByPracticeBaseName(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(Leaf<Region, PracticeBaseWithRegion> rp:this.getList())
			for(PracticeBaseWithRegion pair:rp.getList()){
				PracticeBase p=pair.getKey();
				if(p!=null && p.getName()!=null && p.getName().equals(practiceBaseName))
					return pair;
			}
		return null;
	}
	public void put(Region region,PracticeBase pb) throws IllegalArgumentException, InstantiationException, SQLException {
		if(pb==null)
			return;
		Leaf<Region,PracticeBaseWithRegion> tmp=
				new Leaf<Region,PracticeBaseWithRegion>(region);
		tmp=this.insert(region,tmp);
		tmp.insert(new PracticeBaseWithRegion(pb,region));
	}
}
