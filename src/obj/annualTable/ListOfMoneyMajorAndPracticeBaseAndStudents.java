package obj.annualTable;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.staticObject.PracticeBase;
import obj.staticSource.*;
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
public class ListOfMoneyMajorAndPracticeBaseAndStudents{
	static public class MajorPair implements Comparable<MajorPair>{
		private Major major;
		private List<RegionPair> list=new ArrayList<RegionPair>();
			public Major getMajor() {return major;}
			public int getSize() {return this.list.size();}
			public List<RegionPair> getList(){return this.list;}
		public MajorPair(Major major) {this.major=major;}
			
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
				private Region region;
				private PracticeBase practiceBase;
				private List<Student> students=new ArrayList<Student>();
					public int getSize(){return this.students.size();}
					public Region getRegion() {return this.region;}
					public PracticeBase getPracticeBase(){return this.practiceBase;}
					public List<Student> getStudents(){return this.students;}
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
		
		@Override
		public int compareTo(MajorPair o) {
			if(o==null) return 1;
			return this.major.compareTo(o.major);
		}
	}
	private List<RegionPair> list=new ArrayList<RegionPair>();
		public List<RegionPair> getList(){return list;}
		public int getSize(){return list.size();}
	
	/**
	 * 不包含Region=null的子树
	 */
	public ListOfMoneyMajorAndPracticeBaseAndStudents(int year,Major major) throws IllegalArgumentException, InstantiationException, SQLException{
		if(major==null||major.getName()==null||major.getName().isEmpty())
			major=null;
		//已分配实习基地的
		List<Base[]> tmp=Base.list(
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
	public PracticeBasePairBase get(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(RegionPair rp:this.list) if(rp.getRegion()!=null)
			for(PracticeBasePairBase p:rp.list){
				PracticeBase t=p.practiceBase;
				if(t!=null && t.getName()!=null && t.getName().equals(practiceBaseName))
					return p;
			}
		return null;
	}
	public PracticeBasePairBase get(PracticeBase pb){
		for(RegionPair rp:this.list) if(rp.getRegion()!=null)
			for(PracticeBasePairBase p:rp.list){
				PracticeBase t=p.practiceBase;
				if(pb==null && t==null) return p;
				if(pb!=null && t!=null && t.getName()!=null && t.getName().equals(pb.getName()))
					return p;
			}
		return null;
	}
	public void put(Region region,PracticeBase pb,Student stu) throws IllegalArgumentException, InstantiationException, SQLException {
		if(region==null || pb==null)
			return;
		PracticeBasePairBase tmp=this.get(pb);
		if(tmp==null){//需要新增一个PracticeBasePair
			for(RegionPair rp:this.list) {
				if(rp.getRegion().getName()!=null && rp.getRegion().getName().equals(region.getName())) {
					rp.getList().add(tmp=new PracticeBasePairBase(region,pb));
					break;
				}
			}
			if(tmp==null) {
				//需要新增一个RegionPair
				RegionPair rp=new RegionPair(region);
				rp.getList().add(tmp=new PracticeBasePairBase(region,pb));
				this.list.add(rp);
			}
		}
		if(stu!=null && !tmp.students.contains(stu))
			tmp.students.add(stu);
	}
}
