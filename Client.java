import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {

	static InetAddress address;

	public static void main(String[] args) throws UnknownHostException, InterruptedException {
		address = InetAddress.getLocalHost();

		while (true) {
			if (!address.equals(InetAddress.getLocalHost())) {
				address = InetAddress.getLocalHost();
				System.out.println(InetAddress.getLocalHost());
				System.out.println("True");
			}
			System.out.println(InetAddress.getLocalHost());
			Thread.sleep(10000);
		}
	}
}
