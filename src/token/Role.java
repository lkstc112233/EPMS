package token;

import java.util.*;

import obj.Field;
import obj.staticObject.InnerPerson;
import obj.staticSource.ACCESS;

public enum Role{
	xs("学生"),
	jxyz("教学院长"),
	jwy("教务员"),
	js("教师"),
	jwc("教务处"),
	ld("领导"),
	;
	public final String name;
	Role(String name){this.name=name;}
	
	public boolean getACCESS(ACCESS a){
		Object o=Field.getField(ACCESS.class,this.toString()).get(a);
		if(o==null) return false;
		if(o instanceof Boolean) return (Boolean)o;
		return false;
	}
	static public List<Role> getAccessRolesList(ACCESS a){
		List<Role> res=new ArrayList<Role>();
		for(Role r:Role.values()) if(r.getACCESS(a)) res.add(r);
		return res;
	}
	
	static public Role getRoleByInnerPerson(InnerPerson inner){
		if(inner==null)
			return null;
		return Role.getRoleByName(inner.getOffice().toString());
	}
	static public Role getRoleByName(String s){
		for(Role r:Role.values()) if(r.name.equals(s))
			return r;
		return null;
	}
	static public String getActionPrefix(Role role){
		if(role==null) return null;
		return role.toString();
	}
	static public boolean containActionPrefix(String prefix){
		for(Role r:Role.values()) if(getActionPrefix(r).equals(prefix))
			return true;
		return false;
	}
}