package server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import handler.ServerReadHandler;
import handler.ServerWriterHandler;

public class TCPServer {

	static Map<DatagramChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
	final static int BUFFER_SIZE = 1000;
	final static int PORT_NUM = 8001;

	public TCPServer() {

	}

	/***
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length != 2) {
			System.out.println("Usage: java TCPServer");
		}
		TCPServer server = new TCPServer();
		Selector sel = server.start();
		server.receiveRequest(sel);
	}

	private Selector start() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
		channel.configureBlocking(false);
		SocketAddress address = new InetSocketAddress(PORT_NUM);
		DatagramSocket socket = channel.socket();
		socket.bind(address);
		Selector selector = Selector.open();
		return selector;
	}

	private void receiveRequest(Selector sel) throws IOException, ClassNotFoundException {
		Selector selector = sel;

		while (true) {
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> keyIter = keys.iterator();
			while (keyIter.hasNext()) {
				SelectionKey key = keyIter.next();
				keyIter.remove();
				if (key.isReadable()) {
					
					new ServerReadHandler().handle(key);
					
				} else if (key.isWritable()) {
					
					new ServerWriterHandler().handle(key);
				}
			}
		}
	}

}
