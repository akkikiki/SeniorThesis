package samples.graph;

import java.util.Stack;

public class Packet {
	static int macro_n = 11;
	static int macro_N = (int) Math.pow(2, macro_n);
	int diff_bit[] = new int[(int) (100 * macro_n + 1)];
	int ttl;
	int hammingdistance;
	int initial_hamming;
	int trial;
	String origin;
	String current;
	String desti;
	public boolean two_step_extended;
	public boolean bfs_extended;
	String two_step_next;
	public Stack<String> bfsnodes;
	int bitfixed = 0;
	
	public String readorigin() {
		return origin;
	}

	public String readdest() {
		return desti;
	}

	public String readcurrent() {
		return current;
	}

	public void setorigin(String s) {
		origin = s;
	}

	public void setdest(String s) {
		desti = s;
	}

	public void setcurrent(String s) {
		current = s;
	}

    public int readingbit(){
    	return bitfixed;
    }
    public void changingbit(int i){
    	bitfixed = i;
    }
}
