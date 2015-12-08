package client;

import java.nio.ByteBuffer;

public class Blocks implements Comparable<Blocks> {

	int seqNbr;
	ByteBuffer buffer;

	
	public Blocks(int seqNbr, ByteBuffer buffer) {
		super();
		this.seqNbr = seqNbr;
		this.buffer = buffer;
	}


	@Override
	public int compareTo(Blocks o) {
		if (this.seqNbr < o.seqNbr)
			return -1;
		else if (this.seqNbr > o.seqNbr)
			return 1;
		return 0;
	}

}
