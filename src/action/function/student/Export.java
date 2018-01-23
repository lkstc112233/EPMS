package action.function.student;

import java.sql.*;
import java.util.*;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.list.List_Region_PracticeBase_Student;
import obj.staticObject.InnerPerson;
import obj.staticSource.Major;
import token.Role;

/**
 * 导出实习生名单、商洽函、布局规划、指导老师名单
 */
public class Export extends Action{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}


	private String majorName=this.getAllMajors().getName();
		public void setMajorName(String a){this.majorName=Field.s2S(a);}
		public String getMajorName(){return majorName;}
	private List_Region_PracticeBase_Student list;
	
	public List_Region_PracticeBase_Student getList(){return this.list;}
	
	//记忆化部件
	public Student getStudent(){return new Student();}
	private List<Major> majors;
		private Major allMajors=null;
		public Major getAllMajors() {if(allMajors==null){allMajors=new Major();allMajors.setSubject("所有专业");}return this.allMajors;}
		public List<Major> getMajors(){
			this.getAllMajors();
			if(this.majors!=null) return this.majors;
			Role role=Role.getRole(Manager.getUser());
			if(role==null) return null;
			try{
				this.majors=new ArrayList<Major>();
				this.majors.add(this.getAllMajors());
				if(role==Role.jwc)
					this.majors.addAll(Base.list(Major.class));
				else
					this.majors.addAll(Base.list(Major.class,
							new Restraint(Field.getField(Major.class,"school"),Manager.getUser().getSchool()))
							);
				return this.majors;
			}catch(SQLException | IllegalArgumentException | InstantiationException e) {
				e.printStackTrace();
			}return this.majors=null;
		}
	private List<InnerPerson> innerPersons;
	public List<InnerPerson> getInnerPersons(){
		if(this.innerPersons!=null) return this.innerPersons;
		try {
			if(Manager.getUser().getSchool().equals(Role.jwc.getName()))
				return this.innerPersons=InnerPerson.list(InnerPerson.class
						,new Restraint(Field.getField(InnerPerson.class,"name"),Restraint.Type.NotLike,InnerPerson.UndefinedName)
						);
			else
				return this.innerPersons=Base.list(InnerPerson.class,new Restraint(new Restraint.Part[]{
						new Restraint.Part(Field.getField(InnerPerson.class,"school"),Manager.getUser().getSchool()),
						new Restraint.Part(Field.getField(InnerPerson.class,"name"),Restraint.Type.NotLike,InnerPerson.UndefinedName)
						}));
		}catch(SQLException | IllegalArgumentException | InstantiationException e) {
			e.printStackTrace();
		}return this.innerPersons=null;
	}
	

	static public final String SessionListKey="student_Export_list"; 
	
	public Export(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBase_Student.class,SessionListKey);
		if(this.getMajors()!=null && !this.getMajors().isEmpty())
			this.setMajorName(this.getMajors().get(0).getName());
	}
	
	

	/**
	 * 用于显示
	 */
	public String display(){
		System.out.println(">> Export:display > year="+this.getAnnual().getYear()+",majorName="+majorName);
		this.list=null;
		Major major=null;
		try{
			if(this.majorName!=null && !this.majorName.isEmpty())
				major=new Major(this.majorName);
		}catch(IllegalArgumentException | SQLException e){
			return this.returnWithTips(NONE,"专业("+this.majorName+")不存在！",e);
		}
		try{
			this.list=new List_Region_PracticeBase_Student(
					this.getAnnual().getYear(),major);
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return this.returnWithTips(NONE,"数据库开小差去了！",e);
		}
		if(this.getMajors()==null)
			return this.returnWithTips(NONE,"读取实习专业列表失败!");
		Manager.saveSession(SessionListKey,this.list);
		System.out.println(">> Export:display <NONE");
		return NONE;
	}
	
	@Override
	public String execute(){
		return display();
	}
	
	
	
}
