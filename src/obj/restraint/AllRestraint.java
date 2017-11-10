package obj.restraint;

import obj.*;

public class AllRestraint extends BaseRestraint{
	
	public AllRestraint(JoinParam param,int orderFieldsCount) {
		int len=0;
		for(JoinParam.Part p:param.getList())
			len+=p.getFields().size();
		Restraint.Part[] where=new Restraint.Part[len];
		len=0;
		for(JoinParam.Part p:param.getList())
			for(Field f:p.getFields())
				where[len++]=new Restraint.Part(f,null,null);
		Field[] order=new Field[Math.max(0,Math.min(orderFieldsCount,where.length))];
		this.setRestraint(new Restraint(where,order));
	}
	
	
	@Override
	public boolean checkBase(Base b,boolean setIfFalse) {
		return true;
	}
	
}