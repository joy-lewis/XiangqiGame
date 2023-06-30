package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import de.tuberlin.sese.swtpp.gameserver.model.*;
//TODO: more imports from JVM allowed here

import java.awt.print.Printable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class XiangqiGame extends Game implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5424778147226994452L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign red and black player
	private Player blackPlayer;
	private Player redPlayer;

	// internal representation of the game state
	// TODO: insert additional game data here
	private char[][] gameBoard;
	//list of int arrays, to save the fields which are in between start and targed coordination
	private List<int[]> advisorR;
	private List<int[]> advisorB;
	private List<int[]> generalR;
	private List<int[]> generalB;
	private int[] rGeneral;
	private int[] bGeneral;
	private char previousPiece;
	private List<int[]> allFields; // all board coordiantes
	
	/************************
	 * constructors
	 ***********************/

	public XiangqiGame() {
		super();

		// TODO: initialization of game state can go here
		//gameBoard[3][2] = S - scnd soldier from left top 
		
		gameBoard = new char[][] 	{{'R', 'H', 'E', 'A', 'G', 'A', 'E', 'H', 'R'},
            						{'0', '0', '0', '0', '0', '0', '0', '0', '0'},
            						{'0', 'C', '0', '0', '0', '0', '0', 'C', '0'},
            						{'S', '0', 'S', '0', 'S', '0', 'S', '0', 'S'},
            						{'0', '0', '0', '0', '0', '0', '0', '0', '0'},
            						{'0', '0', '0', '0', '0', '0', '0', '0', '0'},
            						{'s', '0', 's', '0', 's', '0', 's', '0', 's'},
            						{'0', 'c', '0', '0', '0', '0', '0', 'c', '0'},
            						{'0', '0', '0', '0', '0', '0', '0', '0', '0'},
            						{'r', 'h', 'e', 'a', 'g', 'a', 'e', 'h', 'r'}};
            						
        advisorInit();
        generalInit();
        allFieldsInit();
	}
	
	public void advisorInit(){
		advisorR = new ArrayList<int[]>();
	    advisorB = new ArrayList<int[]>();
	    generalR = new ArrayList<int[]>();
	    generalB = new ArrayList<int[]>();
	       
	    // add all possible fields for red advisor to the list 
	    advisorR.add(new int[] {0,3});
	    advisorR.add(new int[] {2,3});
	    advisorR.add(new int[] {1,4});
	    advisorR.add(new int[] {0,5});
	    advisorR.add(new int[] {2,5});
	       
	    // add all possible fields for black advisor to the list 
	    advisorB.add(new int[] {9,3});
	    advisorB.add(new int[] {7,3});
	    advisorB.add(new int[] {8,4});
	    advisorB.add(new int[] {9,5});
	    advisorB.add(new int[] {7,5});
	}
	
	public void generalInit() {
	    // add all possible fields for both generals to the list 
	    for (int i = 0; i <= 2; i++) {
	        for (int j = 3; j <= 5; j++) {
	   	        generalR.add(new int[] {i,j});
	   	        generalB.add(new int[] {i+7,j});
	        }
	    }
	       
	    // default position of generals
	    rGeneral = new int[2];
	    bGeneral = new int[2];
	    rGeneral[0] = 0;
	    rGeneral[1] = 4;
	    bGeneral[0] = 9;
	    bGeneral[1] = 4;
	}
	
	public void allFieldsInit() {
	       // add all fields on the board to a list
	       allFields = new ArrayList<int[]>();
	       
	       for (int row = 0; row <= 9; row++) {
				for (int col = 0; col <= 8; col++) {
					allFields.add(new int[] {row, col});
				}
			}
	}

	public String getType() {
		return "xiangqi";
	}

	/*******************************************
	 * Game class functions already implemented
	 ******************************************/

	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);

			// game starts with two players
			if (players.size() == 2) {
				started = true;
				this.redPlayer = players.get(0);
				this.blackPlayer= players.get(1);
				nextPlayer = redPlayer;
			}
			return true;
		}

		return false;
	}

	@Override
	public String getStatus() {
		if (error)
			return "Error";
		if (!started)
			return "Wait";
		if (!finished)
			return "Started";
		if (surrendered)
			return "Surrendered";
		if (draw)
			return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if (started) {
			if (blackGaveUp())
				gameInfo = "black gave up";
			else if (redGaveUp())
				gameInfo = "red gave up";
			else if (didRedDraw() && !didBlackDraw())
				gameInfo = "red called draw";
			else if (!didRedDraw() && didBlackDraw())
				gameInfo = "black called draw";
			else if (draw)
				gameInfo = "draw game";
			else if (finished)
				gameInfo = blackPlayer.isWinner() ? "black won" : "red won";
		}

		return gameInfo;
	}

	@Override
	public String nextPlayerString() {
		return isRedNext() ? "r" : "b";
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}

	@Override
	public boolean callDraw(Player player) {

		// save to status: player wants to call draw
		if (this.started && !this.finished) {
			player.requestDraw();
		} else {
			return false;
		}

		// if both agreed on draw:
		// game is over
		if (players.stream().allMatch(Player::requestedDraw)) {
			this.draw = true;
			finish();
		}
		return true;
	}

	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.redPlayer == player) {
				redPlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				redPlayer.setWinner();
			}
			surrendered = true;
			finish();

			return true;
		}

		return false;
	}

	/* ******************************************
	 * Helpful stuff
	 ***************************************** */

	/**
	 *
	 * @return True if it's red player's turn
	 */
	public boolean isRedNext() {
		return nextPlayer == redPlayer;
	}

	/**
	 * Ends game after regular move (save winner, finish up game state,
	 * histories...)
	 *
	 * @param winner player who won the game
	 * @return true if game was indeed finished
	 */
	public boolean regularGameEnd(Player winner) {
		// public for tests
		if (finish()) {
			winner.setWinner();
			winner.getUser().updateStatistics();
			return true;
		}
		return false;
	}

	public boolean didRedDraw() {
		return redPlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean redGaveUp() {
		return redPlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/***********************************************************
	 * !!!!!!!!! FEN->BOARD | BOARD->FEN CONVERTER !!!!!!!!!!!!*
	 ***********************************************************/
	
	/* Convert the FEN-String to the board*/
	
    public void stringToArray(String fenAbschnitt, int zeile) {
    	// beinhaltet die Spalte
        int stringZaehler = 0;
        // loop trough every char 
        for (char c : fenAbschnitt.toCharArray()) {
        	// if c is an digit - just empty fields on the boards
            if (Character.isDigit(c)) {
            	// get the number of the digit - ASCII ex: if c = 3 -> ASCII number is 51 and 0 -> 49
            	// so 51 - 49 = 3 wich equals to c
                int a = c - '0';
                // set 0 a-times
                for (int i = 0; i < a; i++) {
                    gameBoard[zeile][stringZaehler] = '0';
                    stringZaehler++;
                }
            }
            // if c is not an digit - set the character into the right field
            else {
                gameBoard[zeile][stringZaehler] = c;
                stringZaehler++;
            }
        }
    }
    
    /* Convert the gameBoard into a FEN-String*/
    
	private String arrayToString(char[] row) {
		StringBuilder fen = new StringBuilder();
		
		int freeFields = 0;
		// s = a single row in the gameBoard
		for (char c : row) {
			// count he freeFields
			if (c == '0') freeFields++;
			// if the next is a figure, add the number of the freeFields to the string and then the figure
			else {
				if (freeFields!= 0) fen.append(freeFields);
				fen.append(c);
				freeFields = 0;
			}
		}
		// add the freeFieds to the string, in case there is no other figure, who would trigger the else statement - 253
		if (freeFields!= 0) fen.append(freeFields);
		
		return fen.toString();
	}
	
	@Override
	public void setBoard(String state) {
		// Note: This method is for automatic testing. A regular game would not start at some artificial state.
		//       It can be assumed that the state supplied is a regular board that can be reached during a game.
		// TODO: implement
		String[] splittedState = state.split("/");
		int j = 0;
		for (int i = 9; i >= 0; i--) {
			stringToArray(splittedState[j], i);
			j++;
		}
		// search the fields for the general
		for (int i = 0; i <= 2; i++) {
	       for (j = 3; j <= 5; j++) {
	    	   if (gameBoard[i][j] == 'G') {
	    		   rGeneral[0] = i; 
	    		   rGeneral[1] = j;
	    	   } 
	    	   if (gameBoard[i+7][j] == 'g') {
	    		   bGeneral[0] = i+7; 
	    		   bGeneral[1] = j;
	    		   }
	    	   }
	      }
	}

	/* Build the string, with the method "arrayToString"
	 * Ensure the backslash is not added ad the end of the last row
	 */
	
	@Override
	public String getBoard() {
		// TODO: implement
		int i = 0;
		StringBuilder fen = new StringBuilder();
		
        for (char[] row : gameBoard) {
        	fen.insert(0,arrayToString(row));
    		if (i != 9) fen.insert(0,"/");
    		i++;
        } 
    
        return fen.toString();
	}

	/* Convert the given string to an int[] to work with it
	 * @param coord[0] & coord[1] start position
	 * @param coord[2] & coord[3] target position
	 * rows and column reversed from the given specification - first row then column
	 */
	
	private int[] stringToInt(String moveString) {
		int[] coord = new int[4];
		
		coord[0] = Character.getNumericValue(moveString.charAt(1));
		coord[1] = moveString.charAt(0) - 97;
		coord[2] = Character.getNumericValue(moveString.charAt(4));
		coord[3] = moveString.charAt(3) - 97;

		return coord;
	}

	/* check if field for the given coord's is empty */
	
	private boolean checkField(int[] coord) {
		if (gameBoard[coord[0]][coord[1]] == '0') {
			return true;
		} else return false;
	}
	
	/**********************************************************
	 * !!!!!!!!! ALL PROPERTIES OF POSSIBLE MOVES !!!!!!!!!!!!*
	 **********************************************************/
	
	// move horizontally along coord[0]
	private boolean horizontal(int[] coord) {
		int column = coord[1];
		if (coord[0] == coord[2]) {
			for (int i = 0; i < Math.abs(coord[1]-coord[3]); i++) {
				if (column > coord[3]) column -= 1; else column += 1;
				if (column == coord[3]) return true;
				if (!checkField(new int[] {coord[0], column})) return false;
			}
		}
		return false; 
	}
	
	// move vertically along coord[1]
	private boolean vertical(int[] coord) {
		int row = coord[0];
		if (coord[1] == coord[3]) {
			for (int i = 0; i < Math.abs(coord[0]-coord[2]); i++) {
				if (row > coord[2]) row -= 1; else row += 1;
				if (row == coord[2]) return true;
				if (!checkField(new int[] {row, coord[1]})) return false;
			}
		} 
		return false; 
	}
	
	/** VERTICAL
	 * count all figures which are in between the start and target position
	 * if its only one counted figure return true
	 */
	
	private boolean verticalSchanzenstein(int[] coord) {
		int anzahlSchanzen = 0;
		int row = coord[0];
		if (coord[1] == coord[3]) {
			for (int i = 0; i < Math.abs(coord[0]-coord[2]); i++) {
				if (row > coord[2]) row -= 1; else row += 1;
				if (row != coord[2] && gameBoard[row][coord[1]] != '0') anzahlSchanzen++;
			}
			if (anzahlSchanzen == 1) return true;
		} 
		return false; 
	}
	
	/** HORIZONTAL
	 * count all figures which are in between the start and target position
	 * if its only one counted figure return true
	 */
	private boolean horizontalSchanzenstein(int[] coord) {
		int anzahlSchanzen = 0;
		int column = coord[1];
		if (coord[0] == coord[2]) {
			for (int i = 0; i < Math.abs(coord[1]-coord[3]); i++) {
				if (column > coord[3]) column -= 1; else column += 1;
				if (column != coord[3] && gameBoard[coord[0]][column] != '0') anzahlSchanzen++;
			}
			if (anzahlSchanzen == 1) return true;
		} 
		return false; 
	}
	
	/** Only exact two diogonal steps allowed
	 * The first field has to be free
	 */
	
	private boolean twoStepsDiogonal(int[] coord) {
		
		if (coord[2] == coord[0]+2 && coord[3] == coord[1]+2) return checkField(new int[] {coord[0]+1, coord[1]+1});
		if (coord[2] == coord[0]-2 && coord[3] == coord[1]+2) return checkField(new int[] {coord[0]-1, coord[1]+1});
		if (coord[2] == coord[0]+2 && coord[3] == coord[1]-2) return checkField(new int[] {coord[0]+1, coord[1]-1});
		if (coord[2] == coord[0]-2 && coord[3] == coord[1]-2) return checkField(new int[] {coord[0]-1, coord[1]-1});
		
		return false;
	}
	
	/** possible moves for the horse, for first horizontal step
	 * check if the first step field is empty
	 */
	
	private boolean horseFirstVertical(int[] coord) {
		if ((coord[2] == coord[0]+2 && coord[3] == coord[1]+1) || (coord[2] == coord[0]+2 && coord[3] == coord[1]-1)) {
			return checkField(new int[] {coord[0]+1, coord[1]});
		} else if ((coord[2] == coord[0]-2 && coord[3] == coord[1]+1) || (coord[2] == coord[0]-2 && coord[3] == coord[1]-1)) {
			return checkField(new int[] {coord[0]-1, coord[1]});
		} else { return false; }
	}
	
	/** possible moves for the horse, for first vertical step
	 * check if the first step field is empty
	 */
	
	private boolean horseFirstHorizontal(int[] coord) {
		if ((coord[3] == coord[1]+2 && coord[2] == coord[0]+1) || (coord[3] == coord[1]+2 && coord[2] == coord[0]-1)) {
			return checkField(new int[] {coord[0], coord[1]+1});
		} else if ((coord[3] == coord[1]-2 && coord[2] == coord[0]+1) || (coord[3] == coord[1]-2 && coord[2] == coord[0]-1)) {
			return checkField(new int[] {coord[0], coord[1]-1});
		} else { return false; }
	}
	
	/** @param list of possible moves from the respective player
	 * check if the coord's are existing in the list
	 * check if its only a one diagonal step
	 */
	
	private boolean advisor(int[] coord) {
		
		List<int[]> list = new ArrayList<int[]>();
		
		if (gameBoard[coord[0]][coord[1]] == 'A') list = advisorR;
		else list = advisorB;
		
		for (int[] advisorM : list) {
			if (advisorM[0] == coord[2] && advisorM[1] == coord[3] && 
					Math.abs(advisorM[0] - coord[0]) == 1 /*&& Math.abs(advisorM[1] - coord[1]) == 1*/) return true;
		}

		return false;
	}
	
	/** RED SOLDIER
	 * check if the soldier makes one horizontal step on his half site
	 * check if the soldier makes one horizontal step, or a diagonal one step
	 */
	
	private boolean soldierR(int[] coord) {
		if (coord[2]<coord[0])return false;  // one step back
		if (coord[0] >= 5 && coord[2] == coord[0] && Math.abs(coord[3]-coord[1]) == 1) return true;
		return (coord[2] == coord[0]+1 && coord[3] == coord[1]);
	}
	
	/** BLACK SOLDIER
	 * check if the soldier makes one horizontal step on his half site
	 * check if the soldier makes one horizontal step, or a diagonal one step
	 */
	
	private boolean soldierB(int[] coord) {
		if (coord[2]>coord[0])return false;  // one step back
		if (coord[0] <= 4 && coord[2] == coord[0] && Math.abs(coord[3]-coord[1]) == 1) return true;
		return (coord[2] == coord[0]-1 && coord[3] == coord[1]);
	}
	
	private boolean cannon(int[] coord) {
		
		if (gameBoard[coord[2]][coord[3]] == '0') {
			if (vertical(coord) || horizontal(coord)) return true;
		} 
		else if (verticalSchanzenstein(coord) || horizontalSchanzenstein(coord)) return true;
		
		return false;
	}
	
	/* Elephant can't leave his half of the board */
	private boolean elephantCantLeaveOwnHalf(int[] coord) {
		if(gameBoard[coord[0]][coord[1]] == 'e' && 
				coord[2] >= 5) return true;
		else if(gameBoard[coord[0]][coord[1]] == 'E' && 
				coord[2] <= 4) return true;
		else { return false; } 
	}
	
	private boolean rook(int[] coord) {
		if (vertical(coord)) return true;
		else if (horizontal(coord)) return true;
		return false;
	}
	
	private boolean soldier(int[] coord) {
		if (gameBoard[coord[0]][coord[1]] == 'S') {
			return (soldierR(coord));
		} 
		return soldierB(coord);
	}
	
	private boolean horse(int[] coord) {
		
		if (horseFirstHorizontal(coord)) return true;
		
		return horseFirstVertical(coord);
	}
	
	private boolean elephant(int[] coord) {
		
		if (twoStepsDiogonal(coord)) return elephantCantLeaveOwnHalf(coord);
		
		return false;
	}
	
	private boolean general(int[] coord) {
		return generalPalast(coord);
	}
	
	/** @param list of possible moves from the respective player
	 * check if the coord's are existing in the list
	 * check if its a one diagonal step or one vertical/horizontal step
	 */
	
	private boolean generalPalast(int[] coord) {
		
		List<int[]> list = new ArrayList<int[]>();
		if (gameBoard[coord[0]][coord[1]] == 'G') list = generalR;
		else list = generalB;

		for (int[] advisorM : list) {
			if ( advisorM[0] == coord[2] && advisorM[1] == coord[3] && 
			   ( Math.abs(advisorM[0]-coord[0]) == 1 && advisorM[1] == coord[1] ||
			     Math.abs(advisorM[1]-coord[1]) == 1 && advisorM[0] == coord[0])) return true;
		}

		return false;
	}
	

	/** Todesblick rule
	 * @param coord
	 * @return
	 */
	private boolean flyingGeneralRule(int[] coord) {
		
		if (Character.toLowerCase(gameBoard[coord[0]][coord[1]]) == 'g') {
			if (gameBoard[coord[0]][coord[1]] == 'G') {
				return vertical(new int[] {coord[2], coord[3], bGeneral[0], bGeneral[1]});
			} else {
				return vertical(new int[] {coord[2], coord[3], rGeneral[0], rGeneral[1]});
			}
		} else if ((coord[1] == 3 || coord[1] == 4 || coord[1] == 5) && coord[1] != coord[3] && rGeneral[1] == bGeneral[1]) {
			char fig = gameBoard[coord[0]][coord[1]];
			gameBoard[coord[0]][coord[1]] = '0';
			boolean x = vertical(new int[] {rGeneral[0], rGeneral[1], bGeneral[0], bGeneral[1]});
			gameBoard[coord[0]][coord[1]] = fig;
			if (x) return true;
		}
		
		return false;
	}
	
	/** Tracks both generals positions during the game
	 * @param coord
	 */
	private void trackGeneral(int[] coord) {
		if (gameBoard[coord[0]][coord[1]] == 'G') {
			rGeneral[0] = coord[2];
			rGeneral[1] = coord[3];
		} else {
			bGeneral[0] = coord[2];
			bGeneral[1] = coord[3];
		}
	}
	
	/** Tracks both generals positions when undoing a move
	 * @param coord
	 */
	private void unTrackGeneral(int[] coord) {
		if (gameBoard[coord[0]][coord[1]] == 'G') {
			rGeneral[0] = coord[0];
			rGeneral[1] = coord[1];
		} else {
			bGeneral[0] = coord[0];
			bGeneral[1] = coord[1];
		}
	}
	
	/** Checks if general of opposing player is in checkmate
	 * @param coord: coordinates of player who's turn it is
	 * @return true if general is in checkmate
	 */
	private void inCheckMate(int[] coord, Player player) {
		Player p; // this player
		Player op; // opposite player
		if (player == redPlayer) {
			p = redPlayer;
			op = blackPlayer;
		}
		else {
			p = blackPlayer;
			op = redPlayer;
		}
		if (!anyLegalMoves(op)) { // check if any no legal moves are left
			if (generalIsAttacked(coord, op, false)) { 
				// no legal moves for opposite player and opposite player is attacked -> this player wins
				regularGameEnd(p); 
			} else {  
				// no legal moves for opposite player but opposite player is not attacked -> draw and this player wins 
				callDraw(op); 
				this.draw = true;
				regularGameEnd(p);
			}
		}
	}
	
	/** Checks if move of current player would put own general in chess
	 * 
	 * @param coord move of player
	 * @return true if the generals is in chess
	 */
	private boolean generalIsAttacked(int[] coord, Player player, Boolean makeMove) {
		if (makeMove) doMove(coord);  // execute move at first
		char enemy = ' ';  // an enemy piece
		char enemy2 = ' '; // another enemy piece
		int[] gCoord;      // general's coordinates
		
		if (player == redPlayer) {
		    gCoord = rGeneral;
		} else {
			gCoord = bGeneral;
		}
        if (soldierAttacks(gCoord, player)) return true; // soldier attacks?
        if (horseAttacksHor(gCoord, enemy, player)) return true; // horse attacks horizontally?
        if (horseAttacksVert(gCoord, enemy, player)) return true; // horse attacks vertically?

        if (player == redPlayer) {
			enemy = 'r';
			enemy2 = 'c';
		} else {
			enemy = 'R';
			enemy2 = 'C';
		}
		return rcAttacks(gCoord, enemy, enemy2); // rook or cannon attacks?
	}
	
	/** Checks if soldier attacks
	 * @param gCoord
	 * @param coord
	 * @return true if general is attacked by soldier
	 */
	private boolean soldierAttacks(int[] gCoord, Player player) {
		if (player == redPlayer) {
			// soldier attacks? (3)
			if (gameBoard[gCoord[0]][gCoord[1]+1] == 's' || 
				gameBoard[gCoord[0]][gCoord[1]-1] == 's' || 
				gameBoard[gCoord[0]+1][gCoord[1]] == 's') {
				return true;
			}
		}
		else {
			// soldier attacks? (3)
			if (gameBoard[gCoord[0]][gCoord[1]+1] == 'S' || 
				gameBoard[gCoord[0]][gCoord[1]-1] == 'S' || 
				gameBoard[gCoord[0]-1][gCoord[1]] == 'S') {
				return true;
			}
		}
		return false;
	}
	
	/** Checks if horse attacks 
	 * @param gCoord
	 * @param coord
	 * @param enemy
	 * @return true if general is attacked by horse
	 */
	private boolean horseAttacksHor(int[] gCoord, char enemy, Player player) {
		//if (Character.isUpperCase(gameBoard[coord[2]][coord[3]])) enemy = 'h';
		if (player == redPlayer) enemy = 'h';
		else enemy = 'H';
		if (gCoord[0] <= 8) {
		    if (gameBoard[gCoord[0]+1][gCoord[1]+2] == enemy ||
			    gameBoard[gCoord[0]+1][gCoord[1]-2] == enemy) return true;
		}
		if (gCoord[0] >= 1) {
			if (gameBoard[gCoord[0]-1][gCoord[1]+2] == enemy || //lower
			    gameBoard[gCoord[0]-1][gCoord[1]-2] == enemy) return true;
		}
		return false;
	}
	
	/** Checks if horse attacks 
	 * @param gCoord
	 * @param coord
	 * @param enemy
	 * @return true if general is attacked by horse
	 */
	private boolean horseAttacksVert(int[] gCoord, char enemy, Player player) {
		//if (Character.isUpperCase(gameBoard[coord[2]][coord[3]])) enemy = 'h';
		if (player == redPlayer) enemy = 'h';
		else enemy = 'H';
		if (gCoord[0] <= 7) {
			if (gameBoard[gCoord[0]+2][gCoord[1]+1] == enemy || //upper
			    gameBoard[gCoord[0]+2][gCoord[1]-1] == enemy ) return true; 
		}
		if (gCoord[0] >= 2) {
		    if (gameBoard[gCoord[0]-2][gCoord[1]+1] == enemy || 
				gameBoard[gCoord[0]-2][gCoord[1]-1] == enemy) return true; 
		}
		return false;
	}
	
	/** Checks if rook or cannon attacks
	 * @param gCoord
	 * @param coord
	 * @param enemy = rook 
	 * @param enemy2 = cannon
	 * @return true if general is attacked by rook or cannon
	 */
	private boolean rcAttacks(int[] gCoord, char enemy, char enemy2) { // Schanzenstein counter if field not empty
        // check all directions
		if (rcTop(gCoord, enemy, enemy2)) return true;
		if (rcBottom(gCoord, enemy, enemy2)) return true;
		if (rcRight(gCoord, enemy, enemy2)) return true;
        if (rcLeft(gCoord, enemy, enemy2)) return true;
        return false;
	}
	/** Helper Functions for all 4 directions
	 * @param gCoord
	 * @param enemy
	 * @param enemy2
	 * @return
	 */
	private boolean rcTop(int[] gCoord, char enemy, char enemy2) {
		int sCounter = 0; 
		for (int row = gCoord[0]+1; row<=9; row++) { // top
			if (gameBoard[row][gCoord[1]] == enemy && sCounter == 0) return true;  // rook attacks
			if (gameBoard[row][gCoord[1]] == enemy2 && sCounter == 1) return true; // cannon attacks
			if (gameBoard[row][gCoord[1]] != '0') sCounter++;
			if (sCounter > 1) break;
		}
		return false;
	}
	private boolean rcBottom(int[] gCoord, char enemy, char enemy2) {
		int sCounter = 0; 
        for (int row = gCoord[0]-1; row>=0; row--) { // bottom
        	if (gameBoard[row][gCoord[1]] == enemy && sCounter == 0) return true;  // rook attacks
			if (gameBoard[row][gCoord[1]] == enemy2 && sCounter == 1) return true; // cannon attacks
			if (gameBoard[row][gCoord[1]] != '0') sCounter++;
			if (sCounter > 1) break;
		} 
		return false;
	}
	private boolean rcRight(int[] gCoord, char enemy, char enemy2) {
		int sCounter = 0; 
		for (int col = gCoord[1]+1; col<=8; col++) { // right
        	if (gameBoard[gCoord[0]][col] == enemy && sCounter == 0) return true;  // rook attacks
			if (gameBoard[gCoord[0]][col] == enemy2 && sCounter == 1) return true; // cannon attacks
			if (gameBoard[gCoord[0]][col] != '0') sCounter++;
			if (sCounter > 1) break;
		} 
		return false;
	}
	private boolean rcLeft(int[] gCoord, char enemy, char enemy2) {
		int sCounter = 0; 
		for (int col = gCoord[1]-1; col>=0; col--) { // left
        	if (gameBoard[gCoord[0]][col] == enemy && sCounter == 0) return true;  // rook attacks
			if (gameBoard[gCoord[0]][col] == enemy2 && sCounter == 1) return true; // cannon attacks
			if (gameBoard[gCoord[0]][col] != '0') sCounter++;
			if (sCounter > 1) break;
		} 
		return false;
	}
	
	/** Checks if any legal move can be made
	 * @param coord
	 * @return false if no legal moves could be found
	 */
	private boolean anyLegalMoves(Player player) {
		for (int row = 0; row <= 9; row++) {
			for (int col = 0; col <= 8; col++) {
				char piece = gameBoard[row][col];  // current piece must have same color as player
				if (piece == '0') continue;  
				else if (Character.isUpperCase(piece) && player == blackPlayer) continue;
				else if (Character.isLowerCase(piece) && player == redPlayer) continue;
				
				int[] newMove = new int[4];
				newMove[0] = row;
				newMove[1] = col;
				if (moveChecker(row, col, newMove, player))return true;
			}
		}
		return false;  // no legal moves
	}
	
	/** Checks for any leagl moves starting at {row, col}
	 * @param row
	 * @param col
	 * @param newMove
	 * @param player
	 * @return true if legal move could be found
	 */
	private boolean moveChecker(int row, int col, int[] newMove, Player player) {
		for (int i = 0; i < allFields.size(); i++) { // iterate over all fields on the board
			int[] field = new int[2];
			field = allFields.get(i);
			if (row == field[0] && col == field[1]) continue; // must move somewhere
			newMove[2] = field[0];
	        newMove[3] = field[1];
	        if (testMove(newMove, player)) return true; // return true if move is legal 
		}
		return false;
	}
	
	/** Checks the legality of a move
	 * @param coord
	 * @param player
	 * @return
	 */
	private boolean testMove(int[] coord, Player player) {
		// field already occupied?
		if (!selectRule(coord)) return false;
		if (flyingGeneralRule(coord)) return false;
		if (Character.isUpperCase(gameBoard[coord[2]][coord[3]]) && Character.isUpperCase(gameBoard[coord[0]][coord[1]]) || 
			Character.isLowerCase(gameBoard[coord[2]][coord[3]]) && Character.isLowerCase(gameBoard[coord[0]][coord[1]])) return false;
		if (generalIsAttacked(coord, player, true)) { // check for chess of own general
			undoMove(coord);
			return false; 
		} else { 
			undoMove(coord); 
			return true; // move is legal
		}
	}
	
	
	/***********************************************
	 * !!!!!!!!! ALL ESSENTIAL METHODS !!!!!!!!!!!!*
	 ***********************************************/
	
	@Override
	public boolean tryMove(String moveString, Player player) {
		// TODO: implement
		// error handling
		if (!validFormatOfMoveString(moveString)) return false;
		int[] coord = stringToInt(moveString);
		if (!selectRule(coord)) return false;
		if (!rightPlayerCheck(player, coord)) return false;
		// field already occupied?
		if (Character.isUpperCase(gameBoard[coord[2]][coord[3]]) && Character.isUpperCase(gameBoard[coord[0]][coord[1]]) || 
			Character.isLowerCase(gameBoard[coord[2]][coord[3]]) && Character.isLowerCase(gameBoard[coord[0]][coord[1]])) return false;
		if (flyingGeneralRule(coord)) return false;
		if (generalIsAttacked(coord, player, true)) { // check if own general is in chess
			undoMove(coord);
			return false; 
		} else { undoMove(coord); }
		
		doMove(coord);
		// ends game if move of this player put opposite player in checkmate or patt
		inCheckMate(coord, player);	
		
		nextPlayersTurn(player);
		history.add(new Move(moveString, getBoard(), player));
		return true;
	}
	
	/** Does a move
	 * @param coord move to do
	 */
	private void doMove(int[] coord) {	
		if (Character.toLowerCase(gameBoard[coord[0]][coord[1]]) == 'g') { // is used in inCheckMate() function
		    trackGeneral(coord);
		}
		previousPiece = gameBoard[coord[2]][coord[3]];
		gameBoard[coord[2]][coord[3]] = gameBoard[coord[0]][coord[1]];
		gameBoard[coord[0]][coord[1]] = '0';
	}
	
	/** Undoes a move made by a player
	 * @param coord move to undo
	 */
	private void undoMove(int[] coord) {
		gameBoard[coord[0]][coord[1]] = gameBoard[coord[2]][coord[3]];
		gameBoard[coord[2]][coord[3]] =  previousPiece;
		if (Character.toLowerCase(gameBoard[coord[0]][coord[1]]) == 'g') { // is used in inCheckMate() function
		    unTrackGeneral(coord);
		}
	}
	
	/** Checks if move would break one of the game rules 
	 * @param coord move of player
	 * @return
	 */
	private boolean selectRule(int[] coord) {
		// game rules followed?
		switch (Character.toLowerCase(gameBoard[coord[0]][coord[1]])) {
			case 'r':
				return rook(coord);
			case 's':
				return soldier(coord);
			case 'c':
				return cannon(coord);
			case 'a':
				return advisor(coord);
			case 'h':
				return horse(coord);
			case 'e':
				return elephant(coord);
			case 'g':
				return general(coord);
			default: // let default be -> case '0':
				return false;
		} 
	}
	
	/** Checks if right player is at turn and players can't capture their own pieces
	 * @param player player at turn
	 * @param coord move of player
	 * @return
	 */
	private Boolean rightPlayerCheck(Player player, int[] coord) {	
		char piece = gameBoard[coord[0]][coord[1]];
		if (Character.isUpperCase(piece) && nextPlayer == redPlayer){
			return true;
		}else if (Character.isLowerCase(piece) && nextPlayer == blackPlayer){
			return true;
		}
		return false;
	}
	
	private Boolean validFormatOfMoveString(String moveString) {
		return moveString.matches("^([a-i][0-9]-[a-i][0-9])");
	}
	
	void nextPlayersTurn(Player player) {
		if (blackPlayer == player) {
			nextPlayer = redPlayer;
		} else {
			nextPlayer = blackPlayer;
		}
	}
	
	/*
	public void printBoard(XiangqiGame game) {
        for (int i = 9; i >= 0; i--) {
            for (int j = 0; j <= 8; j++) {
                System.out.print(game.gameBoard[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
	}
	
    public static void main(String[] args) {
    	
    	XiangqiGame game = new XiangqiGame();
    	User user = new User("Huseyin", "1");
    	Player player = new Player(user, game);
    	game.redPlayer = player;
    	
    	game.setBoard("9/5g3/9/9/9/9/9/5R3/5C3/3G5");
    	game.printBoard(game);
    	game.tryMove("d0-e0", player);
    	game.printBoard(game);
        
    }
    */
	
}
