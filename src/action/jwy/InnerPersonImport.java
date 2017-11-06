package action.jwy;

import java.sql.SQLException;

import action.Manager;
import obj.Search;
import obj.staticSource.School;

public class InnerPersonImport extends action.TableOperationAction{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	

	public InnerPersonImport() {
		super("InnerPerson");
	}

	@Override
	protected void setupSearchRestraint()
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SQLException {
		School school=new School();
		school.setName(Manager.getUser().getSchool());
		this.getSearch().setRestraint(new Search.jwyRestraint(this.getSearch().getClassInfo(),
				school,
				this.getAnnual().getYear()));
	}

}
