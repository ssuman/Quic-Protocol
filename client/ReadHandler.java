package client;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

import handler.Handler;

public class ReadHandler implements Handler<SelectionKey> {

	public ReadHandler() {
		
	}

	@Override
	public void handle(SelectionKey key) throws IOException {


	}

}
