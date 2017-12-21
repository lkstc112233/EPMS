package action.function;

import java.util.List;

import action.Manager;
import obj.*;
import obj.Restraint.Part;
import obj.staticObject.InnerPerson;
import obj.staticSource.Major;

public class InnerPersonImport extends action.TableOperationAction{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	public InnerPersonImport(){
		super();
	}
	
	@Override
	protected Search createSearch() throws Exception {
		JoinParam param=new JoinParam(obj.staticObject.InnerPerson.class);
		obj.staticObject.InnerPerson user=Manager.getUser();
		token.Role role=token.Role.getRole(user);
		Pair<List<Major>, List<Part>> p=
				obj.restraint.YearAndSchoolAndMajorRestraint.CreateRestraintList(
						param,this.getAnnual().getYear(),
						role==token.Role.jwc ? null : new obj.staticSource.School(user.getSchool())
								);
		p.getValue().add(new Restraint.Part(Field.getField(InnerPerson.class,"name"),Restraint.Type.NotLike,InnerPerson.UndefinedName));
		return new Search(param,new obj.restraint.YearAndSchoolAndMajorRestraint(
				param,3,p));
	}
	
}
