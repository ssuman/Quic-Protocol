package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import handler.Handler;

public class WriteHandler implements Handler<SelectionKey> {

	private final Map<DatagramChannel, Queue<ByteBuffer>> pendingData;
	
	public WriteHandler(Map<DatagramChannel, Queue<ByteBuffer>> pendingData) {
		this.pendingData = pendingData;
	}
	
	public WriteHandler() {
		this.pendingData = new HashMap<>();
	}

	@Override
	public void handle(SelectionKey key) throws IOException {

	}

}
