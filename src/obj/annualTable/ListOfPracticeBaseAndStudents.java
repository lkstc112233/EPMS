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
	
		
	public ListOfPracticeBaseAndStudents(int year,Major major,int minPlanNumber) throws IllegalArgumentException, InstantiationException, SQLException{
		list.add(new Pair(null,null));
		if(major==null||major.getName()==null||major.getName().isEmpty())
			throw new IllegalArgumentException("Major can NOT be null or empty!");
		List<Base[]> tmp=Base.list(
				new JoinParam(Student.class)
				.append(JoinParam.Type.LeftJoin,
						Plan.class,
						Field.getFields(Student.class,"practiceBase","major","year"),
						Field.getFields(Plan.class,"practiceBase","major","year"),
						Field.getFields(Plan.class,"major","year"),
						new Object[]{major.getName(),year})
				.append(JoinParam.Type.LeftJoin,
						PracticeBase.class,
						Field.getField(Plan.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name")
				),
				new Restraint(Field.getField(Student.class,"major"),
						major.getName(),
						Field.getField(Student.class,"id")));
		tmp.addAll(Base.list(
				new JoinParam(Student.class)
				.append(JoinParam.Type.RightJoin,
						Plan.class,
						Field.getFields(Student.class,"practiceBase","major","year"),
						Field.getFields(Plan.class,"practiceBase","major","year"),
						Field.getFields(Student.class,"major","year"),
						new Object[]{major.getName(),year})
				.append(JoinParam.Type.LeftJoin,
						PracticeBase.class,
						Field.getField(Plan.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name")
				),
				new Restraint(Field.getField(Plan.class,"major"),
						major.getName(),
						Field.getField(Student.class,"id")))
				);
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=3){
				Student stu=null;
				Plan plan=null;
				PracticeBase pb=null;
				if(bs[0]!=null) stu=(Student)bs[0];
				if(bs[1]!=null){
					plan=(Plan)bs[1];
					if(plan.getNumber()<minPlanNumber) plan=null;
				}
				if(bs[2]!=null && plan!=null) pb=(PracticeBase)bs[2];
				this.put(pb,plan,stu);
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
	public void put(PracticeBase pb,Plan plan,Student stu) throws IllegalArgumentException, InstantiationException, SQLException {
		Pair tmp=this.get(pb);
		if(tmp==null){//需要新增一个Pair
			if(pb!=null && plan!=null)
				this.list.add(tmp=new Pair(pb,plan));
		}
		if(stu!=null && !tmp.students.contains(stu))
			tmp.students.add(stu);
	}
}
