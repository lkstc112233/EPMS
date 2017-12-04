package obj.annualTable;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.staticObject.PracticeBase;
import obj.staticSource.*;
import obj.annualTable.ListOfPracticeBaseAndStudentsAndPlan.RegionPair.PracticeBasePair;
import obj.annualTable.Student;

/**
 * there is a '<code>list</code>' which contains an '<code>ArrayList</code>'
 * with type '<code>Pair</code>'.<br/>
 *  '<code>Pair</code>'  has a '<code>practiceBase</code>' and a '<code>students</code>'
 *  which is an '<code>ArrayList</code>' with type'<code>PracticBase</code>'<br/><br/>
 *  树形结构<br/>
 *  ROOT<br/>
 *  {Region................Region}<br/>
 *  {[PracticeBase...].....[PracticeBase...]}<br/>
 *  {[(Student......)].....[(Student.......)]}<br/>
 */
public class ListOfPracticeBaseAndStudentsAndPlan{
	static public class RegionPair implements Comparable<RegionPair>{
		private Region region;
		private List<PracticeBasePair> list=new ArrayList<PracticeBasePair>();
			public Region getRegion() {return this.region;}
			public int getSize() {return this.list.size();}
			public List<PracticeBasePair> getList(){return this.list;}
			public int getAllStudentsCount() {
				int res=0;
				for(PracticeBasePair p:list) res+=p.getSize();
				return res;
			}
		public RegionPair(Region region) {this.region=region;}
			
		static public class PracticeBasePair{
			private PracticeBase practiceBase;
			private List<Student> students=new ArrayList<Student>();
			private Plan plan;
				public int getSize(){return this.students.size();}
				public PracticeBase getPracticeBase(){return this.practiceBase;}
				public List<Student> getStudents(){return this.students;}
				public Plan getPlan(){return plan;}
			public PracticeBasePair(PracticeBase pb,Plan p){this.practiceBase=pb;this.plan=p;}
		}	
		@Override
		public int compareTo(RegionPair o) {
			if(o==null) return 1;
			if(region==null && o.region!=null) return -1;
			if(region!=null && o.region==null) return 1;
			if(region==null && o.region==null) return 0;
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
	private List<Student> undistributedStudents=new ArrayList<Student>();
		public List<Student> getUndistributedStudents(){return this.undistributedStudents;}
	
	/**
	 * 不包含Region=null的子树
	 */
	public ListOfPracticeBaseAndStudentsAndPlan(int year,Major major,int minPlanNumber) throws IllegalArgumentException, InstantiationException, SQLException{
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
		List<Base[]> tmp=Base.list(
				new JoinParam(Student.class)
				.append(JoinParam.Type.RightJoin,
						Plan.class,
						Field.getFields(Student.class,"practiceBase","major","year"),
						Field.getFields(Plan.class,"practiceBase","major","year"),
						Field.getFields(Student.class,"major","year"),//Where子句不同
						new Object[]{major.getName(),year})
				.append(JoinParam.Type.LeftJoin,
						PracticeBase.class,
						Field.getField(Plan.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"))
				.append(JoinParam.Type.LeftJoin,
						Region.class,
						Field.getField(Region.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name")
						),
				new Restraint(Field.getField(Plan.class,"major"),//Where子句不同
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
	public void put(Region region,PracticeBase pb,Plan plan,Student stu) throws IllegalArgumentException, InstantiationException, SQLException {
	//	System.out.println(region+"\n\t"+pb+"\n\t"+plan+"\n\t"+stu);
		if(region==null || pb==null || plan==null) {
	//		if(!this.undistributedStudents.contains(stu))
	//			this.undistributedStudents.add(stu);
			return;
		}
		PracticeBasePair tmp=this.get(pb);
		if(tmp==null){//需要新增一个PracticeBasePair
			for(RegionPair rp:this.list) {
				if(rp.getRegion().getName()!=null && rp.getRegion().getName().equals(region.getName())) {
					rp.getList().add(tmp=new PracticeBasePair(pb,plan));
					break;
				}
			}
			if(tmp==null) {
				//需要新增一个RegionPair
				RegionPair rp=new RegionPair(region);
				rp.getList().add(tmp=new PracticeBasePair(pb,plan));
				this.list.add(rp);
			}
		}
		if(stu!=null && !tmp.students.contains(stu))
			tmp.students.add(stu);
	}
}
