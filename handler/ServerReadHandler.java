package handler;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import server.State;
import server.UnAckedMap;

public class ServerReadHandler implements Handler<SelectionKey> {

	static UnAckedMap unAckMap = new UnAckedMap();
	static Map<Integer, Integer> ackMap = new HashMap<>();
	
	public static State state;
	
	private final Map<DatagramChannel, Queue<ByteBuffer>> pendingData;
	
	public ServerReadHandler() {
		this.pendingData = new HashMap<>();
		ServerReadHandler.state = State.SLOW;
	}

	@Override
	public void handle(SelectionKey key) throws IOException {
		
		ByteBuffer buffer = ByteBuffer.allocate(1500);
		DatagramChannel channel = (DatagramChannel) key.channel();
		channel.configureBlocking(false);
		SocketAddress addr = channel.receive(buffer);
		key.attach(addr);
		Queue<ByteBuffer> queue = new ArrayDeque<>();
		queue.add(buffer);
		pendingData.put(channel, queue);
		key.interestOps(SelectionKey.OP_WRITE);

	}
	
}
