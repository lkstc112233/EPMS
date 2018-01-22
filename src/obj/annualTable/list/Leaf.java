package obj.annualTable.list;

public class Leaf<T,L>{
	private T t;
	private java.util.List<L> list=new java.util.ArrayList<L>();
	
	public final T getT() {return t;}
	public final java.util.List<L> getList(){return list;}
	public final int getSize() {return list.size();}
	
	public Leaf(T t) {
		this.t=t;
	}

	/**
	 * 获取List中的某个元素，它和输入TOfListContent相同(equals)
	 * 相当于contains
	 */
	public L get(final Object listContent) {
		if(listContent==null) return null;
		for(L content:this.getList()) {
			if(listContent!=null && content!=null) {
				if(content.equals(listContent))
					return content;
			}
		}
		return null;
	}
	/**
	 * 向该Node中插入子节点，如果没有子节点则新建子节点（会调用get方法）
	 */
	@SuppressWarnings("unchecked")
	protected L insert(Object listContentToBeInsert){
		L tmp=this.get(listContentToBeInsert);
		if(tmp==null) try{//相当于!contains(listContentToBeInsert)
			this.getList().add((L)listContentToBeInsert);
		}catch(ClassCastException e){
			return null;
		}
		return tmp;
	}
	public L insert(Object listContentToBeInsert,L aNewListContentIfNecessary){
		return this.insert(listContentToBeInsert);
	}

}
