package com.org.zhaohui.client.game;

import com.org.zhaohui.shared.basics.Color;
import com.org.zhaohui.shared.basics.GameResult;
import com.org.zhaohui.shared.basics.GameResultReason;
import com.org.zhaohui.shared.basics.Piece;
import com.org.zhaohui.shared.basics.PieceKind;
import com.org.zhaohui.shared.basics.Position;
import com.org.zhaohui.shared.basics.State;

public class StateSerializer {
	private static StateSerializer instance;

	private StateSerializer() {
	}

	public static StateSerializer getInstance() {
		if (instance == null) {
			synchronized (StateSerializer.class) {
				if (instance == null) {
					instance = new StateSerializer();
				}
			}
		}
		return instance;
	}

	public String serializeState(State state) {
		Color color = state.getTurn();
		StringBuffer serializedStateBuff = new StringBuffer();
		serializedStateBuff.append(color == Color.WHITE ? "W" : "B");
		serializedStateBuff.append("-");
		for (int i = 0; i < State.ROWS; i++) {
			for (int j = 0; j < State.COLS; j++) {
				Piece piece = state.getPiece(i, j);
				if (piece == null) {
					serializedStateBuff.append("__");
				} else {
					Color pieceColor = piece.getColor();
					PieceKind kind = piece.getKind();
					serializedStateBuff.append(pieceColor == Color.WHITE ? "W"
							: "B");
					char pieceChar = 0;
					switch (kind) {
					case QUEEN:
						pieceChar = 'Q';
						break;
					case KING:
						pieceChar = 'K';
						break;
					case KNIGHT:
						pieceChar = 'N';
						break;
					case BISHOP:
						pieceChar = 'B';
						break;
					case ROOK:
						pieceChar = 'R';
						break;
					case PAWN:
						pieceChar = 'P';
						break;
					default:
						break;
					}
					serializedStateBuff.append(pieceChar);
				}
			}
		}
		serializedStateBuff.append("-");
		serializedStateBuff
				.append(state.isCanCastleKingSide(Color.WHITE) == true ? 'T'
						: 'F');
		serializedStateBuff
				.append(state.isCanCastleKingSide(Color.BLACK) == true ? 'T'
						: 'F');
		serializedStateBuff
				.append(state.isCanCastleQueenSide(Color.WHITE) == true ? 'T'
						: 'F');

		serializedStateBuff
				.append(state.isCanCastleQueenSide(Color.BLACK) == true ? 'T'
						: 'F');
		serializedStateBuff.append("-");
		Position empassPosition = state.getEnpassantPosition();
		if (empassPosition != null) {
			serializedStateBuff.append("[" + empassPosition.getRow()
					+ empassPosition.getCol() + "]");
		} else {
			serializedStateBuff.append("NULL");
		}
		serializedStateBuff.append("-");
		GameResult gameResult = state.getGameResult();
		StringBuffer gameResultBuff = new StringBuffer();
		if (gameResult == null) {
			gameResultBuff.append("[NULL]");
		} else {
			Color winner = gameResult.getWinner();
			gameResultBuff.append("[");
			if (winner == null) {
				gameResultBuff.append('_');
			} else {
				gameResultBuff.append(winner == Color.WHITE ? "W" : "B");
			}

			switch (gameResult.getGameResultReason()) {
			case CHECKMATE:
				gameResultBuff.append(1);
				break;
			case FIFTY_MOVE_RULE:
				gameResultBuff.append(2);
				break;
			case THREEFOLD_REPETITION_RULE:
				gameResultBuff.append(3);
				break;
			case STALEMATE:
				gameResultBuff.append(4);
				break;
			default:
				break;
			}
			gameResultBuff.append("]");

		}
		serializedStateBuff.append(gameResultBuff);
		serializedStateBuff.append("-");
		serializedStateBuff.append(state
				.getNumberOfMovesWithoutCaptureNorPawnMoved());
		return serializedStateBuff.toString();
	}

	public State unSrializeState(String stateStr) {
		try {
			String[] splitsStr = stateStr.split("-");

			Color turn;
			if (splitsStr[0].equals("W")) {
				turn = Color.WHITE;
			} else {
				turn = Color.BLACK;
			}

			Piece[][] board = new Piece[State.ROWS][State.COLS];
			for (int i = 0; i < State.ROWS; i++) {
				for (int j = 0; j < State.COLS; j++) {
					Piece piece;
					String pieceStr = splitsStr[1].substring(2 * State.ROWS * i
							+ 2 * j, 2 * State.ROWS * i + 2 * j + 2);
					if (pieceStr.equals("__")) {
						piece = null;
					} else {
						Color pieceCol;
						PieceKind kind = null;
						if (pieceStr.subSequence(0, 1).equals("W")) {
							pieceCol = Color.WHITE;
						} else {
							pieceCol = Color.BLACK;
						}
						if (pieceStr.substring(1).equals("Q")) {
							kind = PieceKind.QUEEN;
						} else if (pieceStr.substring(1).equals("K")) {
							kind = PieceKind.KING;
						} else if (pieceStr.substring(1).equals("N")) {
							kind = PieceKind.KNIGHT;
						} else if (pieceStr.substring(1).equals("B")) {
							kind = PieceKind.BISHOP;
						} else if (pieceStr.substring(1).equals("R")) {
							kind = PieceKind.ROOK;
						} else if (pieceStr.substring(1).equals("P")) {
							kind = PieceKind.PAWN;
						}
						piece = new Piece(pieceCol, kind);
					}
					board[i][j] = piece;
				}
			}

			boolean castleKingWhite = splitsStr[2].substring(0, 1).equals("T") ? true
					: false;
			boolean castleKingBlack = splitsStr[2].substring(1, 2).equals("T") ? true
					: false;
			boolean castleQueenWhite = splitsStr[2].substring(2, 3).equals("T") ? true
					: false;
			boolean castleQueenBlack = splitsStr[2].substring(3, 4).equals("T") ? true
					: false;
			boolean[] castleKingSide = new boolean[] { castleKingWhite,
					castleKingBlack };
			boolean[] castleQueenSide = new boolean[] { castleQueenWhite,
					castleQueenBlack };

			Position enpassantPosition;
			if (splitsStr[3].equals("NULL")) {
				enpassantPosition = null;
			} else {
				int row = Integer.parseInt(splitsStr[3].substring(1, 2));
				int col = Integer.parseInt(splitsStr[3].substring(2, 3));
				enpassantPosition = new Position(row, col);
			}

			GameResult gameResult;
			if (splitsStr[4].equals("[NULL]")) {
				gameResult = null;
			} else {
				Color winner;
				if (splitsStr[4].substring(1, 2).equals("_")) {
					winner = null;
				} else if (splitsStr[4].substring(1, 2).equals("W")) {
					winner = Color.WHITE;
				} else {
					winner = Color.BLACK;
				}
				GameResultReason gameResultReason;
				if (splitsStr[4].substring(2, 3).equals("1")) {
					gameResultReason = GameResultReason.CHECKMATE;
				} else if (splitsStr[4].substring(2, 3).equals("2")) {
					gameResultReason = GameResultReason.FIFTY_MOVE_RULE;
				} else if (splitsStr[4].substring(2, 3).equals("3")) {
					gameResultReason = GameResultReason.THREEFOLD_REPETITION_RULE;
				} else {
					gameResultReason = GameResultReason.STALEMATE;
				}
				gameResult = new GameResult(winner, gameResultReason);
			}

			int numberOfMoves = Integer.parseInt(splitsStr[5]);

			return new State(turn, board, castleKingSide, castleQueenSide,
					enpassantPosition, numberOfMoves, gameResult);
		} catch (Exception e) {
			return new State();
		}
	}
}
