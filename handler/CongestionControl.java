package handler;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import Segment.Segment;

public class CongestionControl {
	
	protected Segment seg;
	protected Map<DatagramChannel, Queue<ByteBuffer>> pendingData;
	protected List<ByteBuffer> itemQueue;
	protected DatagramChannel channel;

}
