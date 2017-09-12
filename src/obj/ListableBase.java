package obj;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public abstract class ListableBase extends Base{
	
	/**
	 * 即用即查的
	 */
	static public interface ListableBaseWithNoSave{
	}
	/**
	 * 是否是即用即查的
	 */
	static public boolean isListableBaseWithNoSave(Class<? extends ListableBase> clazz){
		return ListableBaseWithNoSave.class.isAssignableFrom(clazz);
	}

	public ListableBase() throws SQLException {
		super();
	}
	
	
	static private Map<Class<? extends ListableBase>,List<ListableBase>> StaticList=new HashMap<Class<? extends ListableBase>,List<ListableBase>>();
	static public <T extends ListableBase>  List<T> list(Class<T> clazz) throws SQLException{
		List<ListableBase> tmp;
		boolean nosave=ListableBase.isListableBaseWithNoSave(clazz);
		if(nosave||
				(tmp=ListableBase.StaticList.get(clazz))==null){
			tmp=ListableBase.initializeList(clazz);
			if(!nosave)//非即用即查的就保存
				ListableBase.StaticList.put(clazz,tmp);
			if(tmp==null) throw new SQLException(
					"ListableBase.list CANNOT initialize the class("+clazz.getName()+")");
		}
		List<T> res=new ArrayList<T>();
		for(ListableBase t:tmp){
			if(clazz.isInstance(t))
				res.add(clazz.cast(t));
		}
		return res;
	}
	static private List<ListableBase> initializeList(Class<? extends ListableBase> clazz){
		List<ListableBase> res=new ArrayList<ListableBase>();
		try {
			res.addAll(SQLCollection.selectAll(clazz,null,null));//主要代码，从数据库读取数据，这里不会调用Base.load()
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public void update() throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update();
		Class<? extends ListableBase> clazz=this.getClass();
		ListableBase.StaticList.remove(clazz);
	}
	@Override
	public void update(Field[] updateFields) throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update(updateFields);
		Class<? extends ListableBase> clazz=this.getClass();
		ListableBase.StaticList.remove(clazz);
	}
	@Override
	public void create() throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.create();
		Class<? extends ListableBase> clazz=this.getClass();
		ListableBase.StaticList.remove(clazz);
	}
	
	
	
	
}