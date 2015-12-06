package handler;
import java.io.IOException;

public interface Handler<X>{
	public void handle(X x) throws IOException;
	
}
