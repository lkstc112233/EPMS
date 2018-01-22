package obj.annualTable.list;

public class Node<T,L extends Leaf<?,?>> extends Leaf<T,L>{
	
	public Node(T t) {
		super(t);
	}
	/**
	 * 获取List中的某个元素，其Node.t属性和输入TOfListContent相同(equals)
	 * 相当于contains
	 */
	public L get(final Object tOfListContent) {
		for(L content:this.getList()) {
			if(tOfListContent==null && content.getT()==null) return content;
			if(tOfListContent!=null && content.getT()!=null) {
				if(tOfListContent.equals(content))
					return content;
			}
		}
		return null;
	}
	/**
	 * 向该Node中插入子节点，如果没有子节点则新建子节点（会调用get方法）
	 */
	@Override
	protected L insert(Object listContentToBeInsert){
		return super.insert(listContentToBeInsert);
	}
	public L insert(Object tOfListContentToBeInsert,final L aNewListContentIfNecessary){
		L tmp=this.get(tOfListContentToBeInsert);
		if(tmp==null){
			//相当于!contains(listContentToBeInsert)
			if(aNewListContentIfNecessary==null)
				throw new NullPointerException("When we insert a new 'L' into the list,"
						+ "the 'aNewListContentIfNecessary' CANNOT be null!");
			super.insert(tmp=aNewListContentIfNecessary);
		}
		return tmp;
	}
	
}
