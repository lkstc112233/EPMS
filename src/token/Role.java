package token;

import obj.staticObject.InnerPerson;

public enum Role{
	jwc("教务处"),
	jwy("教务员"),
	jxyz("教学院长"),
	ld("领导"),
	js("教师"),
	xs("学生");
	public final String name;
	Role(String name){this.name=name;}
	
	static public Role getRoleByOffice(InnerPerson inner){
		if(inner==null) return null;
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