package client;
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

public class TCP {

	private final static int PORT_NUM = 10000;
	private static Map<DatagramChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();

	public static void main(String[] args) throws IOException {

		DatagramChannel channel = DatagramChannel.open();
		channel.configureBlocking(false);
		SocketAddress address = new InetSocketAddress(PORT_NUM);
		DatagramSocket socket = channel.socket();
		socket.bind(address);

		Selector selector = Selector.open();
		channel.register(selector, SelectionKey.OP_READ);

		while (true) {
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iter = keys.iterator();

			while (iter.hasNext()) {
				SelectionKey key = iter.next();
				iter.remove();
				if (key.isReadable()) {
					new ClientReadHandler(pendingData).handle(key);

				} else if (key.isWritable()) {

					new ClientWriteHandler(pendingData).handle(key);
				}
			}
		}

	}
}
