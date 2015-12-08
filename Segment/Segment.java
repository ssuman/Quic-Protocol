package Segment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import server.Flags;

public class Segment implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public int sourcePort;
	public int destPort;
	public String sourceIPAddr;
	public String destIPAddr;
	public int sequenceNbr;
	public int acknowledgementNbr;
	public int dataOffset;
	public Flags flag;
	public int windowSize;
	public int checksum;
	public List<Integer> NACKs;
	public String connectionId;
	public byte[] data;
	public String filename;
	
	
	public Segment() {
		
	}
	
	public Segment(String connectionId, int sourcePort, int destPort, String sourceIPAddr, String destIPAddr, int sequenceNbr,
			int acknowledgementNbr, int dataOffset, Flags flag, int windowSize, int checksum, byte[] data) {
		super();
		this.connectionId = connectionId;
		this.sourcePort = sourcePort;
		this.destPort = destPort;
		this.sourceIPAddr = sourceIPAddr;
		this.destIPAddr = destIPAddr;
		this.sequenceNbr = sequenceNbr;
		this.acknowledgementNbr = acknowledgementNbr;
		this.dataOffset = dataOffset;
		this.flag = flag;
		this.windowSize = windowSize;
		this.checksum = checksum;
		this.data = data;
		this.NACKs = new ArrayList<>();
		
	}
	
	
	public static byte[] serializeToBytes(Segment seg) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try{
			out  = new ObjectOutputStream(bos);
			out.writeObject(seg);
			out.flush();
			
		} finally {
			out.close();
			bos.close();
		}
		return bos.toByteArray();
	}
	
	public static Segment deserializeBytes(ByteBuffer buffer) throws IOException, ClassNotFoundException{
		
		buffer.flip();
		byte [] bytes  = new byte[buffer.limit()];
		buffer.get(bytes);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream in = null;
		Segment seg = null;
		try{
			in = new ObjectInputStream(bis);
			seg = (Segment) in.readObject();
		}finally{
			in.close();
			bis.close();
		}
		return seg;	
	}

	public static Segment createSegment(Flags flag, byte[] bytes, int seq, int ack) {
		Segment seg = new Segment();
		seg.flag = flag;
		
		return seg;
	}
	
}
