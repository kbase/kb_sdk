package us.kbase.kidl;

public interface KidlNode {
	
	/** Accept a visitor to this node and return the results of the visitor's
	 * visit.
	 * @param visitor the visitor
	 * @return the result returned from the visitor.
	 */
	public <T> T accept(KidlVisitor<T> visitor, KidlNode parent);

}
