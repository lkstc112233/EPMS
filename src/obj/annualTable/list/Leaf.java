package obj.annualTable.list;

public class Leaf<T,L>{
	private T t;
	private java.util.List<L> list=new java.util.ArrayList<L>();
	@SuppressWarnings("rawtypes")
	protected java.util.Comparator comparator;
	
	public final T getT() {return t;}
	public final java.util.List<L> getList(){return list;}
	public final int getSize() {return list.size();}
	
	protected Leaf(T t) {
		this(t,new DefaultComparator_Region());
	}
	public Leaf(T t,java.util.Comparator<?> comparator) {
		this.t=t;
		this.comparator=comparator;
	}

	/**
	 * 获取List中的某个元素，它和输入TOfListContent相同(equals)
	 * 相当于contains
	 */
	@SuppressWarnings("unchecked")
	public L get(final Object listContent) {
		if(listContent==null) return null;
		for(L content:this.getList()) {
			if(listContent!=null && content!=null) {
				if(comparator==null?content.equals(listContent):
					(comparator.compare(content,listContent)==0))
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
			this.getList().add(tmp=(L)listContentToBeInsert);
		}catch(ClassCastException e){
			return null;
		}
		return tmp;
	}
	public L insert(Object listContentToBeInsert,L aNewListContentIfNecessary){
		return this.insert(listContentToBeInsert);
	}

}
