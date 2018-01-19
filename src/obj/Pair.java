package obj;

import java.util.*;

public class Pair<K,V> implements Map.Entry<K,V>{
	private K key;
	private V value;
	
	public Pair(K k,V v){
		this.key=k;
		this.value=v;
	}
	public Pair(K k){
		this(k,(V)null);
	}
	public Pair(){
		this((K)null,(V)null);
	}

	@Override
	public K getKey() {
		return key;
	}public K first() {
		return this.getKey();
	}

	@Override
	public V getValue() {
		return value;
	}public V second() {
		return this.getValue();
	}

	@Override
	public V setValue(V value) {
		V tmp=this.value;
		this.value=value;
		return tmp;
	}public V second(V value) {
		return this.setValue(value);
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(!(o instanceof Pair))return false;
		Pair<?,?> p=(Pair<?,?>)o;
		if(this.getKey()==null ^ p.getKey()==null) return false;
		if(this.getKey()==null || !this.getKey().equals(p.getKey())) return false;
		if(this.getValue()==null ^ p.getValue()==null) return false;
		if(this.getValue()==null || !this.getValue().equals(p.getValue())) return false;
		return true;
	}
	

}
