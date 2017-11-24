package action.function;

import action.Manager;
import obj.*;
import obj.staticSource.School;

public class PlanDesign extends action.TableOperation2Action{
	private static final long serialVersionUID = 8833385464572061925L;

	public PlanDesign(){
		super();
	}
	
	@Override
	protected Search createSearch() throws Exception {
		JoinParam param=new JoinParam(obj.annualTable.Plan.class);
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
