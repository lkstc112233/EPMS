package action.function;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.*;
import obj.annualTable.*;
import obj.staticObject.InnerPerson;
import obj.staticSource.Major;
import token.Role;

/**
 * 导出实习生名单
 */
public class ExportStudentList extends ActionSupport{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private ListOfPracticeBaseAndStudentsAndPlan practiceBaseAndStudents;
	
	public ListOfPracticeBaseAndStudentsAndPlan getPracticeBaseAndStudents(){return this.practiceBaseAndStudents;}
	
	//记忆化部件
	public Student getStudent(){return new Student();}
	

	static public final String SessionListKey="StudentArrangeIntoPracticeBase_list"; 
	
	public ExportStudentList(){
		super();
		this.practiceBaseAndStudents=Manager.loadSession(ListOfPracticeBaseAndStudentsAndPlan.class,SessionListKey);
		if(this.getMajors()!=null && !this.getMajors().isEmpty())
			this.setMajorName(this.getMajors().get(0).getName());
	}


	
	/**
	 * 用于显示
	 */
	public String display(){
		System.out.println(">> RegionArrangement:display > year="+this.getAnnual().getYear()+",majorName="+majorName);
		this.practiceBaseAndStudents=null;
		Major major=null;
		try{
			major=new Major(this.majorName);
		}catch(IllegalArgumentException | SQLException e){
			return Manager.tips("专业("+this.majorName+")不存在！",e,NONE);
		}
		try {
			this.practiceBaseAndStudents=new ListOfPracticeBaseAndStudentsAndPlan(
					this.getAnnual().getYear(),major,/*minPlanNumber*/1);
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return Manager.tips("数据库开小差去了！",e,NONE);
		}
		if(this.getInnerPersons()==null)
			return Manager.tips("读取实习基地列表失败!",NONE);
		if(this.getMajors()==null)
			return Manager.tips("读取实习专业列表失败!",NONE);
		Manager.saveSession(SessionListKey,this.practiceBaseAndStudents);
		System.out.println(">> RegionArrangement:display <NONE");
		return NONE;
	}
	
	/**
	 * 用于推荐大组长和设置指导老师
	 */
	@Override
	public String execute(){
		if(this.practiceBaseAndStudents==null)
			return display();
		System.out.println(">> RegionArrangement:execute > choose= ["+this.choose[0]+","+this.choose[1]+","+this.choose[2]+"]");
		ListOfPracticeBaseAndStudentsAndPlan.RegionPair.PracticeBasePair pair=
				this.practiceBaseAndStudents.get(this.choose[0]);//choose[0]是基地名称
		if(pair==null)
			return Manager.tips("请选择正确的实习基地！",
					display());
		//StudenGroupLeaderRecommend:execute
		Manager.removeSession(SessionListKey);
		if(this.choose[2]==null || this.choose[2].isEmpty()) { 
			//推荐大组长：choose[1]学生
			boolean ok=false;
			for(Student stu:pair.getStudents()) if(stu.getId().equals(this.choose[1])) {
				ok=true;
				break;
			}
			if(!ok) 
				return Manager.tips("基地("+this.choose[0]+")没有学生学号为("+this.choose[1]+")!",
						display());
			StringBuilder error=new StringBuilder();
			for(Student stu:pair.getStudents()) {
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
			return Manager.tips(error.toString(),
					display());
		}else if(this.choose[1]==null || this.choose[1].isEmpty()) {
			//设置基地所有学生指导老师：choose[2]老师
			InnerPerson teacher=null;
			try {
				teacher=new InnerPerson(this.choose[2]);
			}catch(SQLException | IllegalArgumentException e) {
				return Manager.tips("指导教师输入错误!",e,display());
			}
			StringBuilder error=new StringBuilder();
			for(Student stu:pair.getStudents()) {
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
			return Manager.tips(error.toString(),
					display());
		}else {
			//设置某学生指导老师：choose[1]学生,choose[2]老师
			InnerPerson teacher=null;
			try {
				teacher=new InnerPerson(this.choose[2]);
			}catch(SQLException | IllegalArgumentException e) {
				return Manager.tips("指导教师输入错误!",e,display());
			}
			Student opStudent=null;
			for(Student stu:pair.getStudents()) if(stu.getId().equals(this.choose[1])) {
				opStudent=stu;
				break;
			}
			if(opStudent==null)
				return Manager.tips("基地("+this.choose[0]+")没有学生学号为("+this.choose[1]+")!",
						display());
			if(teacher.getId().equals(opStudent.getTeacherId()))
				return Manager.tips(opStudent.getName()+"指导教师没有变化!",
						display());
			opStudent.setTeacherId(teacher.getId());
			try {
				opStudent.update();
			}catch(SQLException | IllegalArgumentException e) {
				return Manager.tips(opStudent.getName()+"设置指导老师失败!",
						e,display());
			}
			return Manager.tips(opStudent.getName()+"指导老师设置为"+teacher.getDescription(),
					display());
		}
	}
	
	
	
}
