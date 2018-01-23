package obj.annualTable.list;

import java.sql.SQLException;
import java.util.Comparator;

import obj.*;
import obj.annualTable.*;
import obj.staticObject.*;
import obj.staticSource.*;

public class List_Region_PracticeBase_Student extends List<Node<Region,Leaf<PracticeBaseWithRegion,Student>>>{
	public List_Region_PracticeBase_Student(int year,Major major)
			throws IllegalArgumentException, InstantiationException, SQLException{
		this(year,major,new DefaultComparator_LeafRegion<Leaf<PracticeBaseWithRegion,Student>>());
	}
	public List_Region_PracticeBase_Student(int year,Major major,
			Comparator<? super Node<Region,Leaf<PracticeBaseWithRegion,Student>>> comparator)
			throws IllegalArgumentException, InstantiationException, SQLException{
		super();
		if(major==null||major.getName()==null||major.getName().isEmpty())
			major=null;
		java.util.List<Base[]> tmp=Base.list(
				new JoinParam(Student.class)
				.append(JoinParam.Type.InnerJoin,
						PracticeBase.class,
						Field.getField(Student.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Student.class,"year"),
						year)
				.append(JoinParam.Type.LeftJoin,
						Region.class,
						Field.getField(Region.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Region.class,"year"),
						year),
				major==null ? new Restraint(Field.getField(Student.class,"id"))
						: new Restraint(Field.getField(Student.class,"major"),major.getName(),
								Field.getField(Student.class,"id"))
				);
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=3){
				Student stu=null;
				PracticeBase pb=null;
				Region region=null;
				if(bs[0]!=null) stu=(Student)bs[0];
				if(bs[1]!=null) pb=(PracticeBase)bs[1];
				if(bs[2]!=null && pb!=null) region=(Region)bs[2];
				this.put(region,pb,stu);
			}
		}
		this.getList().sort(comparator);
	}

	public int[] indexOf(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(int[] index=new int[]{0,0};index[0]<this.getSize();index[0]++){
			Node<Region,Leaf<PracticeBaseWithRegion,Student>> rp=this.getList().get(index[0]);
			for(index[1]=0;index[1]<rp.getList().size();index[1]++){
				PracticeBase pb=rp.getList().get(index[1]).getT().first();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}
	public Leaf<PracticeBaseWithRegion,Student> get(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(Node<Region,Leaf<PracticeBaseWithRegion,Student>> rp:this.getList())
			for(Leaf<PracticeBaseWithRegion,Student> pair:rp.getList()){
				PracticeBase pb=pair.getT().first();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return pair;
			}
		return null;
	}
	public void put(Region region,PracticeBase pb,Student stu) throws IllegalArgumentException, InstantiationException, SQLException {
		if(region==null || pb==null)
			return;
		Node<Region,Leaf<PracticeBaseWithRegion,Student>> tmp=
				new Node<Region,Leaf<PracticeBaseWithRegion,Student>>(region);
		tmp=this.insert(region,tmp);
		PracticeBaseWithRegion pbr=new PracticeBaseWithRegion(pb,region);
		Leaf<PracticeBaseWithRegion, Student> tmp2=new Leaf<PracticeBaseWithRegion,Student>(pbr);
		tmp2=tmp.insert(pbr,tmp2);
		tmp2.insert(stu);
	}
}
