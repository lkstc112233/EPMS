package token;

import java.util.*;

import obj.*;
import obj.annualTable.Student;
import obj.staticObject.InnerPerson;
import obj.staticSource.*;

public enum Role{
	xs("学生"),
	lxr("教育实习联系人"),//[(免费师范生/普通师范生)"教育实习联系人"]
	js("教师"),
	jwc("教务处"),
	ld("领导"),
	;
	public final String name;
	public String getName(){return this.name;}
	public String getString(){return this.toString();}
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
	
	static public Role getRole(Base b) {
		if(b==null) return null;
		if(b instanceof Student)
			return Role.xs;
		if(!(b instanceof InnerPerson))
			return null;
		InnerPerson inner=(InnerPerson)b;
		if(inner.getSchool()==null || inner.getSchool().isEmpty())
			return null;
		if(inner.getOffice()==null || inner.getOffice().isEmpty())
			return null;
		boolean isSchool=inner.getSchool().equals("教务处");
		if(inner.getOffice().contains("教育实习联系人"))
			return isSchool ? Role.lxr : Role.jwc;
		else if(inner.getOffice().contains("教学院长"))
			return isSchool ? Role.js : Role.ld;
		else
			return isSchool ? Role.xs : null;
	}
}