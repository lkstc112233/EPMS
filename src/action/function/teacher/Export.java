package action.function.teacher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.*;
import obj.annualTable.*;
import obj.staticSource.School;
import token.Role;

/**
 * 总领队和督导老师
 */
public class Export extends ActionSupport{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	

	private String schoolName=Role.jwc.getName();
		public void setSchoolName(String a){this.schoolName=Field.s2S(a);}
		public String getSchoolName(){return schoolName;}
	private ListOfRegionAndPracticeBaseAndInnerPerson regionAndPracticeBaseAndInnerPerson;

	public ListOfRegionAndPracticeBaseAndInnerPerson getRegionAndPracticeBaseAndInnerPerson(){return this.regionAndPracticeBaseAndInnerPerson;}
	public int[] getSuperviseTypeList(){return Supervise.getTypeList();}

	//记忆化部件
	public Student getStudent(){return new Student();}
	private List<School> schools;
		public List<School> getSchools(){
			if(this.schools!=null) return this.schools;
			Role role=Role.getRole(Manager.getUser());
			if(role==null) return null;
			try{
				this.schools=new ArrayList<School>();
				if(role==Role.jwc)
					this.schools.addAll(Base.list(School.class));
				else
					this.schools.add(new School(Manager.getUser().getSchool()));
				return this.schools;
			}catch(SQLException | IllegalArgumentException | InstantiationException e) {
				e.printStackTrace();
			}return this.schools=null;
		}
	
	

	static public final String SessionListKey="teacher_Export_List";
	static public final String ActionName="teacher_Export_display.action";
		
	public Export(){
		super();
		this.regionAndPracticeBaseAndInnerPerson=Manager.loadSession(ListOfRegionAndPracticeBaseAndInnerPerson.class, SessionListKey);
	}
	
	public String display(){
		System.out.println(">> Export:display > year="+this.getAnnual().getYear()+",schoolName="+schoolName);
		this.regionAndPracticeBaseAndInnerPerson=null;
		try{
			this.regionAndPracticeBaseAndInnerPerson=new ListOfRegionAndPracticeBaseAndInnerPerson(
					this.getAnnual().getYear());
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return Manager.tips("数据库开小差去了！",e,NONE);
		}
		if(this.getSchools()==null)
			return Manager.tips("读取实习专业列表失败!",NONE);
		Manager.saveSession(SessionListKey,this.regionAndPracticeBaseAndInnerPerson);
		System.out.println(">> Export:display <NONE");
		return NONE;
	}
	
	@Override
	public String execute(){
		return display();
	}
	
	
}
