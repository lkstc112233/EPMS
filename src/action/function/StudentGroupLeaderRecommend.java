package action.function;

import java.sql.*;
import java.util.*;

import action.*;
import obj.*;
import obj.annualTable.Student;
import obj.annualTable.list.Leaf;
import obj.annualTable.list.List_Region_PracticeBaseRegion_Student;
import obj.annualTable.list.PracticeBaseWithRegion;
import obj.staticObject.InnerPerson;
import obj.staticSource.Major;
import token.Role;

/**
 * 确定实习生大组长和指导教师
 */
public class StudentGroupLeaderRecommend extends Action{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private String majorName;
	private List_Region_PracticeBaseRegion_Student list;
	private String[] choose=new String[]{null,null,null};//[0]基地,[1]学生Id,[2]老师Id
	
	public void setMajorName(String a){this.majorName=Field.s2S(a);}
	public String getMajorName(){return majorName;}
	public String[] getChoose(){return this.choose;}
	public List_Region_PracticeBaseRegion_Student getList(){return this.list;}
	
	//记忆化部件
	public Student getStudent(){return new Student();}
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
	private List<Major> majors;
		public List<Major> getMajors(){
			if(this.majors!=null) return this.majors;
			Role role=Role.getRole(Manager.getUser());
			if(role==null) return null;
			try{
				if(role==Role.jwc)
					return this.majors=Base.list(Major.class);
				else
					return this.majors=Base.list(Major.class,
							new Restraint(Field.getField(Major.class,"school"),Manager.getUser().getSchool()));
			}catch(SQLException | IllegalArgumentException | InstantiationException e) {
				e.printStackTrace();
			}return this.majors=null;
		}
	

	static public final String SessionListKey="StudentArrangeIntoPracticeBase_list";
	static public final String SessionMajorNameKey="StudentArrangeIntoPracticeBase_majorName";
	
	public StudentGroupLeaderRecommend(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBaseRegion_Student.class,SessionListKey);
		this.majorName=Manager.loadSession(String.class,SessionMajorNameKey);
		if(this.majorName==null && this.getMajors()!=null && !this.getMajors().isEmpty())//默认值
			this.setMajorName(this.getMajors().get(0).getName());
	}


	
	/**
	 * 用于显示
	 */
	public String display(){
		System.out.println(">> StudentGroupLeaderRecommend:display > year="+this.getAnnual().getYear()+",majorName="+majorName);
		this.list=null;
		Major major=null;
		try{
			major=new Major(this.majorName);
		}catch(IllegalArgumentException | SQLException e){
			return this.returnWithTips(NONE,"专业("+this.majorName+")不存在！",e);
		}
		try {
			this.list=new List_Region_PracticeBaseRegion_Student(
					this.getAnnual().getYear(),major);
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return this.returnWithTips(NONE,"数据库开小差去了！",e);
		}
		if(this.getInnerPersons()==null)
			return this.returnWithTips(NONE,"读取校内教师列表失败!");
		if(this.getMajors()==null)
			return this.returnWithTips(NONE,"读取实习专业列表失败!");
		Manager.saveSession(SessionListKey,this.list);
		Manager.saveSession(SessionMajorNameKey,this.majorName);
		System.out.println(">> StudentGroupLeaderRecommend:display <NONE");
		this.choose[2]=null;
		return NONE;
	}
	
	/**
	 * 用于推荐大组长和设置指导老师
	 */
	@Override
	public String execute(){
		if(this.list==null)
			return display();
		System.out.println(">> StudentGroupLeaderRecommend:execute > choose= ["+this.choose[0]+","+this.choose[1]+","+this.choose[2]+"]");
		Leaf<PracticeBaseWithRegion,Student> pair=
				this.list.get(this.choose[0]);//choose[0]是基地名称
		if(pair==null)
			return this.jumpBackWithTips("请选择正确的实习基地！");
		//StudenGroupLeaderRecommend:execute
		if(this.choose[2]==null || this.choose[2].isEmpty()) { 
			//推荐大组长：choose[1]学生
			boolean ok=false;
			for(Student stu:pair.getList()) if(stu.getId().equals(this.choose[1])) {
				ok=true;
				break;
			}
			if(!ok) 
				return this.jumpBackWithTips("基地("+this.choose[0]+")没有学生学号为("+this.choose[1]+")!");
			StringBuilder error=new StringBuilder();
			for(Student stu:pair.getList()) {
				boolean flag=stu.getId().equals(this.choose[1]);
				if(stu.getRecommend()==flag) continue;
				stu.setRecommend(flag);
				if(error.length()>0) error.append('\n');
				try {
					stu.update();
					error.append(stu.getName()+"推荐状态变更成功!");
				}catch(SQLException | IllegalArgumentException e) {
					e.printStackTrace();
					error.append(stu.getName()+"推荐状态变更失败!("+e.getMessage()+")");
				}
			}
			return this.jumpToMethodWithTips("display",error.toString());
		}else if(this.choose[1]==null || this.choose[1].isEmpty()) {
			//设置基地所有学生指导老师：choose[2]老师
			InnerPerson teacher=null;
			try {
				teacher=new InnerPerson(this.choose[2]);
			}catch(SQLException | IllegalArgumentException e) {
				return this.jumpBackWithTips("指导教师输入错误!",e);
			}
			StringBuilder error=new StringBuilder();
			for(Student stu:pair.getList()) {
				if(teacher.getId().equals(stu.getTeacherId())) continue;
				stu.setTeacherId(teacher.getId());
				if(error.length()>0) error.append('\n');
				try {
					stu.update();
					error.append(stu.getName()+"设置指导老师成功!");
				}catch(SQLException | IllegalArgumentException e) {
					e.printStackTrace();
					error.append(stu.getName()+"设置指导老师失败!("+e.getMessage()+")");
				}
			}
			return this.jumpToMethodWithTips("display",error.toString());
		}else {
			//设置某学生指导老师：choose[1]学生,choose[2]老师
			InnerPerson teacher=null;
			try {
				teacher=new InnerPerson(this.choose[2]);
			}catch(SQLException | IllegalArgumentException e) {
				return this.jumpBackWithTips("指导教师输入错误!",e);
			}
			Student opStudent=null;
			for(Student stu:pair.getList()) if(stu.getId().equals(this.choose[1])) {
				opStudent=stu;
				break;
			}
			if(opStudent==null)
				return this.jumpBackWithTips("基地("+this.choose[0]+")没有学生学号为("+this.choose[1]+")!");
			if(teacher.getId().equals(opStudent.getTeacherId()))
				return this.jumpBackWithTips(opStudent.getName()+"指导教师没有变化!");
			opStudent.setTeacherId(teacher.getId());
			try {
				opStudent.update();
			}catch(SQLException | IllegalArgumentException e) {
				return this.jumpBackWithTips(opStudent.getName()+"设置指导老师失败!",e);
			}
			return this.jumpToMethodWithTips("display",opStudent.getName()+"指导老师设置为"+teacher.getDescription());
		}
	}
	
	
	
}
