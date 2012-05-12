package samples.graph;

public class MyEdge {
	String label;
	Boolean dirtybit;

	public Boolean readbit() {
		return dirtybit;
	}

	public MyEdge(String label) {
		this.label = label;
	}

	public void setbit() {
		this.dirtybit = true;
	}

	public void initialize() {
		this.dirtybit = false;
	}

	@Override
	public String toString() {
		return label;
	}
}
