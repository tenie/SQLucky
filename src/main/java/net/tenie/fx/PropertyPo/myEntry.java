package net.tenie.fx.PropertyPo;

/*   @author tenie */
public class myEntry<K, V> {
	private K v1;
	private V v2;

	public myEntry(K v1, V v2) {
		super();
		this.v1 = v1;
		this.v2 = v2;
	}

	public K getKey() {
		return v1;
	}

	public V getValue() {
		return v2;
	}

	public V setValue(V value) {
		v2 = value;
		return v2;
	}

}
