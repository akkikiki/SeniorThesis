package samples.graph;

import java.util.Collection;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class Sample3a {
	static int macro_n = 11;
	static int macro_N = (int) Math.pow(2, macro_n);
	static double macro_p = 0.4;// failure percentage
	static int trial_limit = (int) Math.ceil(1 / Math.pow(1 - macro_p,
			macro_n - 1));
	public static int pathextension1(Graph<MyNode,MyEdge> graph, Packet origin, MyNode currentnode, MyNode nextnode, int i){
    	MyEdge outedge;
		outedge = graph.findEdge(currentnode, nextnode);
		if(!outedge.dirtybit){
			currentnode.packets--; nextnode.packets++;
			origin.changingbit(i+1);
			origin.setcurrent(nextnode.label);
			outedge.setbit();
		}
		else{
			origin.bfsnodes.push(nextnode.label);
		}
		return i;
	}
	public static void pathextension3(Packet origin, MyNode nextnode){
		origin.two_step_next = nextnode.label;
	}
	
	public static int hrouter(Packet n1, Graph<MyNode,MyEdge> graph, int i, String destination, int k){
    	Boolean flag = false;
    	String current;
    	current = n1.readcurrent(); 
		MyNode currentnode,nextnode;
		currentnode = Data.MyNodes[Integer.parseInt(current, 2)];
		String a = current.substring(i, i+1);
		String c = destination.substring(i, i+1);
		while(a.equals(c)){
			n1.changingbit(i+1);
			if(i == macro_n - 1){
				break;
			}
			i++;
		}
		a = current.substring(i, i+1);
    	c = destination.substring(i, i+1);
    	StringBuffer b = new StringBuffer(current);
    	b.deleteCharAt(i);
    	b.insert(i, c);
		if(!a.equals(c)){
			nextnode = getnode(b.toString());
			if(!nextnode.faulty){
				pathextension1(graph, n1, currentnode, nextnode, i);
			}
			else{
				while(i < macro_n- k){
					for(int j = macro_n- k +1; j<macro_n; j++){
				   		String d = current.substring(j, j+1);
				    	StringBuffer q = new StringBuffer(current);
				    	b.deleteCharAt(j);
				    	q.deleteCharAt(j);
				    	/*b is two bits different from the current node
				    	 * q is one bit different from the current node */
						int p = Integer.parseInt(d);
						p = (p + 1)%2;  //ˆê‚Â‚ÌŒ…‚Émod 2‘«‚µŽZ‚ð‚·‚éB
						String s = String.valueOf(p);
				    	q.insert(j, s);
				    	b.insert(j, s);
						MyNode q1 = Data.MyNodes[Integer.parseInt(q.toString(), 2)];
						MyNode q2 = Data.MyNodes[Integer.parseInt(b.toString(), 2)];
						if(!q1.faulty && !q2.faulty){
							flag = true;
							//System.out.println("sending packet from "+ currentnode.label + " to " +q2.label);
							pathextension1(graph, n1, currentnode, q1, i);
							pathextension3(n1, q2);
							break;
							}

						b.deleteCharAt(j);
				    	b.insert(j, d);
					}
					if(!flag){
						k++;
					}
					else{
						break;
					}
				}
				}	
			}
		return k;
		}

	/* pathextension is sending a packet from its current node to its next node */
	public static boolean pathextension(Graph<MyNode, MyEdge> graph,
			Packet origin, MyNode currentnode, MyNode nextnode) {
		MyEdge outedge;
		outedge = graph.findEdge(currentnode, nextnode);
		boolean delay = false;
		if (!outedge.dirtybit) {
			currentnode.packets--;
			nextnode.packets++;
			origin.setcurrent(nextnode.label);
			outedge.setbit();
			delay = false;
		} else {
			delay = true;
		}
		return delay;
	}

	public static MyNode getnode(String node_string) {
		int node_int = Integer.parseInt(node_string, 2);
		MyNode node = Data.MyNodes[node_int];
		return node;
	}

	public static int gordon_stout_always_sidetracking(
			Graph<MyNode, MyEdge> graph, Packet pa, int routing_failed_packets) {
		int place = (int) ((Math.random() * 1000000) % pa.hammingdistance);
		String a = pa.current.substring(pa.diff_bit[place],
				pa.diff_bit[place] + 1);
		int a_number = Integer.parseInt(a);
		a_number = (a_number + 1) % 2;
		String flipped_bit = String.valueOf(a_number);
		StringBuffer b = new StringBuffer(pa.current);
		b.deleteCharAt(pa.diff_bit[place]);
		b.insert(pa.diff_bit[place], flipped_bit);
		int nextnode_int = Integer.parseInt(b.toString(), 2);
		MyNode next_node_candidate = Data.MyNodes[nextnode_int];
		if (next_node_candidate.faulty) {
			int checked_bit[] = new int[macro_n];
			int counter = 0;
			for (int l = 0; l < macro_n; l++) {
				StringBuffer changing = new StringBuffer(pa.current);
				String changing_word = changing.substring(l, l + 1);
				int changing_number = Integer.parseInt(changing_word);
				changing_number = (changing_number + 1) % 2;
				flipped_bit = String.valueOf(changing_number);
				changing.deleteCharAt(l);
				changing.insert(l, flipped_bit);
				MyNode checking_node = Data.MyNodes[Integer.parseInt(
						changing.toString(), 2)];
				if (!checking_node.faulty) {
					checked_bit[counter] = l;
					counter++;
				}
			}
			boolean flag = false;
			int optimal_bit = 0;
			int optimal_diff_bit = 0;
			for (int l = 0; l < counter; l++) {
				for (int j = 0; j < pa.diff_bit.length; j++) {
					if (checked_bit[l] == pa.diff_bit[j]) {
						optimal_bit = l;
						optimal_diff_bit = j;
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}
			if (flag) {
				StringBuffer c2 = new StringBuffer(pa.current);// ‘I‚Ñ’¼‚·
				String c0 = c2.substring(checked_bit[optimal_bit],
						checked_bit[optimal_bit] + 1);
				int c_number2 = Integer.parseInt(c0);
				c_number2 = (c_number2 + 1) % 2;
				flipped_bit = String.valueOf(c_number2);
				c2.deleteCharAt(checked_bit[optimal_bit]);
				c2.insert(checked_bit[optimal_bit], flipped_bit);
				MyNode currentnode = getnode(pa.current);
				MyNode nextnode = getnode(c2.toString());
				boolean delay = pathextension(graph, pa, currentnode, nextnode);
				if (!delay) {
					for (int s = optimal_diff_bit; s < pa.hammingdistance - 1; s++) {
						pa.diff_bit[optimal_diff_bit] = pa.diff_bit[optimal_diff_bit + 1];
					}
					pa.hammingdistance--;
				}
			} else {
				int random = (int) ((Math.random() * 1000000) % counter);
				pa.diff_bit[pa.hammingdistance] = checked_bit[random];
				StringBuffer c1 = new StringBuffer(pa.current);
				String c = c1.substring(checked_bit[random],
						checked_bit[random] + 1);
				int c_number = Integer.parseInt(c);
				c_number = (c_number + 1) % 2;
				flipped_bit = String.valueOf(c_number);
				c1.deleteCharAt(checked_bit[random]);
				c1.insert(checked_bit[random], flipped_bit);
				MyNode currentnode = getnode(pa.current);
				MyNode nextnode = getnode(c1.toString());
				pathextension(graph, pa, currentnode, nextnode);
				pa.hammingdistance++;
				pa.ttl++;
				if (pa.ttl > macro_n / 2) {// Time To Live
					routing_failed_packets++;
					pa.finished = true;
				}
			}
		}
		if (!next_node_candidate.faulty) {
			MyNode currentnode = getnode(pa.current);
			MyNode nextnode = getnode(b.toString());
			boolean delay = false;
			delay = pathextension(graph, pa, currentnode, nextnode);
			if (!delay) {
				for (int s = place; s < pa.hammingdistance - 1; s++) {
					pa.diff_bit[place] = pa.diff_bit[place + 1];
					place++;
				}
				pa.hammingdistance--;
			}
		}
		if (pa.hammingdistance == 0 || pa.current.equals(pa.desti)) {
			pa.finished = true;
		}
		if (!pa.finished && pa.trial == trial_limit) {
			pa.finished = true;
			routing_failed_packets++;
		}
		return routing_failed_packets;
	}

	public static int gordon_simulation1(DirectedGraph<MyNode, MyEdge> graph,
			Packet[] packet_array) {
		long start = System.currentTimeMillis();
		Collection<MyEdge> edges = graph.getEdges();
		MyEdge[] array2;
		int steps = 0;
		boolean flag = false;
		int routing_failed_packets = 0;
		int node_failed_packets = 0;
		array2 = (MyEdge[]) edges.toArray(new MyEdge[0]);

		for (int j = 0; j < packet_array.length; j++) {
			String origin = packet_array[j].origin;
			String destination = packet_array[j].desti;
			int originnode_int = Integer.parseInt(origin, 2);
			int destination_int = Integer.parseInt(destination, 2);
			MyNode originnode = Data.MyNodes[originnode_int];
			MyNode destinationnode = Data.MyNodes[destination_int];
			if (originnode.faulty) {
				packet_array[j].finished = true;
				node_failed_packets++;
			}
			if (!originnode.faulty && destinationnode.faulty) {
				packet_array[j].finished = true;
				node_failed_packets++;
			} else {// calculating hamming distance
				int hammingdistance = 0;
				for (int p = 0; p < macro_n; p++) {
					String a = origin.substring(p, p + 1);
					String b = destination.substring(p, p + 1);
					if (!a.equals(b)) {
						packet_array[j].diff_bit[hammingdistance] = p;
						/*
						 * Storing bits different from its destination and its
						 * source
						 */
						hammingdistance++;
					}
				}
				packet_array[j].initial_hamming = hammingdistance;
				packet_array[j].hammingdistance = hammingdistance;
			}
		}
		// checking fixed bits
		while (true) {
			for (int j = 0; j < array2.length; j++) {
				array2[j].initialize(); // initializing the edges
			}
			for (int j = 0; j < packet_array.length; j++) {
				if (!packet_array[j].finished) {
					if (!packet_array[j].sleep) {
						routing_failed_packets = gordon_stout_always_sidetracking(
								graph, packet_array[j], routing_failed_packets);
					} else {
						/*
						 * a case when a packet failed from ttl error at the
						 * previous step
						 */
						if (packet_array[j].sleep_int == 0) {
							initializePacket2(packet_array[j]);
							routing_failed_packets = gordon_stout_always_sidetracking(
									graph, packet_array[j],
									routing_failed_packets);
							packet_array[j].sleep = false;
						} else {
							packet_array[j].sleep_int--;
						}
					}
				}
			}
			flag = false;
			for (int j = 0; j < packet_array.length; j++) {
				if (!packet_array[j].finished) {
					flag = true;
					break;
					/*
					 * without the finished variable, we might complete the
					 * packet routing even though some packets are sleeping
					 */
				}
			}
			if (flag)
				steps++;
			if (!flag)
				break;
		}
		long stop = System.currentTimeMillis();
		System.out.append(node_failed_packets + "	" + routing_failed_packets
				+ "	" + steps + "	" + (stop - start));
		return steps;
	}

	public static int nonprobabilistic_sidetracking(
			Graph<MyNode, MyEdge> graph, Packet pa, int routing_failed_packets) {
		int place = (int) ((Math.random() * 1000000) % pa.hammingdistance);
		String a = pa.current.substring(pa.diff_bit[place],
				pa.diff_bit[place] + 1);
		int a_number = Integer.parseInt(a);
		a_number = (a_number + 1) % 2;
		String flipped_bit = String.valueOf(a_number);
		StringBuffer b = new StringBuffer(pa.current);
		b.deleteCharAt(pa.diff_bit[place]);
		b.insert(pa.diff_bit[place], flipped_bit);
		/* b differs only one bit from the current node */
		int nextnode_int = Integer.parseInt(b.toString(), 2);
		MyNode next_node_candidate = Data.MyNodes[nextnode_int];
		if (next_node_candidate.faulty) {
			/* searching for nonfaulty neighbors */
			int checked_bit[] = new int[macro_n];
			int counter = 0;
			for (int l = 0; l < macro_n; l++) {
				// l being the bit to change
				StringBuffer changing = new StringBuffer(pa.current);
				String changing_word = changing.substring(l, l + 1);
				int changing_number = Integer.parseInt(changing_word);
				changing_number = (changing_number + 1) % 2;
				flipped_bit = String.valueOf(changing_number);
				changing.deleteCharAt(l);
				changing.insert(l, flipped_bit);
				MyNode checking_node = Data.MyNodes[Integer.parseInt(
						changing.toString(), 2)];
				if (!checking_node.faulty) {
					checked_bit[counter] = l;
					counter++;
				}
			}
			int random = (int) ((Math.random() * 1000000) % counter);
			pa.diff_bit[pa.hammingdistance] = checked_bit[random];
			StringBuffer c1 = new StringBuffer(pa.current);
			String c = c1.substring(checked_bit[random],
					checked_bit[random] + 1);
			int c_number = Integer.parseInt(c);
			c_number = (c_number + 1) % 2;
			flipped_bit = String.valueOf(c_number);
			c1.deleteCharAt(checked_bit[random]);
			c1.insert(checked_bit[random], flipped_bit);
			MyNode currentnode = getnode(pa.current);
			MyNode nextnode = getnode(c1.toString());
			pathextension(graph, pa, currentnode, nextnode);
			pa.hammingdistance++;
			pa.ttl++;
			if (pa.ttl > 40 * macro_n) {// Time To Live
				routing_failed_packets++;
				pa.finished = true;
			}
		}
		if (!next_node_candidate.faulty) {
			MyNode currentnode = getnode(pa.current);
			MyNode nextnode = getnode(b.toString());
			boolean delay = false;
			delay = pathextension(graph, pa, currentnode, nextnode);
			if (!delay) {
				/*
				 * If delay flag is true, we do not delete any place of bits to
				 * be flipped
				 */
				for (int s = place; s < pa.hammingdistance - 1; s++) {
					pa.diff_bit[place] = pa.diff_bit[place + 1];
					/*
					 * Since one bit is flipped we delete it and shift the
					 * remaining place of bits to be flipped
					 */
					place++;
				}
				pa.hammingdistance--;
			}
		}
		if (pa.hammingdistance == 0 || pa.desti.equals(pa.current)) {
			pa.finished = true;
		}
		if (pa.trial == trial_limit) {
			pa.finished = true;
			routing_failed_packets++;
		}
		return routing_failed_packets;
	}

	public static void nodefailure(Graph<MyNode, MyEdge> graph) {
		Collection<MyNode> vertices1 = graph.getVertices();
		MyNode[] array;
		array = (MyNode[]) vertices1.toArray(new MyNode[0]);
		for (int j = 0; j < array.length; j++) {
			double i = Math.random();
			if (i < macro_p) {
				array[j].faulty = true;
			}
		}
	}

	public static void setranddestination(Packet pa, String[] array, int j) {
		int i = (int) ((Math.random() * 1000000) % macro_N);
		String s = Integer.toBinaryString(i);
		int slength = s.length();
		for (int l = macro_n; l > slength; l--) {
			s = "0" + s;
		}
		pa.desti = s;
	}

	public static DirectedGraph<MyNode, MyEdge> initializeGraph(
			DirectedGraph<MyNode, MyEdge> graph, Packet[] packet_array) {
		Collection<MyNode> vertices1 = graph.getVertices();
		MyNode[] node_array;
		node_array = (MyNode[]) vertices1.toArray(new MyNode[0]);
		for (int j = 0; j < node_array.length; j++) {
			node_array[j].packets = 1;
			Data.MyNodes[j].packets = 1;
		}
		for (int j = 0; j < packet_array.length; j++) {
			initializePacket(packet_array[j]);
		}
		return graph;
	}

	public static void initializePacket(Packet pa) {
		pa.sleep = false;
		pa.sleep_int = 0;
		pa.trial = 0;
		pa.finished = false;
		String origin = pa.origin;
		pa.setcurrent(origin);
		pa.ttl = 0;
	}

	public static void initializePacket2(Packet pa) {
		pa.sleep = false;
		pa.sleep_int = 0;
		String origin = pa.origin;
		String destination = pa.desti;
		pa.setcurrent(origin);
		int hammingdistance = 0;
		for (int p = 0; p < macro_n; p++) {
			String a = origin.substring(p, p + 1);
			String b = destination.substring(p, p + 1);
			/*
			 * storing which bits are different between its source and its
			 * destination to diff_bit array
			 */
			if (!a.equals(b)) {
				pa.diff_bit[hammingdistance] = p;
				hammingdistance++;
			}
		}
		pa.initial_hamming = hammingdistance;
		pa.hammingdistance = hammingdistance;
	}

	public static DirectedGraph<MyNode, MyEdge> createGraph(
			Packet[] packet_array, int counter) {
		DirectedGraph<MyNode, MyEdge> graph = new DirectedSparseGraph<MyNode, MyEdge>();
		MyNode[] vertices = new MyNode[macro_N];
		String[] array1 = new String[macro_N];
		for (int i = 0; i < macro_N; i++) {
			String s = Integer.toBinaryString(i);
			int slength = s.length();
			StringBuilder sb = new StringBuilder();
			for (int l = macro_n; l > slength; l--) {
				sb.append("0");
			}
			sb.append(s);
			s = sb.toString();
			MyNode v = new MyNode(s);
			v.faulty = false;
			v.packets = 1;
			Packet p = new Packet();
			p.trial = 0;
			p.finished = false;
			p.sleep = false;
			p.sleep_int = 0;
			p.setorigin(s);
			p.setcurrent(s);
			setranddestination(p, array1, i);
			packet_array[counter] = p;
			counter++;
			graph.addVertex(v);
			vertices[i] = v;
			/* if the vertices differ in one bit, add an edge between it */
		}
		Data.MyNodes = vertices;
		/* we will create edges and connect nodes which differ exactly one bit */
		boolean flag = false;
		MyNode[] array;
		MyNode currentnode, connectingnode = null;
		Collection<MyNode> vertices1 = graph.getVertices();
		array = (MyNode[]) vertices1.toArray(new MyNode[0]);
		MyNode[] alreadyconnected = (MyNode[]) vertices1.toArray(new MyNode[0]);
		int label = 0;
		for (int j = 0; j < array.length; j++) {
			for (int i = 0; i < macro_n; i++) {
				flag = false;
				currentnode = array[j];
				String a = currentnode.label.substring(i, i + 1);
				/* getting bits starting from the most significant bit */
				StringBuffer b = new StringBuffer(array[j].toString());
				int p = Integer.parseInt(a);
				p = (p + 1) % 2; // mod2 addition to a value of a specific bit
				String s = String.valueOf(p);
				b.deleteCharAt(i);
				b.insert(i, s);
				String d = b.toString();
				connectingnode = Data.MyNodes[Integer.parseInt(d, 2)];
				if (j != 0) {
					for (int l = 0; l < j; l++) {
						/*
						 * for the elements which its index number is smaller
						 * than j, there exist already connected nodes
						 */
						if (connectingnode.label
								.equals(alreadyconnected[l].label)) {
							flag = true;
							/*
							 * if node b is stored in the alreadyconnected
							 * array, skip it
							 */
						}
					}
				}
				if (!flag) {
					MyEdge e1 = new MyEdge("e" + label);
					label++;
					MyEdge e2 = new MyEdge("e" + label);
					label++;
					graph.addEdge(e1, currentnode, connectingnode);
					graph.addEdge(e2, connectingnode, currentnode);
				}
				if (i == macro_n - 1) {
					alreadyconnected[j] = array[j];
				}
			}
		}
		Data.graph = graph;
		return graph;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			Packet[] packet_array = new Packet[macro_N];
			/* number of packets */
			int counter = 0;
			DirectedGraph<MyNode, MyEdge> graph = createGraph(packet_array,
					counter);
			nodefailure(graph);
			simulation1(graph, packet_array);
			graph = initializeGraph(graph, packet_array);
			/*
			 * for implementing 6 distributions, we copy and paste simulation1
			 * and distributed_probability_sidetracking1 and change the
			 * distribution
			 */
			System.out.println();
		}
	}
}