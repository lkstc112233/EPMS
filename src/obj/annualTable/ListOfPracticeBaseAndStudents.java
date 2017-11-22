package obj.annualTable;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.staticObject.PracticeBase;
import obj.staticSource.*;
import obj.annualTable.Student;

/**
 * there is a '<code>list</code>' which contains an '<code>ArrayList</code>'
 * with type '<code>Pair</code>'.
 *  '<code>Pair</code>'  has a '<code>practiceBase</code>' and a '<code>students</code>'
 *  which is an '<code>ArrayList</code>' with type'<code>PracticBase</code>'
 */
public class ListOfPracticeBaseAndStudents{
	static public class Pair{
		private PracticeBase practiceBase;
		private List<Student> students=new ArrayList<Student>();
		private Plan plan;
			public int getSize(){return this.students.size();}
			public PracticeBase getPracticeBase(){return this.practiceBase;}
			public List<Student> getStudents(){return this.students;}
			public Plan getPlan(){return plan;}
		public Pair(PracticeBase pb,Plan p){this.practiceBase=pb;this.plan=p;}
	}
	private List<Pair> list=new ArrayList<Pair>();
		public List<Pair> getList(){return list;}
		public int getSize(){return list.size();}
	
		
	public ListOfPracticeBaseAndStudents(int year,Major major,boolean containsNullPracticeBase) throws IllegalArgumentException, InstantiationException, SQLException{
		if(containsNullPracticeBase)
			list.add(new Pair(null,null));
		if(major==null||major.getName()==null||major.getName().isEmpty())
			throw new IllegalArgumentException("Major can NOT be null or empty!");
		List<Base[]> tmp=Base.list(
				new JoinParam(Student.class)
				.append(containsNullPracticeBase ? JoinParam.Type.LeftJoin : JoinParam.Type.InnerJoin,
						PracticeBase.class,
						Field.getField(Student.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"))
				.append(JoinParam.Type.LeftJoin,
						Plan.class,
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Plan.class,"practiceBase"),
						Field.getField(Plan.class,"major"),
						major.getName()),
				new Restraint(new Restraint.Part[]{
						new Restraint.Part(Field.getField(Student.class,"year"),year),
						new Restraint.Part(Field.getField(Student.class,"major"),major.getName())
				},
						Field.getField(Student.class,"id")));
		for(Base[] bs:tmp){
			Student stu=null;
			PracticeBase pb=null;
			if(bs!=null && bs.length>=2){
				if(bs[0]!=null){
					stu=(Student)bs[0];
					if(bs[1]!=null) pb=(PracticeBase)bs[1];
					if(containsNullPracticeBase || pb!=null){
						this.put(pb,stu,year,major);
					}
				}
			}
		}
	}
	
	public int[] indexOf(String studentName){
		int[] index=new int[]{-1,-1};
		if(studentName==null || studentName.isEmpty()) return null;
		for(index[0]=0;index[0]<this.list.size();index[0]++){
			List<Student> pbs=this.list.get(index[0]).getStudents();
			for(index[1]=0;index[1]<pbs.size();index[1]++){
				Student pb=pbs.get(index[1]);
				if(pb!=null && pb.getName()!=null && pb.getName().equals(studentName))
					return index;
			}
		}
		return null;
	}
	public Pair get(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(Pair p:this.list){
			PracticeBase t=p.practiceBase;
			if(t!=null && t.getName()!=null && t.getName().equals(practiceBaseName))
				return p;
		}
		return null;
	}
	public Pair get(PracticeBase pb){
		for(Pair p:this.list){
			PracticeBase t=p.practiceBase;
			if(pb==null && t==null) return p;
			if(pb!=null && t!=null && t.getName()!=null && t.getName().equals(pb.getName()))
				return p;
		}
		return null;
	}
	public void put(PracticeBase pb,Student stu,int year,Major major) throws IllegalArgumentException, InstantiationException, SQLException {
		Pair tmp=this.get(pb);
		if(tmp==null){
			//需要新增一个Pair
			Plan plan=null;
			if(pb!=null){
				List<Plan> ps=Base.list(Plan.class,new Restraint(new Restraint.Part[]{
						new Restraint.Part(Field.getField(Plan.class,"year"),year),
						new Restraint.Part(Field.getField(Plan.class,"practiceBase"),pb.getName()),
						new Restraint.Part(Field.getField(Plan.class,"major"),major)}));
				if(ps!=null && ps.size()>0) plan=ps.get(0);
			}
			if(plan==null)
				return;
			this.list.add(tmp=new Pair(pb,plan));
		}
		if(stu!=null)
			tmp.students.add(stu);
	}
}
