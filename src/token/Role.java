package token;

import java.sql.SQLException;
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
	
	static public Role getRole(Object b) {
		if(b==null) return null;
		if(b instanceof InnerPerson || b instanceof String) {
			try {
				b=new InnerOffice((b instanceof InnerPerson)?((InnerPerson)b).getOffice():b.toString());
			}catch(IllegalArgumentException | SQLException e) {
				return null;
			}
		}
		if(b instanceof InnerOffice) {
			InnerOffice x=(InnerOffice)b;
			if(x.getIsSchool()) {
				//属于部院系分支
				if(x.getName().contains(Role.lxr.getName()))//[(免费师范生/普通师范生)"教育实习联系人"]
					return Role.lxr;
				return Role.js;
			}
			if(x.getName().equals(Role.jwc.getName()))
				return Role.jwc;
			if(x.getName().equals(Role.ld.getName()))
				return Role.ld;
			return null;
		}
		if(b instanceof Student)
			return Role.xs;
		return null;
		
	}
}