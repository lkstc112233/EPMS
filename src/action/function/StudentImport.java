package action.function;

import action.Manager;
import obj.*;
import obj.staticSource.School;

public class StudentImport extends action.TableOperation2Action{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	public StudentImport(){
		super();
	}
	
	@Override
	protected Search createSearch() throws Exception {
		JoinParam param=new JoinParam(obj.annualTable.Student.class);
		obj.staticObject.InnerPerson user=Manager.getUser();
		token.Role role=token.Role.getRole(user);
		return new Search(param,new obj.restraint.YearAndSchoolAndMajorRestraint(param,3,
				this.getAnnual().getYear(),
				role==token.Role.jwc ? null : new obj.staticSource.School(user.getSchool())
						));
	}
	
	

	@Override
	public int checkProgress(School school) {
		if(school==null || school.getName()==null ||school.getName().isEmpty())
			return ProgressError_null;
		return ProgressMin;
	}
	
}
