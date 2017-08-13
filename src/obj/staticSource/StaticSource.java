package obj.staticSource;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import obj.*;

public abstract class StaticSource extends Base{

	public StaticSource() throws SQLException {
		super();
	}
	
	
	static private Map<Class<? extends StaticSource>,List<StaticSource>> StaticList=new HashMap<Class<? extends StaticSource>,List<StaticSource>>();
	static public <T extends StaticSource>  List<T> list(Class<T> clazz) throws SQLException{
		List<StaticSource> tmp=StaticSource.StaticList.get(clazz);
		if(tmp==null){
			StaticSource.initialize(clazz);
			return list(clazz);
		}
		List<T> res=new ArrayList<T>();
		for(StaticSource t:tmp){
			if(clazz.isInstance(t))
				res.add(clazz.cast(t));
		}
		return res;
	}
	static private <T extends StaticSource> void initialize(Class<T> clazz){
		List<StaticSource> res=new ArrayList<StaticSource>();
		SQLCollection<T> sqlCollection=new SQLCollection<T>(clazz);
		List<T> tmp=null;
		try {
			tmp=sqlCollection.selectAll(null,null);//主要代码，从数据库读取数据，这里不会调用Base.load()
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		res.addAll(tmp);
		StaticSource.StaticList.put(clazz,res);
	}
	
	
	@Override
	public void update() throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update();
		Class<? extends StaticSource> clazz=this.getClass();
		StaticSource.StaticList.remove(clazz);
	}
	@Override
	public void update(Field[] updateFields) throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.update(updateFields);
		Class<? extends StaticSource> clazz=this.getClass();
		StaticSource.StaticList.remove(clazz);
	}
	@Override
	public void create() throws IllegalArgumentException, IllegalAccessException, SQLException{
		super.create();
		Class<? extends StaticSource> clazz=this.getClass();
		StaticSource.StaticList.remove(clazz);
	}
	
	
	
	
}