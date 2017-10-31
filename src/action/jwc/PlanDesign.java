package action.jwc;

import java.sql.SQLException;

import action.Manager;
import obj.Search2;
import obj.staticSource.School;

public class PlanDesign extends action.TableOperationAction{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	

	public PlanDesign() {
		super("Plan");
	}

	@Override
	protected void setupSearchRestraint()
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SQLException {
		this.getSearch().setRestraint(new Search2.jwyRestraint(this.getSearch().getClassInfo(),
				new School(Manager.getUser().getSchool()),
				this.getAnnual().getYear()));
	}

}
