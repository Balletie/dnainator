package nl.tudelft.dnainator.core;

/**
 * Implements a default sequence conform the Sequence interface.
 */
public class DefaultSequence implements Sequence {
	
	private int id;
	private String source;
	private int start;
	private int end;
	private String sequence;

	/**
	 * Constructs a default sequence with all parameters specified.
	 * @param id
	 * @param source
	 * @param start
	 * @param end
	 * @param sequence
	 */
	public DefaultSequence(int id, String source, int start, int end, String sequence) {
		this.id = id;
		this.source = source;
		this.start = start;
		this.end = end;
		this.sequence = sequence;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public int getStartRef() {
		return start;
	}

	@Override
	public int getEndRef() {
		return end;
	}
	
	@Override
	public String getSequence() {
		return sequence;
	}
}
