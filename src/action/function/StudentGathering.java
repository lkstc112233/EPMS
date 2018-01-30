package action.function;

import action.Manager;
import obj.*;
import obj.annualTable.Student;

public class StudentGathering extends action.TableOperationAction{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	public StudentGathering(){
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
	protected Field[] refuseDisplayField() {
		return Field.getFields(Student.class,
				//	"year",
				//	"id","name","mobile","email",
				//	"major",
					"practiceBase",
				//	"sfzh","zzmm","province","nation",
				//	"hxyx",
					"recommend","teacherId",
					"outstandingType","outstandingMaterial",
					"status"
				);
	}
	
}
