package de.tuberlin.sese.swtpp.gameserver.test.xiangqi;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;

public class TryMoveIntegrationTest {


	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player redPlayer = null;
	Player blackPlayer = null;
	Game game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "", "xiangqi");
		
		game =  controller.getGame(gameID);
		redPlayer = game.getPlayer(user1);

	}
	
	public void startGame() {
		controller.joinGame(user2, "xiangqi");		
		blackPlayer = game.getPlayer(user2);
	}
	
	public void startGame(String initialBoard, boolean redNext) {
		startGame();
		
		game.setBoard(initialBoard);
		game.setNextPlayer(redNext? redPlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean red, boolean expectedResult) {
		if (red)
			assertEquals(expectedResult, game.tryMove(move, redPlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, blackPlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean redNext, boolean finished, boolean redWon) {
		assertEquals(expectedBoard,game.getBoard());
		assertEquals(finished, game.isFinished());

		if (!game.isFinished()) {
			assertEquals(redNext, game.getNextPlayer() == redPlayer);
		} else {
			assertEquals(redWon, redPlayer.isWinner());
			assertEquals(!redWon, blackPlayer.isWinner());
		}
	}
	

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
	@Test
	public void exampleTest() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("e3-e4",true,true);
	    assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR",false,false,false);
	}
	
	@Test
	public void exampleTestFromOurMain() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/9/1CH4C1/9/9",true);
	    assertMove("b2-b9",true,true);
	    assertGameState("rCeagaehr/9/1c5c1/s1s1s1s1s/9/9/9/2H4C1/9/9",false,false,false);
	}
	
	@Test
	public void tryWrongMoveString() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    
	    assertMove("e3e4",true,false);
	    assertMove("x3-e4",true,false);
	    assertMove("13-24",true,false);
	    assertMove("a1-a2",true,false); //empty field
	    assertMove("a1---a2",false,false);
	    assertMove("b2-c2 b2-c2",true,false);
	    
	    assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true,false,false);
	}
	
	//TODO: implement test cases of same kind as example here
	@Test
	public void WrongPlayer() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/9/1CH4C1/9/4G4",true);
	    
	    assertMove("a6-a5",false,false); // try black player even though its reds turn
	    assertMove("h2-h3",true,true);
	    assertMove("b2-b9",false,false);
	    assertMove("i9-i8",false,true);
	    assertMove("b2-b9",true,true);
	    assertMove("c2-d4",false,false);
	    
	    assertGameState("rCeagaeh1/8r/1c5c1/s1s1s1s1s/9/9/7C1/2H6/9/4G4",false,false,false);
	}
	
	@Test
	public void samePlayerTwice() {
        startGame("1heagah2/sss3sss/9/9/8c/4s4/9/3r1r3/SSS3SSS/RHC1G1HCR",true);
		
		assertMove("a1-a2",true,true);
		assertMove("a2-a3",true,false); 
		assertMove("e4-e3",false,true); 
		assertMove("e3-e2",false,false); 
		
		assertGameState("1heagah2/sss3sss/9/9/8c/9/4s4/S2r1r3/1SS3SSS/RHC1G1HCR",true,false,false);
		
	}
	
	@Test
	public void moveEveryPieceTypeOnce1() {
		 startGame("rhea1gehr/9/9/scssssscs/9/4S4/SCSSSSSCS/9/9/RHEA1GEHR",true);
		 assertMove("a0-a1",true,true);
		 assertMove("a9-a8",false,true);
		 assertMove("b0-a2",true,true);
		 assertMove("b9-a7",false,true);
		 assertMove("c0-e2",true,true);
		 assertMove("c9-e7",false,true);
		 assertMove("d0-e1",true,true);
		 assertMove("d9-e8",false,true);
		 assertMove("f0-f1",true,true);
		 assertMove("f9-f8",false,true);
		 assertGameState("6ehr/r3ag3/h3e4/scssssscs/9/4S4/SCSSSSSCS/H3E4/R3AG3/6EHR",true,false,false);
	}
	
	@Test
	public void captureOthers() {
	    startGame("rreagaehr/7R1/1c5c1/s1s3s1s/S8/3H2h2/2S1S1S1S/1C1s3C1/4A4/1EE1GAHHR",true);
	    
		assertMove("b2-b9",true,true);  //cannon
		assertMove("h7-h0",false,true);
		assertMove("e1-d2",true,true);  //advisor
		assertMove("a9-b9",false,true); //rook
		assertMove("d4-c6",true,true);  //horse
		assertMove("a6-a5",false,true); //soldier
		
		assertMove("h8-h2",true,false);  //rook
		assertMove("b7-b9",false,false); //cannon
		assertMove("h8-h2",true,false);  //rook
		assertMove("b9-b6",false,false); //jump
		
	    assertGameState("1reagaehr/7R1/1c7/2H3s1s/s8/6h2/2S1S1S1S/3A3C1/9/1EE1GAHcR",true,false,false);
	}
	
	@Test
	public void captureOwn() {
	    startGame("rreagaehr/7R1/1c5c1/s1s3s1s/S8/3H2h2/2S1S1S1S/1C1s3C1/4A4/1EE1GAHHR",false);
	    
		assertMove("i9-i6",false,false);  
		
	    assertGameState("rreagaehr/7R1/1c5c1/s1s3s1s/S8/3H2h2/2S1S1S1S/1C1s3C1/4A4/1EE1GAHHR",false,false,false);
	}
	
	@Test
	public void moveHorse() {
		 startGame("rheagaehr/scssssscs/h3h4/9/9/9/9/H3H4/SCSSSSSCS/RHEAGAEHR",true);
		 // all vertical moves
		 assertMove("e2-d4",true,true); 
		 assertMove("e7-d5",false,true);
		 assertMove("d4-e2",true,true);
		 assertMove("d5-e7",false,true);
		 
		 assertMove("e2-f4",true,true); 
		 assertMove("e7-f5",false,true);
		 assertMove("f4-e2",true,true);
		 assertMove("f5-e7",false,true);
		 // all horizontal moves
	     assertMove("e2-c3",true,true); 
	     assertMove("e7-c6",false,true);
	     assertMove("c3-e2",true,true);
	     assertMove("c6-e7",false,true);
				 
	     assertMove("e2-g3",true,true); 
	     assertMove("e7-g6",false,true);
	     assertMove("g3-e2",true,true);
	     assertMove("g6-e7",false,true);
				 
		 assertGameState("rheagaehr/scssssscs/h3h4/9/9/9/9/H3H4/SCSSSSSCS/RHEAGAEHR",true,false,false);
	}
	
	@Test
	public void wrongMoveHorse() {
		 startGame("rheagaehr/scssssscs/h3h4/9/9/9/9/H3H4/SCSSSSSCS/RHEAGAEHR",true);
		 
		 assertMove("e2-c1",true,false); 
		 assertMove("e7-d9",false,false);
		 assertMove("c1-e2",true,false); 
		 assertMove("d9-e7",false,false);
		 
		 assertMove("a2-a4",true,false); 
		 assertMove("a7-a6",false,false);
		 assertMove("a4-a2",true,false); 
		 assertMove("a6-a7",false,false);
		 
		 assertMove("e2-c0",true,false); 
		 assertMove("a7-c5",false,false);
		 assertMove("c0-e2",true,false); 
		 assertMove("c5-a7",false,false);
		 
		 assertGameState("rheagaehr/scssssscs/h3h4/9/9/9/9/H3H4/SCSSSSSCS/RHEAGAEHR",true,false,false);
	}
		
	@Test
	public void moveRook() {
		 startGame("rheagaehr/scssssscs/4r4/9/9/4S4/9/4R4/SCSSSSSCS/RHEAGAEHR",true);
		 assertMove("e2-a3",true,false);
		 assertMove("e2-a2",true,true);
		 assertMove("e7-e4",false,true);
		 assertMove("a2-i2",true,true);
		 assertGameState("rheagaehr/scssssscs/9/9/9/4r4/9/8R/SCSSSSSCS/RHEAGAEHR",false,false,false);
	}
	
	@Test
	public void moveElephant() {
		startGame("rh2ge1cr/s1c6/9/9/2e6/4S1E2/9/9/2C6/RH1EG1HCR",true);
		 
		 assertMove("d0-f2",true,true);
		 assertMove("c5-e7",false,true);
		 assertMove("f2-d4",true,true);
		 assertMove("e7-g5",false,true);
		 
		 assertMove("d4-f6",true,false);
		 
		 assertMove("d4-f2",true,true);
		 assertMove("g5-e7",false,true);
		 assertMove("f2-d0",true,true);
		 assertMove("e7-c5",false,true);
		
		 assertGameState("rh2ge1cr/s1c6/9/9/2e6/4S1E2/9/9/2C6/RH1EG1HCR",true,false,false);
	}
	
	@Test
	public void outsideAreaElephant() {
		 startGame("rh2ge1cr/s1c6/9/9/2e6/4S1E2/9/9/2C6/RH1EG1HCR",true);
		 
		 assertMove("g4-g5",true,false);
		 assertMove("c5-e3",false,false);
		 assertMove("d0-d9",true,false);
		 assertMove("f9-i4",false,false);
		
		 assertGameState("rh2ge1cr/s1c6/9/9/2e6/4S1E2/9/9/2C6/RH1EG1HCR",true,false,false);
	}
	
	@Test
	public void moveEveryPieceTypeOnce2() {
		 startGame("rheagaehr/scssssscs/9/9/9/9/9/9/SCSSSSSCS/RHEAGAEHR",true);
		 assertMove("a1-a2",true,true);
		 assertMove("a8-a7",false,true);
		 assertGameState("rheagaehr/1cssssscs/s8/9/9/9/9/S8/1CSSSSSCS/RHEAGAEHR",true,false,false);
	}
	
	@Test
	public void testSchanzenstein() {
	    startGame("rheagaehr/scs1s1scs/8h/1C2h1s2/9/9/S2SS4/H3H4/2S1S1SCS/RHEAGAEHR",true);
	    
		assertMove("b6-g6",true,true);
		assertMove("b8-b7",false,true);
		assertMove("g6-g9",true,true); // checkmate -> red wins
		
	    assertGameState("rheagaChr/s1s1s1scs/1c6h/4h4/9/9/S2SS4/H3H4/2S1S1SCS/RHEAGAEHR",true,true,true);
	}
	
	@Test
	public void wrongSchanzenstein() {
	    startGame("rCeagaehr/s8/1c2s4/1c1ss3s/9/9/9/2sEH2C1/9/1s7",true);
	    assertMove("b9-b0",true,false);
	    assertMove("a9-a9",false,false);
	    assertMove("h2-c2",true,false);
		assertMove("b7-f7",false,false);
		assertMove("b9-a9",true,false);
		assertMove("b6-i6",false,false);
	    assertGameState("rCeagaehr/s8/1c2s4/1c1ss3s/9/9/9/2sEH2C1/9/1s7",true,false,false);
	}
	
	@Test
	public void testDiagonal() {
	    startGame("rh1egehcr/sc7/9/9/4s4/4S4/9/9/SC7/RH1EGEHCR",true);
	    //right-top, left-bottom
	    assertMove("d0-f2",true,true);
		assertMove("d9-f7",false,true);
		assertMove("f2-d0",true,true);
		assertMove("f7-d9",false,true);
	    //left-top, right-bottom
		assertMove("f0-d2",true,true);
		assertMove("f9-d7",false,true);
		assertMove("d2-f0",true,true);
		assertMove("d7-f9",false,true);

	    assertGameState("rh1egehcr/sc7/9/9/4s4/4S4/9/9/SC7/RH1EGEHCR",true,false,false);
	}
	
	@Test
	public void wrongDiagonal() {
	    startGame("h1ege1cr1/s1c3h2/9/9/4s4/4S4/9/8C/S1C3H2/RH1EGE2R",true);

	    assertMove("d0-e1",true,false);
		assertMove("d9-e8",false,false);
		assertMove("d0-g3",true,false);
		assertMove("f9-c6",false,false);
		assertMove("f0-e1",true,false);
		assertMove("d9-e8",false,false);
		
		assertMove("f0-c2",true,false);
		assertMove("c9-f7",false,false);

	    //obstacles
		assertMove("d0-b2",true,false);
		assertMove("d9-b7",false,false);
		assertMove("f0-h2",true,false);
		assertMove("f9-h7",false,false);

	    assertGameState("h1ege1cr1/s1c3h2/9/9/4s4/4S4/9/8C/S1C3H2/RH1EGE2R",true,false,false);
	}
	
	@Test
	public void testSoldier() {
	    startGame("1S2g4/7S1/9/2S2ss2/5R3/5r3/2s2SS2/9/7s1/1s2G4",true);
	    //right
		assertMove("g3-g4",true,true);
		assertMove("g6-g5",false,true);
		assertMove("h8-g8",true,true);
		assertMove("h1-g1",false,true);
		assertMove("g8-g9",true,true);
		assertMove("g1-g0",false,true);
		
		assertMove("b9-a9",true,true);
		assertMove("b0-c0",false,true);
		assertMove("a9-b9",true,true);
		assertMove("c0-b0",false,true);
		
        //capture
		assertMove("f3-f4",true,true);
		assertMove("f6-f5",false,true);
		
	    assertGameState("1S2g1S2/9/9/2S6/5ss2/5SS2/2s6/9/9/1s2G1s2",true,false,false);
	}
	
	@Test
	public void soldierWrong() {
	    startGame("1S2g4/7S1/9/2S2ss2/5R3/5r3/2s2SS2/9/7s1/1s2G4",true);
	    
	    assertMove("g3-g5",true,false);
		assertMove("g6-g4",false,false);
		assertMove("g3-i3",true,false);
		assertMove("g6-i6",false,false);
		
		assertMove("g3-g2",true,false);
		assertMove("g6-g7",false,false);
		assertMove("c6-c0",true,false);
		assertMove("c3-c0",false,false);
		
		assertMove("c3-a3",true,false);
		assertMove("c3-e3",false,false);
		assertMove("c6-a6",true,false);
		assertMove("c3-e3",false,false);
		
		assertMove("c3-a3",true,false);
		assertMove("g6-h5",false,false);
		
	    assertGameState("1S2g4/7S1/9/2S2ss2/5R3/5r3/2s2SS2/9/7s1/1s2G4",true,false,false);
	}
	
	@Test
	public void testAdvisor() {
	    startGame("rh1aga1cr/s1c6/9/9/4sE3/3hS4/9/9/S1C3H2/RH1AGA1CR",true);
	    
	    assertMove("d0-e1",true,true);
		assertMove("d9-e8",false,true);
		assertMove("e1-d0",true,true);
		assertMove("e8-d9",false,true);
	   
		assertMove("f0-e1",true,true);
		assertMove("f9-e8",false,true);
		assertMove("e1-f0",true,true);
		assertMove("e8-f9",false,true);

	    assertGameState("rh1aga1cr/s1c6/9/9/4sE3/3hS4/9/9/S1C3H2/RH1AGA1CR",true,false,false);
	}
	
	@Test
	public void wrongAdvisor() {
	    startGame("rh1aga1cr/sc7/9/9/4sE3/3hS4/9/9/SC4H2/RH1AGA1CR",true);
	    assertMove("d0-f2",true,false);
		assertMove("d9-f7",false,false);
		assertMove("d0-d2",true,false);
		assertMove("d9-f7",false,false);
		assertMove("d0-e0",true,false);
		assertMove("d9-d8",false,false);
		assertMove("d0-d0",true,false);
		assertMove("d9-d8",false,false);
		
		assertMove("f0-e1",true,true);
		assertMove("d9-d8",false,false);
		assertMove("e1-e2",true,false);
		
	    // Attempt to got outside of palest
	    assertMove("d0-c1",true,false);
		assertMove("d9-g6",false,false);
		assertMove("d0-d1",true,false);
		assertMove("d9-c8",false,false);
		
	    assertGameState("rh1aga1cr/sc7/9/9/4sE3/3hS4/9/9/SC2A1H2/RH1AG2CR",false,false,false);
	}
	
	@Test
	public void testCannon() {
	    startGame("rh1aga1cr/s1c6/9/9/4sE3/3hS4/9/9/S1C3H2/RH1AGA1CR",true);
	    
	    assertMove("c1-c2",true,true);
		assertMove("c8-c7",false,true);
		assertMove("c2-c1",true,true);
		assertMove("c7-c8",false,true);
	   
		assertMove("c1-c5",true,true);
		assertMove("c8-i8",false,true);
		assertMove("c5-c1",true,true);
		assertMove("i8-c8",false,true);

	    assertGameState("rh1aga1cr/s1c6/9/9/4sE3/3hS4/9/9/S1C3H2/RH1AGA1CR",true,false,false);
	}
	
	@Test
	public void wrongCannon() {
	    startGame("rh1aga1cr/s1c6/9/9/4sE3/3hS4/9/9/S1C3H2/RH1AGA1CR",true);
	    
	    assertMove("c1-d2",true,false);
		assertMove("c8-e6",false,false);
		assertMove("c1-a1",true,false);
		assertMove("c8-a7",false,false);

	    assertGameState("rh1aga1cr/s1c6/9/9/4sE3/3hS4/9/9/S1C3H2/RH1AGA1CR",true,false,false);
	}
	
	@Test
	public void moveEmpty() {
        startGame("rh1ege1cr/s1c6/9/9/4sE3/3hS4/9/9/S1C3H2/RH1EG2CR",true);
	    
	    assertMove("e1-e2",true,false);
		assertMove("b5-b0",false,false);
		assertMove("e1-d4",true,false);

	    assertGameState("rh1ege1cr/s1c6/9/9/4sE3/3hS4/9/9/S1C3H2/RH1EG2CR",true,false,false);
	}
	
	@Test
	public void testGeneral() {
	    startGame("rhc1g1hcr/9/9/3c1e3/4s4/4S4/3E1E3/9/4s4/RHC1G1HCR",true);
	    //capture
	    assertMove("e0-e1",true,true);
	    //move
		assertMove("e9-d9",false,true);
		assertMove("e1-e0",true,true);
		assertMove("d9-e9",false,true);
		
		assertMove("e0-f0",true,true);
		assertMove("e9-e8",false,true);
		assertMove("f0-e0",true,true);
		assertMove("e8-e9",false,true);

	    assertGameState("rhc1g1hcr/9/9/3c1e3/4s4/4S4/3E1E3/9/9/RHC1G1HCR",true,false,false);
	}
	
	@Test
	public void wrongGeneral() {
	    startGame("rhc1g1hcr/9/9/3c1e3/4s4/4S4/3E1E3/9/9/RHCG2HCR",true);
	    
	    assertMove("d0-f0",true,false);
		assertMove("e9-d7",false,false);

	    assertGameState("rhc1g1hcr/9/9/3c1e3/4s4/4S4/3E1E3/9/9/RHCG2HCR",true,false,false);
	}
	

	@Test
	public void testFlyingGeneral() {
	    startGame("rhc1g1hcr/9/9/9/s3S4/9/9/9/9/RHC1G1HCR",true);

	    assertMove("e5-d5",true,false); // soldier triggers todesblick
	    assertMove("i0-i1",true,true);
		assertMove("e9-f9",false,true);
		assertMove("e0-f0",true,false); // general steps into todesblick

	    assertGameState("rhc2ghcr/9/9/9/s3S4/9/9/9/8R/RHC1G1HC1",true,false,false);
	}
	
	@Test
	public void testChessAtStart() {
	    startGame("C8/2r6/2R1g4/8C/9/3SSS3/9/4G3c/6R2/cr7",false);

	    assertMove("c8-c7",false,true); 
	
	    assertGameState("C8/9/2r1g4/8C/9/3SSS3/9/4G3c/6R2/cr7",true,false,false);
	}
	
	@Test
	public void startWithBlack() {
	    startGame("rheagaehr/scssssscs/4r4/9/9/4S4/9/4R4/SCSSSSSCS/RHEAGAEHR",false);
	    
	    assertMove("c8-c7",false,true);
	    assertMove("b2-b9",false,false);
	    assertMove("i9-i8",false,false);
	    assertMove("b2-b9",true,false);
	    assertMove("c2-d4",false,false);
	    
	    assertGameState("rheagaehr/sc1sssscs/2s1r4/9/9/4S4/9/4R4/SCSSSSSCS/RHEAGAEHR",true,false,false);
	}
	
	@Test
	public void switchGeneralsAtStart() {
	    startGame("rheg1aehr/sc2asscs/2sss4/4r4/3S5/4S4/9/4R4/SC1SSSSCS/RHEAGAEHR",true);
	    
	    assertMove("d5-e5",true,true);
	    
	    assertGameState("rheg1aehr/sc2asscs/2sss4/4r4/4S4/4S4/9/4R4/SC1SSSSCS/RHEAGAEHR",false,false,false);
	}
	
	@Test
	public void cannonChess1() {
		startGame("rhc1g1hcr/9/9/9/s2SS4/9/9/9/9/RHC1G1HCR",true);
		
		assertMove("e0-d0",true,true);
		assertMove("c9-d9",false,true); // cannon triggers chess
		assertMove("a0-a2",true,false); // general has to move out of danger
		assertMove("d0-e0",true,true);

		assertGameState("rh1cg1hcr/9/9/9/s2SS4/9/9/9/9/RHC1G1HCR",false,false,false);
	}
	
	@Test
	public void cannonChess2() {
		startGame("rh1g2c1R/R8/3sss3/C3c4/s2SS4/9/7SS/3H3S1/r7r/R4G3",true);
		
		assertMove("f0-e0",true,false); 
		assertMove("a0-a1",true,true); 
		assertMove("g9-f9",false,true); 
		assertMove("i3-i4",true,false);
		
		assertMove("a1-f1",true,true);
		assertMove("i1-i2",false,true); 
		assertMove("a6-a9",true,true);
		assertMove("i2-h2",false,false); 
		assertMove("b9-c7",false,true);
		
		assertMove("f1-f2",true,true);
		assertMove("i2-i3",false,true); 
		
		assertGameState("C2g1c2R/R8/2hsss3/4c4/s2SS4/9/7Sr/3H1R1S1/9/5G3",true,false,false);
	}

	@Test
	public void cannonChess3() {
		startGame("C8/6r2/1r2g4/8C/9/3SSS3/8c/4G1R2/2R6/c8",false);
		
		assertMove("i3-i2",false,true);
		assertMove("e4-e5",true,false); 
		assertMove("g2-g1",true,true); 
		assertMove("b7-b2",false,true);
		assertMove("c1-c2",true,true);

		assertGameState("C8/6r2/4g4/8C/9/3SSS3/9/1rR1G3c/6R2/c8",false,false,false);
	}
	
	@Test
	public void cannonChess4() {
		startGame("9/4g4/4r4/8C/4S4/4S4/8c/4R4/4G4/9",false);
		
		assertMove("i3-e3",false,true);
		assertMove("e2-d2",true,true); 
		assertMove("e8-e9",false,true);
		assertMove("i6-e6",true,true);
		assertMove("e7-d7",false,true);

		assertGameState("4g4/9/3r5/4C4/4S4/4S4/4c4/3R5/4G4/9",true,false,false);
	}
	
	@Test
	public void rookChess1() {
		
		startGame("rhc1g1hc1/9/3sss3/6r2/s2SS4/9/9/5GSSS/9/RHC3HCR",false);
		
		assertMove("g6-f6",false,true); // rook triggers chess
		assertMove("a0-a1",true,false); // general has to move out of danger
		assertMove("f2-e2",true,true); 
		assertMove("b9-a7",false,true);
		assertMove("e2-f2",true, false);
		
		assertGameState("r1c1g1hc1/9/h2sss3/5r3/s2SS4/9/9/4G1SSS/9/RHC3HCR",true,false,false);
		
	}
	
	@Test
	public void rookChess2() {
		
		startGame("1hc6/3g5/R2sss3/8R/s2SS4/9/r8/5GSSS/9/RHC5r",true);
		
		assertMove("a7-a8",true,true); // rook triggers chess
		assertMove("e7-e6",false,false); // general has to move out of danger
		assertMove("d8-d9",false,true); 
		assertMove("i2-i3",true,true);
		
		assertMove("a3-a2",false,true);
		assertMove("i3-i4",true,false);
		assertMove("f2-f1",true,true);
		assertMove("i0-i1",false,true);
		
		assertMove("i3-i4",true,false);
		assertMove("f1-f0",true,true);
		assertMove("a2-a1",false,true);
		assertMove("i6-i9",true,true);
		assertMove("i1-i0",false,false);
		
		assertGameState("1hcg4R/R8/3sss3/9/s2SS4/9/8S/6SS1/r7r/RHC2G3",false,false,false);
		
	}
	
	@Test
	public void soldierChess() {
		
		startGame("r1c1g1hc1/9/h8/5r2e/s2SS4/9/4sss2/6SSS/5G3/RHC3HCR",true);
		
		assertMove("b0-a2",true,true);
		assertMove("f3-f2",false,true); // soldier attacks
		assertMove("f1-f0",true,true);  // general has to move out of danger
		assertMove("e3-e2",false,true);
		assertMove("i2-i3",true, true);
		assertMove("g3-g2",false,true);
		
		assertMove("d5-d6",true, true);
		assertMove("e2-e1",false,true);
		assertMove("d6-d7",true, true);
		assertMove("g2-g1",false,true);
		assertMove("d7-d8",true, true);
		
		assertMove("f2-f1",false,true); // checkmate for red
		//assertMove("f0-f1",true, false); // checkmate
		
		assertGameState("r1c1g1hc1/3S5/h8/5r2e/s3S4/9/8S/H6S1/4sss2/R1C2GHCR",false,true,false);
		
	}
	
	@Test
	public void horseChess1() {
		startGame("rhc1g2c1/9/3sss3/9/s1HSS1r2/1h7/5SR2/3S3SS/4G4/RHC3HCR",false);
		assertMove("b4-d3",false,true); // horse triggers chess
		assertMove("d2-d3",true,true); // soldier captures horse
		assertMove("g5-i5",false,true); 
		assertMove("g3-g9",true,true); // rook attacks black general
		
		assertMove("e9-e8",false,true);
		assertMove("c0-c9",true,true); 
		assertMove("h9-h0",false,true);
		assertMove("a0-a5",true,true);
		
		assertMove("i5-i4",false,true);
		assertMove("c5-d7",true,true);
		assertMove("a9-a6",false,true);
		assertMove("d5-d6",true,true);
		
		assertMove("i4-i3",false,true);
		assertMove("a5-a6",true,true);
		assertMove("i3-i2",false,true);
		assertMove("a6-a8",true,true);
		assertMove("e8-f8",false,false);  // black checkmate
		assertGameState("1hC3R2/R3g4/3Hss3/3S5/4S4/9/3S1S3/7Sr/4G4/1H4HcR",false,true,true);
	}
	
	@Test
	public void todesblickChess() {
		startGame("9/4g4/3ss3R/9/9/4R4/2S1S4/3C5/4G4/9",true);
		
		assertMove("e1-f1",true,true); 
		assertMove("d7-d6",false,true); 
		assertMove("e4-e7",true,true); // rook triggers checkmate
		assertMove("e8-f8",false,false); 
		assertMove("e8-d8",false,false); 
		
		assertGameState("9/4g4/4R3R/3s5/9/9/2S1S4/3C5/5G3/9",true,true,true);
	}
	
	@Test
	public void soldierBChess() {
		startGame("9/4g4/3s4R/4S4/9/4R4/2SH5/3C5/4G4/9",true);
		
		assertMove("e6-e7",true,true); 
		assertMove("e8-e9",false,true); 
		assertMove("e7-e8",true,true); 
		assertMove("d7-d6",false,false); 
		assertMove("e9-d9",false,true); 
		
		assertMove("e4-d4",true,true); 
		assertMove("d7-d6",false,true); 
		assertMove("e8-e9",true,true); 
		assertMove("d6-d5",false,false); 
		
		assertMove("d9-d8",false,true); 
		assertMove("i7-i6",true,true);
		assertMove("d8-d7",false,true); 
		assertMove("e1-d1",true,true);
		
		assertGameState("4S4/9/3g5/3s4R/9/3R5/2SH5/3C5/3G5/9",false,false,false);
	}
	
	@Test
	public void soldierRChess() {
		startGame("5g3/4S4/9/9/9/5S3/9/9/4s4/5G3",true);
		
		assertMove("e8-e9",true,true); 
		assertMove("e1-d1",false,false); 
		assertMove("f9-f8",false,true);
		
		assertMove("f4-f5",true,true); 
		assertMove("e1-e0",false,true); 
		assertMove("e9-f9",true,false); 
		assertMove("f0-f1",true,true); 
		
		assertGameState("4S4/5g3/9/9/5S3/9/9/9/5G3/4s4",false,false,false);
	}
	
	@Test
	public void horseChess2() {
		startGame("9/4g4/9/9/H3s2H1/4S4/9/2h6/9/4G3R",true);
		
		assertMove("h5-f6",true,true);  // horse attacks
		assertMove("c2-a1",false,false); 
		assertMove("e8-d8",false,true);
		
		assertMove("a5-b7",true,true); 
		assertMove("c2-a1",false,false); 
		assertMove("d8-d9",false,true);  
		
		assertGameState("3g5/9/1H7/5H3/4s4/4S4/9/2h6/9/4G3R",true,false,false);
	}
	
	@Test
	public void horseChess3() {
		startGame("3g4r/9/1H7/5H3/4s4/3SSS1h1/9/9/4G4/h7R",false);
		
		assertMove("h4-f3",false,true);  // horse attacks
		assertMove("i0-i1",true,false); 
		assertMove("e1-d1",true,true);
		
		assertMove("a0-b2",false,true); 
		assertMove("i0-i1",true,false); 
		assertMove("d1-d0",true,true);  
		
		assertGameState("3g4r/9/1H7/5H3/4s4/3SSS3/5h3/1h7/9/3G4R",false,false,false);
	}
	
	@Test
	public void horseChess4() {
		startGame("3g4r/9/1H7/5H3/4s4/1h1SSS3/9/9/4G4/6h1R",false);
		
		assertMove("b4-d3",false,true);  // horse attacks
		assertMove("i0-i1",true,false); 
		assertMove("e1-f1",true,true);
		
		assertMove("g0-h2",false,true); 
		assertMove("i0-i1",true,false); 
		assertMove("f1-f0",true,true);  
		
		assertGameState("3g4r/9/1H7/5H3/4s4/3SSS3/3h5/7h1/9/5G2R",false,false,false);
	}
	
	@Test
	public void testCheckmate() {
        startGame("1heagah2/sss3s1s/8c/9/8c/4s4/9/3r1r3/SSS3SSS/RHC1G1HCR",true);
		
		assertMove("a1-a2",true,true);
		assertMove("i5-e5",false,true); // cannon triggers check
		assertMove("e0-e1",true,false); 
		assertMove("g0-e1",true,true); // horse protects general
		assertMove("i7-i0",false,true);  // red general in checkamte
		
		assertGameState("1heagah2/sss3s1s/9/9/4c4/4s4/9/S2r1r3/1SS1H1SSS/RHC1G2Cc",true,true,false);
		
	}
	
	@Test
	public void testPatt() {
        startGame("4g4/9/3R1R3/4S4/9/9/9/9/4S4/4G4",true);
		
		assertMove("e6-e7",true,true); // black general is not in chess but has no legal moves left -> Patt
		
		assertGameState("4g4/9/3RSR3/9/9/9/9/9/4S4/4G4",true,true,true);
		
	}
	
}
