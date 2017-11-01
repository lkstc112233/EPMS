package action.jwc;

import java.sql.SQLException;

import obj.Search;

public class SuperviseDesign extends action.TableOperationAction{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	

	public SuperviseDesign() {
		super("Supervise");
	}

	@Override
	protected void setupSearchRestraint()
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SQLException {
		this.getSearch().setRestraint(new Search.jwyRestraint(this.getSearch().getClassInfo(),
				null,//new School(Manager.getUser().getSchool()),
				this.getAnnual().getYear()));
	}

}
