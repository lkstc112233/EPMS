package action.function.teacher;

import java.sql.SQLException;
import java.util.*;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.list.Leaf;
import obj.annualTable.list.List_Region_PracticeBaseRegionLeaderSuperviseSupervisors;
import obj.annualTable.list.PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors;
import obj.staticObject.InnerPerson;
import obj.staticSource.School;
import token.Role;

/**
 * 总领队和督导老师
 */
public class Export extends Action{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	

	private String schoolName=Role.jwc.getName();
		public void setSchoolName(String a){this.schoolName=Field.s2S(a);}
		public String getSchoolName(){return schoolName;}
	private List_Region_PracticeBaseRegionLeaderSuperviseSupervisors list;

	public List_Region_PracticeBaseRegionLeaderSuperviseSupervisors getList(){return this.list;}
	public int[] getSuperviseTypeList(){return Supervise.getTypeList();}

	//记忆化部件
	public Student getStudent(){return new Student();}
	private List<School> schools;
		public List<School> getSchools(){
			if(this.schools!=null) return this.schools;
			if(Manager.getUser()==null) return this.schools=null;
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
	private List<Set<InnerPerson>> supervisors;
		public List<Set<InnerPerson>> getSupervisors(){
			if(this.supervisors!=null) return this.supervisors;
			if(Manager.getUser()==null) return this.supervisors=null;
			if(this.getList()==null) return this.supervisors=null;
			Role role=Role.getRole(Manager.getUser());
			if(role==null) return null;
			this.supervisors=new ArrayList<Set<InnerPerson>>();
			for(School school:this.getSchools()) {
				Set<InnerPerson> tmp=new HashSet<InnerPerson>();
				for(Leaf<Region, PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors> rp:this.getList().getList()) {
					for(PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors pair:rp.getList()) {
						for(InnerPerson inner:pair.getSupervisors()) {
							if(inner!=null && school.getName().equals(inner.getSchool()))
								tmp.add(inner);
						}
					}
				}
				this.supervisors.add(tmp);
			}
			return this.supervisors;
		}
	
	

	static public final String SessionListKey="teacher_Export_List";
		
	public Export(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBaseRegionLeaderSuperviseSupervisors.class, SessionListKey);
		if(Manager.getUser()!=null)
			this.setSchoolName(Manager.getUser().getSchool());
	}
	
	public String display(){
		System.out.println(">> Export:display > year="+this.getAnnual().getYear()+",schoolName="+schoolName);
		this.list=null;
		try{
			this.list=new List_Region_PracticeBaseRegionLeaderSuperviseSupervisors(
					this.getAnnual().getYear());
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return this.returnWithTips(NONE,"数据库开小差去了！",e);
		}
		if(this.getSchools()==null)
			return this.returnWithTips(NONE,"读取实习专业列表失败!");
		Manager.saveSession(SessionListKey,this.list);
		return NONE;
	}
	
	@Override
	public String execute(){
		return display();
	}
	
	
}
