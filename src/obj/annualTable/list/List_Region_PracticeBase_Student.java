package obj.annualTable.list;

import java.sql.SQLException;

import obj.*;
import obj.annualTable.*;
import obj.staticObject.*;
import obj.staticSource.*;

public class List_Region_PracticeBase_Student extends List<RegionPair<PracticeBasePair<Student>>>{
	public List_Region_PracticeBase_Student(int year,Major major)
			throws IllegalArgumentException, InstantiationException, SQLException{
		super();
		if(major==null||major.getName()==null||major.getName().isEmpty())
			major=null;
		java.util.List<Base[]> tmp=Base.list(
				new JoinParam(Student.class)
				.append(JoinParam.Type.InnerJoin,
						PracticeBasePair.class,
						Field.getField(Student.class,"practiceBase"),
						Field.getField(PracticeBasePair.class,"name"),
						Field.getField(Student.class,"year"),
						year)
				.append(JoinParam.Type.LeftJoin,
						Region.class,
						Field.getField(Region.class,"practiceBase"),
						Field.getField(PracticeBasePair.class,"name"),
						Field.getField(Region.class,"year"),
						year),
				major==null ? new Restraint(Field.getField(Student.class,"id"))
						: new Restraint(Field.getField(Student.class,"major"),major.getName(),
								Field.getField(Student.class,"id"))
				);
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=3){
				Student stu=null;
				PracticeBasePair pb=null;
				Region region=null;
				if(bs[0]!=null) stu=(Student)bs[0];
				if(bs[1]!=null) pb=(PracticeBasePair)bs[1];
				if(bs[2]!=null && pb!=null) region=(Region)bs[2];
				this.put(region,pb,stu);
			}
		}
		java.util.Collections.sort(this.list);
	}

	public int[] indexOf(String practiceBaseName){
		int[] index=new int[]{-1,-1};
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(index[0]=0;index[0]<this.list.size();index[0]++){
			RegionPair<PracticeBasePair<Student>> rp=this.list.get(index[0]);
			for(index[1]=0;index[1]<rp.getList().size();index[1]++){
				PracticeBasePair pb=rp.getList().get(index[1]).practiceBase;
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}
	public PracticeBasePairBase get(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(RegionPair<PracticeBasePair<Student>> rp:this.list)
			for(PracticeBasePairBase p:rp.list){
				PracticeBasePair t=p.practiceBase;
				if(t!=null && t.getName()!=null && t.getName().equals(practiceBaseName))
					return p;
			}
		return null;
	}
	private PracticeBasePair<Student> get(PracticeBasePair pb){
		for(RegionPair<PracticeBasePair<Student>> rp:this.list)
			for(PracticeBasePair<Student> p:rp.list){
				PracticeBasePair t=p.practiceBase;
				if(pb==null && t==null) return p;
				if(pb!=null && t!=null && t.getName()!=null && t.getName().equals(pb.getName()))
					return p;
			}
		return null;
	}
	public void put(Region region,PracticeBasePair pb,Student stu) throws IllegalArgumentException, InstantiationException, SQLException {
		if(region!=null && pb==null)
			return;
		PracticeBasePair<Student> tmp=this.get(pb);
		if(tmp==null){//需要新增一个PracticeBasePair
			for(RegionPair<PracticeBasePair<Student>> rp:this.list) {
				if(rp.getT().getName()!=null && rp.getT().getName().equals(region.getName())) {
					rp.getList().add(tmp=new PracticeBasePair<Student>(region,pb));
					break;
				}
			}
			if(tmp==null) {
				//需要新增一个RegionPair
				RegionPair<PracticeBasePair<Student>> rp=
						new RegionPair<PracticeBasePair<Student>>(region);
				rp.getList().add(tmp=new PracticeBasePair<Student>(region,pb));
				this.list.add(rp);
			}
		}
		if(stu!=null && !tmp.getList().contains(stu))
			tmp.getList().add(stu);
	}
}
