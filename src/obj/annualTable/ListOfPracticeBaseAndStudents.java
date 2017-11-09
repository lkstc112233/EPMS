package obj.annualTable;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.staticObject.PracticeBase;
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
			public int getSize(){return this.students.size();}
			public PracticeBase getPracticeBase(){return this.practiceBase;}
			public List<Student> getStudents(){return this.students;}
			public Pair(PracticeBase r){this.practiceBase=r;}
	}
	private List<Pair> list=new ArrayList<Pair>();
		public List<Pair> getList(){return list;}
		public int getSize(){return list.size();}
	
		
	public ListOfPracticeBaseAndStudents(int year)throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InstantiationException, SQLException{
		this(year,true);
	}
	private ListOfPracticeBaseAndStudents(int year,boolean containsNullPracticeBase) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InstantiationException, SQLException{
		if(containsNullPracticeBase)
			list.add(new Pair(null));
		List<Base[]> tmp=Base.list(
				new Base.JoinParam(Student.class).append(containsNullPracticeBase ? Base.JoinType.LeftJoin : Base.JoinType.InnerJoin,
						PracticeBase.class,
						Field.getField(Student.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name")),
				Field.getField(Student.class,"year"),
				(Object)Integer.valueOf(year),
				Field.getField(Student.class,"id"));
		for(Base[] bs:tmp){
			Student pb=null;
			PracticeBase r=null;
			if(bs!=null && bs.length>=2){
				if(bs[0]!=null && bs[0] instanceof Student){
					pb=(Student)bs[0];
					if(bs[1]==null || bs[1] instanceof PracticeBase){
						if(bs[1]!=null) r=(PracticeBase)bs[1];
						if(containsNullPracticeBase || r!=null)
							this.put(r,pb);
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
	public Pair get(PracticeBase r) {
		for(Pair p:this.list){
			PracticeBase t=p.practiceBase;
			if(r==null && t==null) return p;
			if(r!=null && t!=null && t.getName()!=null && t.getName().equals(r.getName()))
				return p;
		}
		return null;
	}
	public void put(PracticeBase r, Student pb) {
		if(pb==null) return;
		Pair tmp=this.get(r);
		if(tmp==null)
			this.list.add(tmp=new Pair(r));
		tmp.students.add(pb);
	}
}
