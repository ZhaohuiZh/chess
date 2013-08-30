package com.org.zhaohui.shared.logics;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.org.zhaohui.shared.basics.Color;
import com.org.zhaohui.shared.basics.GameResult;
import com.org.zhaohui.shared.basics.GameResultReason;
import com.org.zhaohui.shared.basics.Move;
import com.org.zhaohui.shared.basics.Piece;
import com.org.zhaohui.shared.basics.PieceKind;
import com.org.zhaohui.shared.basics.Position;
import com.org.zhaohui.shared.basics.State;

public class StateExplorerTest {

	protected State start;
	protected StateExplorer stateExplorer;

	public StateExplorer getStateExplorer() {
		return StateExplorerImpl.getInstance();
	}

	@Before
	public void setup() {
		start = new State();
		final StateExplorer impl = getStateExplorer();
		stateExplorer = new StateExplorer() {
			@Override
			public Set<Move> getPossibleMoves(State state) {
				StateChangerTest.assertStatePossible(state);
				return impl.getPossibleMoves(state);
			}

			@Override
			public Set<Move> getPossibleMovesFromPosition(State state,
					Position start) {
				StateChangerTest.assertStatePossible(state);
				return impl.getPossibleMovesFromPosition(state, start);
			}

			@Override
			public Set<Position> getPossibleStartPositions(State state) {
				StateChangerTest.assertStatePossible(state);
				return impl.getPossibleStartPositions(state);
			}
		};
	}

	/*
	 * Begin Tests by Yoav Zibin <yoav.zibin@gmail.com>
	 */
	@Test
	public void testGetPossibleStartPositions_InitState() {
		Set<Position> expectedPositions = Sets.newHashSet();
		// PieceKind.PAWN positions
		for (int c = 0; c < 8; c++)
			expectedPositions.add(new Position(1, c));
		// knight positions
		expectedPositions.add(new Position(0, 1));
		expectedPositions.add(new Position(0, 6));
		assertEquals(expectedPositions,
				stateExplorer.getPossibleStartPositions(start));
	}

	@Test
	public void testGetPossibleMoves_InitState() {
		Set<Move> expectedMoves = Sets.newHashSet();
		// PieceKind.PAWN moves
		for (int c = 0; c < 8; c++) {
			expectedMoves.add(new Move(new Position(1, c), new Position(2, c),
					null));
			expectedMoves.add(new Move(new Position(1, c), new Position(3, c),
					null));
		}
		// knight moves
		expectedMoves
				.add(new Move(new Position(0, 1), new Position(2, 0), null));
		expectedMoves
				.add(new Move(new Position(0, 1), new Position(2, 2), null));
		expectedMoves
				.add(new Move(new Position(0, 6), new Position(2, 5), null));
		expectedMoves
				.add(new Move(new Position(0, 6), new Position(2, 7), null));
		assertEquals(expectedMoves, stateExplorer.getPossibleMoves(start));
	}

	@Test
	public void testGetPossibleMovesFromPosition_InitStateForLeftKnight() {
		Set<Move> expectedMoves = Sets.newHashSet();
		// knight moves
		expectedMoves
				.add(new Move(new Position(0, 1), new Position(2, 0), null));
		expectedMoves
				.add(new Move(new Position(0, 1), new Position(2, 2), null));
		assertEquals(expectedMoves, stateExplorer.getPossibleMovesFromPosition(
				start, new Position(0, 1)));
	}

	@Test
	public void testGetPossibleMovesFromPosition_Promotion() {
		start.setPiece(new Position(1, 0), null);
		start.setPiece(new Position(6, 0), new Piece(Color.WHITE,
				PieceKind.PAWN));

		Set<Move> expectedMoves = Sets.newHashSet();
		// promotion moves
		expectedMoves.add(new Move(new Position(6, 0), new Position(7, 1),
				PieceKind.BISHOP));
		expectedMoves.add(new Move(new Position(6, 0), new Position(7, 1),
				PieceKind.KNIGHT));
		expectedMoves.add(new Move(new Position(6, 0), new Position(7, 1),
				PieceKind.ROOK));
		expectedMoves.add(new Move(new Position(6, 0), new Position(7, 1),
				PieceKind.QUEEN));
		assertEquals(expectedMoves, stateExplorer.getPossibleMovesFromPosition(
				start, new Position(6, 0)));
	}

	/*
	 * End Tests by Yoav Zibin <yoav.zibin@gmail.com>
	 */

	/*
	 * Begin Tests by Zhaohui Zhang <bravezhaohui@gmail.com>
	 */
	@Test
	public void testGetPossibleStartPositions_BlackInitial() {
		State former = start.copy();
		former.setTurn(Color.BLACK);
		former.setPiece(1, 0, null);
		former.setPiece(2, 0, new Piece(Color.WHITE, PieceKind.PAWN));
		Set<Position> expectedPositions = Sets.newHashSet();
		// PieceKind.PAWN positions
		for (int c = 0; c < 8; c++)
			expectedPositions.add(new Position(6, c));
		// knight positions
		expectedPositions.add(new Position(7, 1));
		expectedPositions.add(new Position(7, 6));
		assertEquals(expectedPositions,
				stateExplorer.getPossibleStartPositions(former));
	}

	@Test
	public void testGetPossibleStartPositions_WhiteNormal() {
		Piece[][] board = new Piece[8][8];
		board[0][3] = new Piece(Color.WHITE, PieceKind.KING);
		board[0][7] = new Piece(Color.WHITE, PieceKind.QUEEN);
		board[1][6] = new Piece(Color.WHITE, PieceKind.BISHOP);
		board[7][4] = new Piece(Color.BLACK, PieceKind.KING);
		State former = new State(Color.WHITE, board, new boolean[] { false, false },
				new boolean[] { false, false }, null, 0, null);
		Set<Position> expectedPositions = Sets.newHashSet();
		expectedPositions.add(new Position(0, 3));
		expectedPositions.add(new Position(0, 7));
		expectedPositions.add(new Position(1, 6));
		assertEquals(expectedPositions,
				stateExplorer.getPossibleStartPositions(former));
	}

	@Test
	public void testGetPossibleStartPositions_GameIsOver() {
		Piece[][] board = new Piece[8][8];
		board[0][3] = new Piece(Color.WHITE, PieceKind.KING);
		board[7][4] = new Piece(Color.BLACK, PieceKind.KING);
		GameResult gameResult = new GameResult(null,
				GameResultReason.FIFTY_MOVE_RULE);
		State former = new State(Color.WHITE, board, new boolean[] { false, false },
				new boolean[] { false, false }, null, 0, gameResult);
		Set<Position> expectedPositions = Sets.newHashSet();
		assertEquals(expectedPositions,
				stateExplorer.getPossibleStartPositions(former));
	}

	@Test
	public void testGetPossibleMovesFromPosition_InitStateForRightKnight() {
		Set<Move> expectedMoves = Sets.newHashSet();
		// knight moves
		expectedMoves
				.add(new Move(new Position(0, 6), new Position(2, 5), null));
		expectedMoves
				.add(new Move(new Position(0, 6), new Position(2, 7), null));
		assertEquals(expectedMoves, stateExplorer.getPossibleMovesFromPosition(
				start, new Position(0, 6)));
	}

	@Test
	public void testGetPossibleMovesFromPosition_NoPiece() {
		Set<Move> expectedMoves = Sets.newHashSet();
		Piece[][] board = new Piece[8][8];
		board[0][3] = new Piece(Color.WHITE, PieceKind.KING);
		board[7][4] = new Piece(Color.BLACK, PieceKind.KING);
		State former = new State(Color.BLACK, board, new boolean[] { false, false },
				new boolean[] { false, false }, null, 0, null);
		assertEquals(expectedMoves, stateExplorer.getPossibleMovesFromPosition(former,
				new Position(4, 4)));
	}

	@Test
	public void testGetPossibleMovesFromPosition_BlackLeftKnightInit() {
		State former = start.copy();
		former.setTurn(Color.BLACK);
		former.setPiece(1, 0, null);
		former.setPiece(2, 0, new Piece(Color.WHITE, PieceKind.PAWN));

		Set<Move> expectedMoves = Sets.newHashSet();
		expectedMoves
				.add(new Move(new Position(7, 1), new Position(5, 0), null));
		expectedMoves
				.add(new Move(new Position(7, 1), new Position(5, 2), null));
		assertEquals(expectedMoves, stateExplorer.getPossibleMovesFromPosition(
				former, new Position(7, 1)));

	}

	@Test
	public void testGetPossibleMovesFromPosition_WhiteQueen() {
		Piece[][] board = new Piece[8][8];
		board[0][3] = new Piece(Color.WHITE, PieceKind.KING);
		board[0][7] = new Piece(Color.WHITE, PieceKind.QUEEN);
		board[1][6] = new Piece(Color.BLACK, PieceKind.BISHOP);
		board[7][4] = new Piece(Color.BLACK, PieceKind.KING);
		State former = new State(Color.WHITE, board, new boolean[] { false, false },
				new boolean[] { false, false }, null, 0, null);
		Set<Move> expectedMoves = Sets.newHashSet();
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(1, 6), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(0, 6), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(0, 5), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(0, 4), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(1, 7), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(2, 7), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(3, 7), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(4, 7), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(5, 7), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(6, 7), null));
		expectedMoves
				.add(new Move(new Position(0, 7), new Position(7, 7), null));
		assertEquals(expectedMoves, stateExplorer.getPossibleMovesFromPosition(
				former, new Position(0, 7)));
	}

	@Test
	public void testGetPossibleMoves_BlackInit() {
		State former = start.copy();
		former.setTurn(Color.BLACK);
		former.setPiece(1, 0, null);
		former.setPiece(2, 0, new Piece(Color.WHITE, PieceKind.PAWN));

		Set<Move> expectedMoves = Sets.newHashSet();
		// PieceKind.PAWN moves
		for (int c = 0; c < 8; c++) {
			expectedMoves.add(new Move(new Position(6, c), new Position(5, c),
					null));
			expectedMoves.add(new Move(new Position(6, c), new Position(4, c),
					null));
		}
		// knight moves
		expectedMoves
				.add(new Move(new Position(7, 1), new Position(5, 0), null));
		expectedMoves
				.add(new Move(new Position(7, 1), new Position(5, 2), null));
		expectedMoves
				.add(new Move(new Position(7, 6), new Position(5, 5), null));
		expectedMoves
				.add(new Move(new Position(7, 6), new Position(5, 7), null));
		assertEquals(expectedMoves, stateExplorer.getPossibleMoves(former));
	}

	@Test
	public void testGetPossibleMovesPawnEnpassant() {
		State former = start.copy();
		former.setTurn(Color.WHITE);
		former.setPiece(1, 5, null);
		former.setPiece(4, 5, new Piece(Color.WHITE, PieceKind.PAWN));
		former.setPiece(6, 0, null);
		former.setPiece(5, 0, new Piece(Color.BLACK, PieceKind.PAWN));
		former.setPiece(6, 6, null);
		former.setPiece(4, 6, new Piece(Color.BLACK, PieceKind.PAWN));
		former.setEnpassantPosition(new Position(4, 6));

		Set<Move> expectedMoves = Sets.newHashSet();
		// PieceKind.PAWN moves
		for (int c = 0; c < 8; c++) {
			if (c != 5) {
				expectedMoves.add(new Move(new Position(1, c), new Position(2,
						c), null));
				expectedMoves.add(new Move(new Position(1, c), new Position(3,
						c), null));
			}
		}
		expectedMoves
				.add(new Move(new Position(4, 5), new Position(5, 5), null));
		expectedMoves
				.add(new Move(new Position(4, 5), new Position(5, 6), null));

		// knight moves
		expectedMoves
				.add(new Move(new Position(0, 1), new Position(2, 0), null));
		expectedMoves
				.add(new Move(new Position(0, 1), new Position(2, 2), null));
		expectedMoves
				.add(new Move(new Position(0, 6), new Position(2, 5), null));
		expectedMoves
				.add(new Move(new Position(0, 6), new Position(2, 7), null));
		expectedMoves
				.add(new Move(new Position(0, 4), new Position(1, 5), null));
		assertEquals(expectedMoves, stateExplorer.getPossibleMoves(former));
	}

	/*
	 * End Tests by Zhaohui Zhang <bravezhaohui@gmail.com>
	 */

}
