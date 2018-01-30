package obj.annualTable.list;

import obj.staticObject.PracticeBase;

class Pair<K,V> extends obj.Pair<K, V>{

	public Pair(K k,V v) {
		super(k,v);
	}
	
	@Override @Deprecated
	public K getKey() {
		return super.getKey();
	}
	@Override @Deprecated
	public V getValue() {
		return super.getValue();
	}
	@Override @Deprecated
	public V setValue(V value) {
		return super.setValue(value);
	}

	@Override @SuppressWarnings("rawtypes")
	public boolean equals(Object o){
		if(o==null) return false;
		if(!(o instanceof Pair)) return false;
		return this.getKey().equals(((Pair)o).getKey());
	}

	@SuppressWarnings("rawtypes")
	static protected <T> PracticeBase GetPracticeBase(T t){
		if(t==null) return null;
		if(t instanceof PracticeBase) return (PracticeBase)t;
		if(t instanceof obj.Pair) {
			PracticeBase pb=GetPracticeBase(((obj.Pair)t).getKey());
			if(pb!=null) return pb;
			pb=GetPracticeBase(((obj.Pair)t).getValue());
			if(pb!=null) return pb;
		}
		if(t instanceof Leaf) return GetPracticeBase(((Leaf<?,?>)t).getT());
		return null;
	}
}
