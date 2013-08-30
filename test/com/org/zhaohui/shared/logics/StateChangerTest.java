package com.org.zhaohui.shared.logics;

import static com.org.zhaohui.shared.basics.Color.BLACK;
import static com.org.zhaohui.shared.basics.Color.WHITE;
import static com.org.zhaohui.shared.basics.PieceKind.KING;
import static com.org.zhaohui.shared.basics.PieceKind.PAWN;
import static com.org.zhaohui.shared.basics.PieceKind.QUEEN;
import static com.org.zhaohui.shared.basics.PieceKind.ROOK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.org.zhaohui.shared.basics.Color;
import com.org.zhaohui.shared.basics.GameResult;
import com.org.zhaohui.shared.basics.GameResultReason;
import com.org.zhaohui.shared.basics.IllegalMove;
import com.org.zhaohui.shared.basics.Move;
import com.org.zhaohui.shared.basics.Piece;
import com.org.zhaohui.shared.basics.PieceKind;
import com.org.zhaohui.shared.basics.Position;
import com.org.zhaohui.shared.basics.State;

public class StateChangerTest {

	protected State start;
	protected StateChanger stateChanger;

	public StateChanger getStateChanger(){
		return StateChangerImpl.getInstance();
	}

	@Before
	public void setup() {
		start = new State();
		final StateChanger impl = getStateChanger();
		stateChanger = new StateChanger() {
			@Override
			public void makeMove(State state, Move move) throws IllegalMove {
				assertStatePossible(state);
				impl.makeMove(state, move);
			}
		};
	}

	public static void assertStatePossible(State state) {
		int[][] piecesCount = new int[2][PieceKind.values().length];
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				Piece piece = state.getPiece(r, c);
				if (piece == null) {
					continue;
				}
				piecesCount[piece.getColor().ordinal()][piece.getKind()
						.ordinal()]++;
			}
		}
		for (Color color : Color.values()) {
			check(piecesCount[color.ordinal()][PieceKind.KING.ordinal()] == 1,
					"You must have exactly one king");
			int promotedCount = 0;
			promotedCount += Math
					.max(0, piecesCount[color.ordinal()][PieceKind.QUEEN
							.ordinal()] - 1);
			promotedCount += Math.max(0,
					piecesCount[color.ordinal()][PieceKind.ROOK.ordinal()] - 2);
			promotedCount += Math
					.max(0, piecesCount[color.ordinal()][PieceKind.KNIGHT
							.ordinal()] - 2);
			promotedCount += Math
					.max(0, piecesCount[color.ordinal()][PieceKind.BISHOP
							.ordinal()] - 2);
			check(piecesCount[color.ordinal()][PieceKind.PAWN.ordinal()]
					+ promotedCount <= 8,
					"You promoted too many pieces, you need to remove some pawns");
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new RuntimeException(message);
		}
	}
	
	/*
     * Start Tests by Zhaohui Zhang <bravezhaohui@gmail.com>
     */
    @Test
    public void testStartStatusHasNoEnpassantPosition() {
    	assertEquals(null, start.getEnpassantPosition());
    }

    @Test
    public void testWhitePawnCanMoveEnpassantPosition() {
    	Move move = new Move(new Position(1, 0), new Position(3, 0), null);
    	Position expectedEnpassantPosition = new Position(3, 0);
    	stateChanger.makeMove(start, move);
    	assertEquals(start.getEnpassantPosition(), expectedEnpassantPosition);
    }

    @Test
    public void testPawnOneSquareMoveWillNotChangeEnpassantPosition() {
    	Move move = new Move(new Position(1, 0), new Position(2, 0), null);
    	State expected = start.copy();
    	expected.setTurn(Color.BLACK);
    	expected.setPiece(1, 0, null);
    	expected.setPiece(2, 0, new Piece(Color.WHITE, PAWN));
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	expected.setEnpassantPosition(null);
    	stateChanger.makeMove(start, move);
    	assertEquals(expected, start);
    }

    @Test
    public void testPawnTwoSquaresMoveCanChangeEnpassantPosition() {
    	Move move = new Move(new Position(1, 0), new Position(3, 0), null);
    	State expected = start.copy();
    	expected.setTurn(Color.BLACK);
    	expected.setPiece(1, 0, null);
    	expected.setPiece(3, 0, new Piece(Color.WHITE, PAWN));
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	expected.setEnpassantPosition(new Position(3, 0));
    	stateChanger.makeMove(start, move);
    	assertEquals(expected, start);
    }

    @Test
    public void testBlackEnpassantPositionBeforeWhitePawnCapture() {
    	Move move = new Move(new Position(6, 6), new Position(4, 6), null);
    	State former = start.copy();
    	former.setTurn(Color.BLACK);
    	former.setPiece(1, 5, null);
    	former.setPiece(2, 5, new Piece(Color.WHITE, PAWN));
    	former.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	former.setEnpassantPosition(null);
    	State expected = former.copy();
    	expected.setTurn(Color.WHITE);
    	expected.setPiece(6, 6, null);
    	expected.setPiece(4, 6, new Piece(Color.BLACK, PAWN));
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	expected.setEnpassantPosition(new Position(4, 6));
    	stateChanger.makeMove(former, move);
    	assertEquals(expected, former);
    }

    @Test
    public void testWhitePawnCanCaptureByEnpassant() {
    	Move move = new Move(new Position(4, 5), new Position(5, 6), null);
    	Position enpassantPositionOfBlack = new Position(4, 6);
    	State former = start.copy();
    	former.setTurn(Color.WHITE);
    	former.setPiece(1, 5, null);
    	former.setPiece(4, 5, new Piece(Color.WHITE, PAWN));
    	former.setPiece(6, 0, null);
    	former.setPiece(5, 0, new Piece(Color.BLACK, PAWN));
    	former.setPiece(6, 6, null);
    	former.setPiece(4, 6, new Piece(Color.BLACK, PAWN));
    	former.setEnpassantPosition(enpassantPositionOfBlack);
    	State expected = former.copy();
    	expected.setTurn(Color.BLACK);
    	expected.setPiece(4, 5, null);
    	expected.setPiece(5, 6, new Piece(Color.WHITE, PAWN));
    	expected.setPiece(4, 6, null);
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	expected.setEnpassantPosition(null);
    	stateChanger.makeMove(former, move);
    	assertEquals(expected, former);
    }

    @Test
    public void testEnpassantPositionSetNullIfWhitePawnNotCapture() {
    	Move move = new Move(new Position(1, 7), new Position(2, 7), null);
    	Position enpassantPositionOfBlack = new Position(4, 6);
    	State former = start.copy();
    	former.setTurn(Color.WHITE);
    	former.setPiece(1, 5, null);
    	former.setPiece(4, 5, new Piece(Color.WHITE, PAWN));
    	former.setPiece(6, 0, null);
    	former.setPiece(5, 0, new Piece(Color.BLACK, PAWN));
    	former.setPiece(6, 6, null);
    	former.setPiece(4, 6, new Piece(Color.BLACK, PAWN));
    	former.setEnpassantPosition(enpassantPositionOfBlack);
    	State expected = former.copy();
    	expected.setTurn(Color.BLACK);
    	expected.setPiece(1, 7, null);
    	expected.setPiece(2, 7, new Piece(Color.WHITE, PAWN));
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	expected.setEnpassantPosition(null);
    	stateChanger.makeMove(former, move);
    	assertEquals(expected, former);
    }

    @Test
    public void testWhitePawnCanBeCapturedIfInEnpassantPosition() {
    	Move move = new Move(new Position(3, 1), new Position(2, 0), null);
    	Position enpassantPositionOfWhite = new Position(3, 0);
    	State former = start.copy();
    	former.setTurn(BLACK);
    	former.setPiece(1, 0, null);
    	former.setPiece(3, 0, new Piece(WHITE, PAWN));
    	former.setPiece(6, 1, null);
    	former.setPiece(3, 1, new Piece(BLACK, PAWN));
    	former.setPiece(1, 2, null);
    	former.setPiece(3, 2, new Piece(WHITE, PAWN));
    	former.setEnpassantPosition(enpassantPositionOfWhite);
    	former.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	State expected = former.copy();
    	expected.setTurn(WHITE);
    	expected.setPiece(3, 0, null);
    	expected.setPiece(3, 1, null);
    	expected.setPiece(2, 0, new Piece(BLACK, PAWN));
    	expected.setEnpassantPosition(null);
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	stateChanger.makeMove(former, move);
    	assertEquals(expected, former);
    }

    @Test(expected = IllegalMove.class)
    public void testWhitePawnCanNotBeCapturedIfNotInEnpassantPosition() {
    	Move move = new Move(new Position(3, 1), new Position(2, 0), null);
    	State former = start.copy();
    	former.setTurn(BLACK);
    	former.setPiece(1, 0, null);
    	former.setPiece(3, 0, new Piece(WHITE, PAWN));
    	former.setPiece(6, 1, null);
    	former.setPiece(3, 1, new Piece(BLACK, PAWN));
    	former.setPiece(1, 2, null);
    	former.setPiece(3, 2, new Piece(WHITE, PAWN));
    	former.setEnpassantPosition(null);
    	former.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	stateChanger.makeMove(former, move);
    }

    @Test(expected = IllegalMove.class)
    public void testBlackPawnCanNotCrossNonEnpassantPosition() {
    	Move move = new Move(new Position(3, 1), new Position(2, 2), null);
    	Position enpassantPositionOfWhite = new Position(3, 0);
    	State former = start.copy();
    	former.setTurn(BLACK);
    	former.setPiece(1, 0, null);
    	former.setPiece(3, 0, new Piece(WHITE, PAWN));
    	former.setPiece(6, 1, null);
    	former.setPiece(3, 1, new Piece(BLACK, PAWN));
    	former.setPiece(1, 2, null);
    	former.setPiece(3, 2, new Piece(WHITE, PAWN));
    	former.setEnpassantPosition(enpassantPositionOfWhite);
    	former.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	stateChanger.makeMove(former, move);
    }

    @Test
    public void testWhitePawnCanNotBeCapturedIfBlackPawnNotCrossEnpassantPosition() {
    	Move move = new Move(new Position(3, 1), new Position(2, 1), null);
    	Position enpassantPositionOfWhite = new Position(3, 0);
    	State former = start.copy();
    	former.setTurn(BLACK);
    	former.setPiece(1, 0, null);
    	former.setPiece(3, 0, new Piece(WHITE, PAWN));
    	former.setPiece(6, 1, null);
    	former.setPiece(3, 1, new Piece(BLACK, PAWN));
    	former.setPiece(1, 2, null);
    	former.setPiece(3, 2, new Piece(WHITE, PAWN));
    	former.setEnpassantPosition(enpassantPositionOfWhite);
    	former.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	State expected = former.copy();
    	expected.setTurn(WHITE);
    	expected.setPiece(3, 1, null);
    	expected.setPiece(2, 1, new Piece(BLACK, PAWN));
    	expected.setEnpassantPosition(null);
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	stateChanger.makeMove(former, move);
    	assertEquals(expected, former);

    }


    @Test(expected = IllegalMove.class)
    public void testWhitePawnCanNotCaptureIfBlackPawnNotInEnpassantPosition() {
    	Move move = new Move(new Position(4, 5), new Position(5, 6), null);
    	State former = start.copy();
    	former.setTurn(WHITE);
    	former.setPiece(1, 5, null);
    	former.setPiece(4, 5, new Piece(WHITE, PAWN));
    	former.setPiece(6, 0, null);
    	former.setPiece(5, 0, new Piece(BLACK, PAWN));
    	former.setPiece(6, 6, null);
    	former.setPiece(4, 6, new Piece(BLACK, PAWN));
    	former.setEnpassantPosition(null);
    	stateChanger.makeMove(former, move);
    }

    @Test
    //fixed the bug of illegal position before move
    public void testEnpassantPositionIfGameOver() {
    	Piece[][] board = new Piece[8][8];
    	board[4][0] = new Piece(BLACK, PAWN);
    	board[4][1] = new Piece(WHITE, PAWN);
    	board[0][3] = new Piece(WHITE, KING);
    	board[7][3] = new Piece(BLACK, KING);
    	board[6][1] = new Piece(WHITE, ROOK);
    	board[6][5] = new Piece(WHITE, QUEEN);
    	State former = new State(WHITE, board, new boolean[] { false, false },
    			new boolean[] { false, false }, new Position(4, 0), 0, null);
    	Move move = new Move(new Position(6, 1), new Position(7, 1), null);
    	State expected = former.copy();
    	GameResult gameResult = new GameResult(WHITE,
    			GameResultReason.CHECKMATE);
    	expected.setTurn(BLACK);
    	expected.setGameResult(gameResult);
    	expected.setPiece(6, 1, null);
    	expected.setPiece(7, 1, new Piece(WHITE, ROOK));
    	expected.setEnpassantPosition(null);
    	expected.setCanCastleKingSide(WHITE, false);
    	expected.setCanCastleQueenSide(WHITE, false);
    	expected.setCanCastleKingSide(BLACK, false);
    	expected.setCanCastleQueenSide(BLACK, false);
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(1);
    	stateChanger.makeMove(former, move);
    	assertEquals(expected, former);
    }

    /*@Test
	//fixed bug for illegal states which is already a checkmate result
	public void testEnpassantPositionCanNotBeAnywhere() {
		Piece[][] board = new Piece[8][8];
		board[4][0] = new Piece(BLACK, PAWN);
		board[4][1] = new Piece(WHITE, PAWN);
		board[0][3] = new Piece(WHITE, KING);
		board[7][4] = new Piece(BLACK, KING);
		State former = new State(WHITE, board, new boolean[] { true, true },
				new boolean[] { true, true }, new Position(4, 0), 0, null);
		assertTrue(former.getEnpassantPosition() != null);
		assertTrue(former.getEnpassantPosition().getCol() == 3
				|| former.getEnpassantPosition().getCol() == 4);
	}*/

    @Test
    public void testEnpassantPositionCanNotPlaceOtherPieceExceptPawn() {
    	Piece[][] board = new Piece[8][8];
    	board[4][0] = new Piece(BLACK, PAWN);
    	board[4][1] = new Piece(WHITE, PAWN);
    	board[0][3] = new Piece(WHITE, KING);
    	board[7][4] = new Piece(BLACK, KING);
    	State former = new State(WHITE, board, new boolean[] { true, true },
    			new boolean[] { true, true }, new Position(4, 0), 0, null);
    	assertTrue(former.getEnpassantPosition() != null);
    	int row = former.getEnpassantPosition().getRow();
    	int collum = former.getEnpassantPosition().getCol();
    	assertTrue(board[row][collum].getKind() == PAWN);
    }

    // test for other cases while related to en passant
    @Test(expected = IllegalMove.class)
    public void testPawnCanNotMoveDiagonallyWithoutEnpassantOrCapture() {
    	Move move = new Move(new Position(6, 1), new Position(5, 0), null);
    	State former = start.copy();
    	former.setTurn(BLACK);
    	former.setPiece(1, 0, null);
    	former.setPiece(3, 0, new Piece(WHITE, PAWN));
    	former.setEnpassantPosition(new Position(3, 0));
    	former.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	stateChanger.makeMove(former, move);
    }

    @Test
    public void testWhitePawnCanMoveDiagonallyWithoutEnpassant() {
    	Move move = new Move(new Position(1, 7), new Position(2, 6), null);
    	Piece[][] board = new Piece[8][8];
    	board[4][0] = new Piece(BLACK, PAWN);
    	board[2][6] = new Piece(BLACK, PAWN);
    	board[4][1] = new Piece(WHITE, PAWN);
    	board[1][7] = new Piece(WHITE, PAWN);
    	board[0][3] = new Piece(WHITE, KING);
    	board[7][4] = new Piece(BLACK, KING);
    	State former = new State(WHITE, board, new boolean[] { true, true },
    			new boolean[] { true, true }, new Position(4, 0), 0, null);
    	State expected = former.copy();
    	expected.setTurn(BLACK);
    	expected.setPiece(1, 7, null);
    	expected.setPiece(2, 6, new Piece(WHITE, PAWN));
    	expected.setEnpassantPosition(null);
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	stateChanger.makeMove(former, move);
    }

    @Test(expected = IllegalMove.class)
    public void testWhitePawnCanNotMoveTwoSquaresIfNotFromInitialPosition() {
    	Piece[][] board = new Piece[8][8];
    	board[4][0] = new Piece(BLACK, PAWN);
    	board[5][0] = new Piece(BLACK, ROOK);
    	board[4][1] = new Piece(WHITE, PAWN);
    	board[0][3] = new Piece(WHITE, KING);
    	board[7][4] = new Piece(BLACK, KING);
    	State former = new State(WHITE, board, new boolean[] { true, true },
    			new boolean[] { true, true }, null, 0, null);
    	Move move = new Move(new Position(4, 1), new Position(6, 1), null);
    	stateChanger.makeMove(former, move);
    }
    //add 4 rooks to ensure the castling state
    @Test
    public void testWhitePawnMoveOneSquareNotFromInitialPositionNotChangeEnpassantPosition() {
    	Piece[][] board = new Piece[8][8];
    	board[4][0] = new Piece(BLACK, PAWN);
    	board[5][0] = new Piece(BLACK, ROOK);
    	board[4][1] = new Piece(WHITE, PAWN);
    	board[0][4] = new Piece(WHITE, KING);
    	board[7][4] = new Piece(BLACK, KING);
    	board[0][0] = new Piece(WHITE, ROOK);
    	board[0][7] = new Piece(WHITE, ROOK);
    	board[7][0] = new Piece(BLACK, ROOK);
    	board[7][7] = new Piece(BLACK, ROOK);
    	State former = new State(WHITE, board, new boolean[] { true, true },
    			new boolean[] { true, true }, new Position(4, 0), 0, null);
    	Move move = new Move(new Position(4, 1), new Position(5, 1), null);
    	State expected = former.copy();
    	expected.setTurn(BLACK);
    	expected.setPiece(4, 1, null);
    	expected.setPiece(5, 1, new Piece(WHITE, PAWN));
    	expected.setEnpassantPosition(null);
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	stateChanger.makeMove(former, move);
    	assertEquals(expected, former);
    }
    //set castling condition for Black to false	
    @Test
    public void testEnpassantPositionCanChangeFromBlackPawnToWhitePawnContinuously(){
    	Piece[][] board = new Piece[8][8];
    	board[4][0] = new Piece(BLACK, PAWN);
    	board[1][1] = new Piece(WHITE, PAWN);
    	board[0][3] = new Piece(WHITE, KING);
    	board[7][4] = new Piece(BLACK, KING);
    	State former = new State(WHITE, board, new boolean[] { false, false },
    			new boolean[] { false, false }, new Position(4, 0), 0, null);
    	Move move = new Move(new Position(1, 1), new Position(3, 1), null);
    	State expected = former.copy();
    	expected.setTurn(BLACK);
    	expected.setPiece(1, 1, null);
    	expected.setPiece(3, 1, new Piece(WHITE, PAWN));
    	expected.setEnpassantPosition(new Position(3, 1));
    	expected.setCanCastleKingSide(WHITE, false);
    	expected.setCanCastleKingSide(BLACK, false);
    	expected.setCanCastleQueenSide(WHITE, false);
    	expected.setCanCastleQueenSide(BLACK, false);
    	expected.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    	stateChanger.makeMove(former, move);
    	assertEquals(expected, former);
    }

    /*
     * End Tests by Zhaohui Zhang <bravezhaohui@gmail.com>
     */

}
