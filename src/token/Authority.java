package token;

import java.util.*;

import obj.*;
import obj.annualTable.*;
import obj.staticObject.*;
import obj.staticSource.*;

import java.lang.reflect.*;

public final class Authority {
	
	
	/**
	 * 通过innerOffice的Name获取Action的前缀
	 * 例如：教务处 -> jwc
	 * @param s
	 * @return
	 */
	static public String getActionPrefixByName(String s){
		return Role.getActionPrefix(Role.getRoleByName(s));
	}
	/*
	static public Set<Field> getSearchRefusedField(String tableName) {
		Class<? extends Base> clazz=Base.getClassForName(tableName);
		if(clazz==null) return null;
		Set<Field> res=new HashSet<Field>();
		Role role=Authority.Role.getRoleByOffice(Manager.getUser());
		if(role==null){
			for(Field f:Base.getFields(clazz)) res.add(f);
		}else if(role==Role.jwc){
		}else if(role==Role.jwy){
			if(clazz.equals(Student.class)){
			}else if(clazz.equals(InnerOffice))
		}else if(role==Role.jxyz){
		}else if(role==Role.ld){
		}else if(role==Role.js){
		}else if(role==Role.xs){
		}
		return res;
	}
	//*/
	
	
	
	
}
