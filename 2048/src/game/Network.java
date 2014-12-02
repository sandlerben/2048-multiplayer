package game;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

// Keeps things common to both the client and server.
public class Network {
	static public final int port = 54555;

	// Registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(TileRequest.class);
		kryo.register(int[][].class);
		kryo.register(int[].class);
		kryo.register(Score.class);
		kryo.register(Boolean.class);
	}

	static public class TileRequest {
		public int row;
		public int col;
		public int value;
	}
	
	static public class Score {
		public int value;
	}
	
	// TODO remove
	static public class MyTurn {
		public boolean value;
	}

}
