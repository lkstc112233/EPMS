package obj.annualTable.list;

import java.util.List;

public class Node<T,L>{
	public final Class<T> tClazz;
	private T t;
	private List<L> list;
	
	public final Class<T> getTClass(){return this.tClazz;}
	public final T getT() {return t;}
	public final List<L> getList(){return list;}
	public final int getSize() {return list.size();}
	
	@SuppressWarnings("unchecked")
	public Node(T t) {
		if(t==null)
			throw new NullPointerException("Use 'new Pair(Class)' INSTEAD, if T is null.");
		this.t=t;
		this.tClazz=(Class<T>)this.getT().getClass();
	}
	public Node(Class<T> tClazz) {
		if(tClazz==null)
			throw new NullPointerException("The tClazz in Pair(Class<?>) CANNOT be null.");
		this.t=null;
		this.tClazz=tClazz;
	}
	

}
