package obj.restraint;

import obj.*;

/**
 * 输入HardPart，对HardPart的约束进行强行限制
 * 一方面，查询时对HardPart的约束不能修改
 * 另一方面checkBase方法会对HardPart的部分进行检查和修正
 */
public class HardRestraint extends AllRestraint{
	
	private boolean[] isHardPart;
		public boolean[] getIsHeardPart(){return this.isHardPart;}
	
		
	public HardRestraint(JoinParam param,int orderFieldsCount,
			Restraint.Part[] hardPart) {
		super(param,orderFieldsCount);
		this.setupHardPart(hardPart);
	}
	
	protected void setupHardPart(Restraint.Part[] hardPart){
		if(hardPart==null) return;
		this.isHardPart=new boolean[this.getRestraint().getWhere().length];
		int i=0;
		for(Restraint.Part p:this.getRestraint().getWhere()){
			boolean flag=false;
			for(Restraint.Part h:hardPart){
				if(p.getField().equals(h.getField())){
					flag=true;
					h.copyTo(p);
					break;
				}
			}
			this.isHardPart[i++]=flag;
		}
	}
	
	
	@Override
	public boolean checkBase(Base b,boolean setIfFalse) {
		boolean res=true;
		int i=0;
		for(Restraint.Part p:this.getRestraint().getWhere()){
			if(this.isHardPart[i++] && p!=null){
				try{
					if(p.checkAndSetBase(b,setIfFalse)) continue;
				}catch(IllegalArgumentException e){
					e.printStackTrace();
					continue;
				}
				res=false;
			}
		}
		return res;
	}
	
}