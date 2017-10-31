package action.jwc.sudo;

import java.sql.SQLException;

import obj.Base;
import obj.Search2;

public class TableOperation extends action.TableOperationAction{
	private static final long serialVersionUID = 8833385464572061925L;

	// 标志可变tableName
	public void setTableName(String tableName){this.setupTableName(tableName);}
	public String[] getTableNames(){return Base.TableNames;}
	
	public TableOperation() {
		super();
	}

	@Override
	protected void setupSearchRestraint()
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SQLException {
		this.getSearch().setRestraint(new Search2.AllRestraint(this.getSearch().getClassInfo()));
	}

	
	@Override
	public String display(){
		return super.display();
	}

	@Override
	public String execute(){
		return super.execute();
	}
	@Override
	public String update(){
		return super.update();
	}
	@Override
	public String create(){
		return super.create();
	}
	@Override
	public String delete(){
		return super.delete();
	}
	@Override
	public String upload(){
		return super.upload();
	}
	@Override
	public String download(){
		return super.download();
	}
	
}
