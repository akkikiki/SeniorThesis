package samples.graph;

public class MyNode {
	String label;
	Integer packets;
	Packet p1;
	Boolean faulty;
	MyNode parent;
	public boolean two_step_extended;
	public MyNode(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}
