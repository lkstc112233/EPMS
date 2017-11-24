package action.function;

import java.sql.SQLException;
import java.util.List;

import action.Manager;
import obj.*;
import obj.staticObject.InnerPerson;
import obj.staticSource.InnerOffice;
import obj.staticSource.School;
import token.Role;

public class InnerPersonImport extends action.TableOperation2Action{
	private static final long serialVersionUID = 8833385464572061925L;
	
	public InnerPersonImport(){
		super();
	}
	
	@Override
	protected Search createSearch() throws Exception {
		JoinParam param=new JoinParam(obj.staticObject.InnerPerson.class);
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
		if(school.getName().equals(Role.jwc.getName())) {
			int res=0;
			List<School> tmp=null;
			try {
				tmp=Base.list(School.class);
			}catch(IllegalArgumentException | InstantiationException | SQLException e) {
				return ProgressError_SQL;
			}
			for(School s:tmp) {
				if(s.getName().equals(school.getName())) continue;
				res+=this.checkProgress(s);
			}
			return res/tmp.size()+1;
		}
		List<InnerPerson> inner=null;
		List<InnerOffice> io=null;
		try {
			inner=Base.list(InnerPerson.class,new Restraint(
					Field.getField(InnerPerson.class,"school"),
					school.getName()));
			io=Base.list(InnerOffice.class);
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			e.printStackTrace();
		}
		if(inner==null || io==null)
			return ProgressError_SQL;
		int res=ProgressMin;
		for(InnerOffice i:io) {
			for(InnerPerson j:inner)
				if(j.getOffice().equals(i.getName())) {
					res++;
					break;
				}
		}
		return res>=io.size()?ProgressMax:
			res*(ProgressMax-ProgressMin)/io.size();
	}
	
}
