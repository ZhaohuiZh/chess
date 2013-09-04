package com.org.zhaohui.shared.logics;

import java.util.Set;

import com.google.common.collect.Sets;
import com.org.zhaohui.shared.basics.Color;
import com.org.zhaohui.shared.basics.GameResult;
import com.org.zhaohui.shared.basics.GameResultReason;
import com.org.zhaohui.shared.basics.IllegalMove;
import com.org.zhaohui.shared.basics.Move;
import com.org.zhaohui.shared.basics.Piece;
import com.org.zhaohui.shared.basics.PieceKind;
import com.org.zhaohui.shared.basics.Position;
import com.org.zhaohui.shared.basics.State;

/**
 * The State Changer Implementation class, to get an instance of this class, use
 * getInstance() method (singleton pattern adopted)
 * 
 * @author zhaohui
 * 
 */
public class StateChangerImpl implements StateChanger {
  private static StateChangerImpl instance;

  private StateChangerImpl() {
  }

  public static StateChangerImpl getInstance() {
    if (instance == null) {
      synchronized (StateChangerImpl.class) {
        if (instance == null) {
          return new StateChangerImpl();
        }
      }
    }
    return instance;
  }

  private StateExplorer stateExplorer = StateExplorerImpl.getInstance();

  @Override
  public void makeMove(State state, Move move) throws IllegalMove {

    // Implement the logic without specific pieces
    implGeneralIllegalLogic(state, move);

    Position from = move.getFrom();
    Set<Move> moves = Sets.newHashSet();

    moves.addAll(stateExplorer.getPossibleMovesFromPosition(state, from));

    if (!moves.contains(move)) {
      throw new IllegalMove();
    }

    Piece piece = state.getPiece(from);
    PieceKind pieceKind = piece.getKind();
    Color color = piece.getColor();

    // Implement the logic for specific pieces
    if (state.getPiece(move.getTo()) != null
        && state.getPiece(move.getTo()).getKind() == PieceKind.ROOK
        && (move.getTo().getRow() == 0 || move.getTo().getRow() == 7)) {
      if (move.getTo().getCol() == 0) {
        state.setCanCastleQueenSide(color == Color.WHITE ? Color.BLACK
            : Color.WHITE, false);
      } else if (move.getTo().getCol() == 7) {
        state.setCanCastleKingSide(color == Color.WHITE ? Color.BLACK
            : Color.WHITE, false);
      }
    }
    if (pieceKind == PieceKind.PAWN) {
      changeStateForPawn(state, move);
      state.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
    } else {
      if (state.getPiece(move.getTo()) != null) {
        state.setNumberOfMovesWithoutCaptureNorPawnMoved(0);
      } else {
        state.setNumberOfMovesWithoutCaptureNorPawnMoved(state
            .getNumberOfMovesWithoutCaptureNorPawnMoved() + 1);
      }
      if (piece.getKind() == PieceKind.KING) {
        changeStateForKing(state, move);
      } else {
        changeStateForOthers(state, move);
      }
      state.setEnpassantPosition(null);
    }

    state.setTurn(color == Color.WHITE ? Color.BLACK : Color.WHITE);

    if (state.getNumberOfMovesWithoutCaptureNorPawnMoved() >= 100) {
      GameResult gameResult = new GameResult(null,
          GameResultReason.FIFTY_MOVE_RULE);
      state.setGameResult(gameResult);
    } else {
      setGameResult(state);
    }

  }

  private void implGeneralIllegalLogic(State state, Move move) {
    if (state.getGameResult() != null) {
      // Game already ended!
      throw new IllegalMove();
    }

    Position from = move.getFrom();
    Piece piece = state.getPiece(from);

    if (piece == null) {
      // Nothing to move!
      throw new IllegalMove();
    }
    // define basic variables used after
    Color color = piece.getColor();
    Position to = move.getTo();
    int fromRow = from.getRow();
    int fromCol = from.getCol();
    int toRow = to.getRow();
    int toCol = to.getCol();

    if (color != state.getTurn()) {
      // Wrong player moves!
      throw new IllegalMove();
    }

    if (from.equals(to)) {
      // Do not move!
      throw new IllegalMove();
    }

    if (fromCol > 7 || fromCol < 0 || fromRow > 7 || fromRow < 0 || toCol > 7
        || toCol < 0 || toRow > 7 || toRow < 0) {
      // wrong position out of range!
      throw new IllegalMove();
    }

    if (state.getPiece(to) != null && state.getPiece(to).getColor() == color) {
      // move to the position occupied by the same color!
      throw new IllegalMove();
    }

    if (move.getPromoteToPiece() != null) {
      if (move.getPromoteToPiece() == PieceKind.PAWN
          || move.getPromoteToPiece() == PieceKind.KING) {
        // pawn cannot promote to pawn or king
        throw new IllegalMove();
      }
      if (state.getPiece(from).getKind() != PieceKind.PAWN) {
        // only pawn can promote
        throw new IllegalMove();
      }
      if (color == Color.WHITE && to.getRow() != 7) {
        throw new IllegalMove();
      }
      if (color == Color.BLACK && to.getRow() != 0) {
        throw new IllegalMove();
      }
    }

  }

  private void changeStateForPawn(State state, Move move) {
    Color color = state.getPiece(move.getFrom()).getColor();

    if (move.getPromoteToPiece() != null) {
      state.setPiece(move.getFrom(), null);
      state.setPiece(move.getTo(), new Piece(color, move.getPromoteToPiece()));
    } else {
      state.setPiece(move.getFrom(), null);
      state.setPiece(move.getTo(), new Piece(color, PieceKind.PAWN));
      Position enpassantPos = state.getEnpassantPosition();
      if (enpassantPos != null
          && state.getPiece(enpassantPos).getColor() != color
          && enpassantPos.getRow() == move.getFrom().getRow()
          && enpassantPos.getCol() == move.getTo().getCol()) {
        state.setPiece(enpassantPos, null);
        state.setEnpassantPosition(null);
      }
    }
    if (color == Color.WHITE) {
      if (move.getFrom().getRow() == 1 && move.getTo().getRow() == 3) {
        state.setEnpassantPosition(move.getTo());
      } else {
        state.setEnpassantPosition(null);
      }
    } else {
      if (move.getFrom().getRow() == 6 && move.getTo().getRow() == 4) {
        state.setEnpassantPosition(move.getTo());
      } else {
        state.setEnpassantPosition(null);
      }
    }
  }

  private void changeStateForKing(State state, Move move) {
    Color color = state.getPiece(move.getFrom()).getColor();
    Position from = move.getFrom();
    Position to = move.getTo();
    int colDiff = Math.abs(from.getCol() - to.getCol());
    int rowDiff = Math.abs(from.getRow() - to.getRow());

    if (colDiff == 2 && rowDiff == 0) {
      int diff = (from.getCol() - to.getCol()) / colDiff;
      StateExplorerImpl stateExplorerImpl = StateExplorerImpl.getInstance();
      State tmp = state.copy();

      for (int row = 0; row < State.ROWS; row++) {
        for (int col = 0; col < State.COLS; col++) {
          Position tmpPos = new Position(row, col);
          if (tmp.getPiece(tmpPos) != null
              && tmp.getPiece(tmpPos).getColor() != tmp.getTurn()
              && (stateExplorerImpl.isCanCheckMate(tmp, tmpPos, from) || stateExplorerImpl
                  .isCanCheckMate(tmp, tmpPos,
                      new Position(from.getRow(), from.getCol() - diff)))) {
            throw new IllegalMove();
          }
        }
      }

      if (to.getCol() - from.getCol() > 0) {
        state.setPiece(new Position(from.getRow(), from.getCol() + 1),
            new Piece(color, PieceKind.ROOK));
        state.setPiece(from.getRow(), 7, null);
      } else {
        state.setPiece(new Position(from.getRow(), from.getCol() - 1),
            new Piece(color, PieceKind.ROOK));
        state.setPiece(from.getRow(), 0, null);
      }
    }
    state.setPiece(from, null);
    state.setPiece(to, new Piece(color, PieceKind.KING));
    state.setEnpassantPosition(null);
    state.setCanCastleKingSide(color, false);
    state.setCanCastleQueenSide(color, false);
  }

  private void changeStateForOthers(State state, Move move) {
    Piece piece = state.getPiece(move.getFrom());
    state.setPiece(move.getFrom(), null);
    state.setPiece(move.getTo(), piece);
    state.setEnpassantPosition(null);
    if (piece.getKind() == PieceKind.ROOK) {
      if (move.getFrom().getCol() == 0) {
        state.setCanCastleQueenSide(piece.getColor(), false);
      } else if (move.getFrom().getCol() == 7) {
        state.setCanCastleKingSide(piece.getColor(), false);
      }
    }
  }

  private void setGameResult(State state) {
    StateExplorerImpl stateExplorerImpl = StateExplorerImpl.getInstance();
    Set<Position> legalPositionForOtherSide = Sets.newHashSet();
    legalPositionForOtherSide.addAll(stateExplorer
        .getPossibleStartPositions(state));

    if (legalPositionForOtherSide == null
        || legalPositionForOtherSide.isEmpty()) {
      GameResult gameResult = null;
      Position kingPos = null;
      for (int r = 0; r < State.ROWS; r++) {
        for (int c = 0; c < State.COLS; c++) {
          if (state.getPiece(r, c) != null
              && state.getPiece(r, c).getColor() == state.getTurn()
              && state.getPiece(r, c).getKind() == PieceKind.KING) {
            kingPos = new Position(r, c);
          }
        }
      }
      if (kingPos == null) {
        throw new IllegalMove();
      }
      for (int row = 0; row < State.ROWS; row++) {
        for (int col = 0; col < State.COLS; col++) {
          Position tmpPos = new Position(row, col);
          if (state.getPiece(tmpPos) != null
              && state.getPiece(tmpPos).getColor() != state.getTurn()
              && stateExplorerImpl.isCanCheckMate(state, tmpPos, kingPos)) {
            gameResult = new GameResult(
                state.getTurn() == Color.WHITE ? Color.BLACK : Color.WHITE,
                GameResultReason.CHECKMATE);
            state.setGameResult(gameResult);

          }
        }
      }
      if (gameResult == null) {
        gameResult = new GameResult(null, GameResultReason.STALEMATE);
        state.setGameResult(gameResult);
      }

    }
  }

}
