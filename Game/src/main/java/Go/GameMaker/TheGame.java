package Go.GameMaker;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class TheGame {

	private static final Object LOCK = new Object();
	private static volatile TheGame instance;

	// tablica plansz
	private Map<String, Board> boards;

	// mapa parująca ID clienta z jego kolorem
	private Map<String, Markers> colors;

	// mapa parująca graczy ze sobą
	private Map<String, String> playerPairs;

	private TheGame() { // inicializacja
		colors = new HashMap<String, Markers>();
		playerPairs = new HashMap<String, String>();
		boards = new HashMap<String, Board>();
	}

	public static TheGame getInstance() { // double checker metoda statyczna na zwracanie singletona
		TheGame result = instance;
		if (result == null) {
			synchronized (LOCK) {
				result = instance;
				if (result == null)
					instance = result = new TheGame();
			}
		}
		return result;
	}

	public String addPlayer(String clientID, int size) {
		for(Entry<String, Board> board: boards.entrySet()) {
			Board b = board.getValue();
			if(b.getSize() == size && !b.arePlayersFound()) {
				boards.put(clientID, b);
				playerPairs.put(clientID, b.getHostID());
				Markers color = b.getGameState().asEnemy();
				colors.put(clientID, color);
				b.setPlayersFound(true);
				return "Succes;" + color.asString();
			}
		}
		Board b = new Board(size);
		b.setHostID(clientID);
		int i = new Random().nextInt(2);
		Markers playerColor = Markers.getColor(i);
		b.setGameState(playerColor);
		boards.put(clientID, b);
		colors.put(clientID, playerColor);
		return "Succes;" + playerColor.asString();

	}

	public String makeMove(String move) {

		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Markers playerColor = colors.get(clientID);
		Board board = boards.get(clientID);

		// kontynuacja gry pomimo pasowania
		if (board.getGameState() == playerColor.asEnemy().asPassed())
			board.setGameState(playerColor);

		if (board.getGameState() == playerColor) {
			int pointsScored = board.insert(x, y, playerColor);
			if (pointsScored >= 0) {
				return Integer.toString(pointsScored) + ";" + board.boardToString();
			} else
				return "IllegalMove";
		} else
			return "NotYrMove";

	}

	public String getGameState(String clientID) {
		Board b = boards.get(clientID);
		return b.getGameState().asString() + ";" + b.boardToString();
	}

	public String getPlayerWhoAccepted(String clientID) {
		Board b = boards.get(clientID);
		return b.getPlayerWhoAccepted().asAccepted().asString();
	}

	public void skip(String clientID) {
		Board board = boards.get(clientID);
		Markers playerColor = colors.get(clientID);
		if (board.getGameState() != playerColor.asEnemy().asPassed()) {
			board.setGameState(playerColor.asPassed());

		} else
			board.setGameState(Markers.BOTHPASSED);

	}

	public void acceptStage(String clientID) {
		Board b = boards.get(clientID);
		Markers playerColor = colors.get(clientID);
		if (b.getPlayerWhoAccepted() == playerColor.asEnemy())
			b.confirmChanges();
		else
			b.setGameResultAccepted(playerColor);

	}

	public void pickDeadStones(String move) {
		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Board board = boards.get(clientID);
		if (!board.isGameResultAccepted())
			board.markDeadStones(x, y);

	}

	public void pickTerritory(String move) {
		String[] splittedCommand = move.split(",");
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Markers playerColor = colors.get(clientID);
		Board board = boards.get(clientID);
		if (!board.isGameResultAccepted())
			board.claimTerritory(x, y, playerColor);
	}

	public void cancelVote(String clientID) {
		boards.get(clientID).restoreBoard();
	}

	public void exit(String clientID) { // czyszczenie map
		giveUp(clientID);
		playerPairs.remove(clientID);
		colors.remove(clientID);
		boards.remove(clientID);

	}

	public void giveUp(String clientID) {
		Board board = boards.get(clientID);
		Markers playerColor = colors.get(clientID);
		if (board.getGameState() != playerColor.asWinner() && board.getGameState() != playerColor.asEnemy().asWinner())
			board.setGameState(playerColor.asEnemy().asWinner());

	}
	
	public String getEnemyID(String clientID) {
		String enemyID;
		if (playerPairs.containsKey(clientID)) {
			enemyID = playerPairs.get(clientID);

		} else if (playerPairs.containsValue(clientID)) {
			enemyID = getKeyByValue(playerPairs, clientID);

		} else
			enemyID = "NoSuchPlayer";
		return enemyID;

	}

	private <T, E> T getKeyByValue(Map<T, E> map, E value) { // odzyskiwanie klucza z wartości
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Board getBoard(String clientID) {
		return boards.get(clientID);
	}

	
	public String getPoints(String clientID) {
		String enemyID = getEnemyID(clientID);
		Markers playerColor = colors.get(clientID);
		Markers enemyColor = colors.get(enemyID);
		int playerPoints = boards.get(clientID).getPoints(playerColor);
		int enemyPoints = boards.get(enemyID).getPoints(enemyColor);
		return playerColor.asString() + ";" + String.valueOf(playerPoints) + ";" + enemyColor.asString() + ";" + String.valueOf(enemyPoints);
	}

	public Map<String, Markers> getColors() {
		return colors;
	}

	public Map<String, String> getPlayerPairs() {
		return playerPairs;
	}

}
