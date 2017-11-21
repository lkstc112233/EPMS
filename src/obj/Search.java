package obj;

import java.sql.SQLException;
import java.util.*;

import obj.restraint.BaseRestraint;

public class Search {
	private final JoinParam param;
		public JoinParam getParam(){return this.param;}
	private BaseRestraint baseRestraint;
		public BaseRestraint getBaseRestraint(){return this.baseRestraint;}
	private final List<Base[]> result=new ArrayList<Base[]>();
		public List<Base[]> getResult(){return this.result;}
		
	
	public Search(JoinParam param){
		this(param,null);
	}
	public Search(JoinParam param,BaseRestraint baseRestraint){
		this.param=param;
		this.baseRestraint=baseRestraint;
	}
	
	
	public void execute() throws SQLException, InstantiationException, IllegalAccessException{
		this.result.clear();
		List<Base[]> list=Base.list(this.param,
				this.baseRestraint==null?null:this.baseRestraint.getRestraint());
		this.result.addAll(list);
	}
	
	
	
	
	
	
}