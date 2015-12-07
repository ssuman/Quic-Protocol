package client;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import handler.Handler;

public class ClientReadHandler implements Handler<SelectionKey> {

	static Map<DatagramChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
	
	public ClientReadHandler(Map<DatagramChannel, Queue<ByteBuffer>> pendingData) {
		this.pendingData = pendingData;
	}

	@Override
	public void handle(SelectionKey key) throws IOException {


	}

}
