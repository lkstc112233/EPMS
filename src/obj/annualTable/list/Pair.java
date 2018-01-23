package obj.annualTable.list;

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
	
}
