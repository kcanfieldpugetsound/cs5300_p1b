package edu.cornell.cs5300.project1b.util;

/**
 * A class for creating an immutable pair of objects. Pair components are
 * accessed using {@link #left()} and {@link #right()}.
 * 
 * @author gus
 *
 * @param <L> the type of the {@code left()} object
 * @param <R> the type of the {@code right()} object
 */
public class Pair<L,R> {

	private L left;
	private R right;
	
	public Pair (L l, R r) {
		left = l;
		right = r;
	}
	
	/**
	 * @return the left element of this {@code Pair}
	 */
	public L left () {
		return left;
	}
	
	/**
	 * @return the right element of this {@code Pair}
	 */
	public R right () {
		return right;
	}
	
	/**
	 * Two pairs are equal if and only if both their left and right elements
	 * are equal. Element comparisons done using {@code .equals(..)}.
	 * @param other
	 * @return
	 */
	public boolean equals (Object o) {
		if (!(o instanceof Pair<?,?>)) return false;
		Pair<?,?> other = (Pair<?,?>) o;
		return left().equals(other.left()) &&
			right().equals(other.right());
	}
	
	
}
