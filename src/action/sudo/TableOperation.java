package action.sudo;

import obj.*;
import obj.restraint.AllRestraint;

public class TableOperation extends action.TableOperation2Action{
	private static final long serialVersionUID = 8833385464572061925L;

	//可变tableName
	private String tableName;
		public String[] getTableNames(){return Base.TableNames;}
		public String getTableName(){return this.tableName;}
		public void setTableName(String tableName){
			Class<? extends Base> clazz=Base.getClassForName(tableName);
			if(clazz==null) this.tableName=null;
			else if(this.getSearch()!=null){
				JoinParam oldP=this.getSearch().getParam();
				this.tableName=tableName;
				if(oldP!=null && oldP.getList().size()==1 && oldP.getClassByIndex(0).equals(clazz)){
					//same tableName
					return;
				}
			}
			this.search=null;
			this.display();//will reset the 'search' and 'operateBase'
		}
	
	public TableOperation() {
		super();
		this.tableName="ACCESS";
		if(this.getSearch()!=null){
			JoinParam oldP=this.getSearch().getParam();
			if(oldP!=null && oldP.getList().size()==1 && oldP.getClassByIndex(0)!=null)
				this.tableName=Base.getSQLTableName(oldP.getClassByIndex(0));
		}
	}
	
	@Override
	protected Search createSearch() throws Exception {
		System.out.println(">> TableOperation:createSearch > tableName="+this.tableName);
		JoinParam param=new JoinParam(Base.getClassForName(this.tableName));
		return new Search(param,new AllRestraint(param,3));
	}
	
}
