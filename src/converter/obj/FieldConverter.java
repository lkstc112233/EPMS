package converter.obj;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

public class FieldConverter extends StrutsTypeConverter{
	
	@Override
	@SuppressWarnings("rawtypes")
	public Object convertFromString(Map context,String[] values,Class toType){
		if(values!=null && values.length>0){
			String[] ss=values[0].split("\\.",2);
			Class<? extends obj.Base> clazz=obj.Base.getClassForName(ss[0]);
			obj.Field f=obj.Field.getField(clazz,ss[1]);
			return f;
		}
		return null;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public String convertToString(Map context,Object obj){
		return obj==null?"":String.valueOf(obj);
	}
}
