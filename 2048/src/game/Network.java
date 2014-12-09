package game;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/*
 * Keeps things common to both the client and server.
 */
public class Network {
	static public final int port = 54555;

	/**
	 * Registers objects that are going to be sent over the network.
	 * @param endPoint - EndPoint object which gets the Kryo instance that
	 * the object will be registered to
	 */
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(TileRequest.class);
		kryo.register(int[][].class);
		kryo.register(int[].class);
		kryo.register(Score.class);
		kryo.register(Boolean.class);
	}

	/*
	 * Handles the state of players choosing where tiles go on their opponent's
	 * board
	 */
	static public class TileRequest {
		public int row;
		public int col;
		public int value;
	}
	
	/*
	 * Allows the scores to be sent back and forth across the network
	 */
	static public class Score {
		public int value;
	}
	
	/*
	 * Allows for the GameBoard to keep track of the current turn
	 */
	static public class MyTurn {
		public boolean value;
	}
	
}
