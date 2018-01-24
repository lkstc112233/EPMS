package obj.annualTable.list;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

import obj.*;
import obj.annualTable.*;
import obj.staticObject.*;
import obj.staticSource.*;

public class List_Region_PracticeBaseRegionPlan_Student extends ListTree<Node<Region,Leaf<PracticeBaseWithRegionWithPlan,Student>>>{

	private java.util.List<Student> undistributedStudents=new ArrayList<Student>();
		public java.util.List<Student> getUndistributedStudents(){return this.undistributedStudents;}
	
	public List_Region_PracticeBaseRegionPlan_Student(int year,Major major,int minPlanNumber)
			throws IllegalArgumentException, InstantiationException, SQLException{
		this(year,major,minPlanNumber,new DefaultComparator_LeafRegion<Leaf<PracticeBaseWithRegionWithPlan,Student>>());
	}
	public List_Region_PracticeBaseRegionPlan_Student(int year,Major major,int minPlanNumber,
			Comparator<? super Node<Region,Leaf<PracticeBaseWithRegionWithPlan,Student>>> comparator)
			throws IllegalArgumentException, InstantiationException, SQLException{
		super();
		if(major==null||major.getName()==null||major.getName().isEmpty())
			throw new IllegalArgumentException("Major can NOT be null or empty!");
		//未分配实习基地的
		for(Student stu:Base.list(Student.class,new Restraint(
				Field.getFields(Student.class,"year","major"),
				new Object[] {year,major.getName()}))){
			if(stu.getPracticeBase()==null || stu.getPracticeBase().isEmpty())
				this.undistributedStudents.add(stu);
		}
		//已分配实习基地的
		java.util.List<Base[]> tmp=Base.list(
				new JoinParam(Student.class)
				.append(JoinParam.Type.RightJoin,
						Plan.class,
						Field.getFields(Student.class,"practiceBase","major","year"),
						Field.getFields(Plan.class,"practiceBase","major","year"),
						Field.getFields(Student.class,"major","year"),//Where子句不同
						new Object[]{major.getName(),year})
				.append(JoinParam.Type.LeftJoin,
						PracticeBase.class,
						Field.getField(Student.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"))
				.append(JoinParam.Type.LeftJoin,
						Region.class,
						Field.getField(Region.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Region.class,"year"),
						year),
				new Restraint(Field.getField(Plan.class,"major"),
						major.getName(),
						Field.getField(Student.class,"id"))
				);
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=4){
				Student stu=null;
				Plan plan=null;
				PracticeBase pb=null;
				Region region=null;
				if(bs[0]!=null) stu=(Student)bs[0];
				if(bs[1]!=null){
					plan=(Plan)bs[1];
					if(plan.getNumber()<minPlanNumber) plan=null;
				}
				if(bs[2]!=null && plan!=null) pb=(PracticeBase)bs[2];
				if(bs[3]!=null && pb!=null) region=(Region)bs[3];
				this.put(region,pb,plan,stu);
			}
		}
		this.getList().sort(comparator);
	}

	public int[] indexOf(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(int[] index=new int[]{0,0};index[0]<this.getSize();index[0]++){
			Node<Region, Leaf<PracticeBaseWithRegionWithPlan, Student>> rp=this.getList().get(index[0]);
			for(index[1]=0;index[1]<rp.getList().size();index[1]++){
				PracticeBase pb=rp.getList().get(index[1]).getT().getPracticeBase();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}
	public Leaf<PracticeBaseWithRegionWithPlan, Student> getByPracticeBaseName(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(Node<Region, Leaf<PracticeBaseWithRegionWithPlan, Student>> rp:this.getList())
			for(Leaf<PracticeBaseWithRegionWithPlan, Student> pair:rp.getList()){
				PracticeBase pb=pair.getT().getPracticeBase();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return pair;
			}
		return null;
	}
	public void put(Region region,PracticeBase pb,Plan plan,Student stu) throws IllegalArgumentException, InstantiationException, SQLException {
		if(region==null || pb==null || plan==null)
			return;
		Node<Region,Leaf<PracticeBaseWithRegionWithPlan,Student>> tmp=
				new Node<Region,Leaf<PracticeBaseWithRegionWithPlan,Student>>(region);
		tmp=this.insert(region,tmp);
		PracticeBaseWithRegionWithPlan pbrp=new PracticeBaseWithRegionWithPlan(pb,region,plan);
		Leaf<PracticeBaseWithRegionWithPlan,Student> tmp2=
				new Leaf<PracticeBaseWithRegionWithPlan,Student>(pbrp);
		tmp2=tmp.insert(pbrp,tmp2);
		tmp2.insert(stu);
	}
}
