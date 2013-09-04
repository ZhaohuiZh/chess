package com.org.zhaohui.client.game;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.org.zhaohui.client.game.ChessPresenter.View;
import com.org.zhaohui.shared.basics.Color;
import com.org.zhaohui.shared.basics.GameResult;
import com.org.zhaohui.shared.basics.Piece;
import com.org.zhaohui.shared.basics.PieceKind;
import com.org.zhaohui.shared.basics.Position;
import com.org.zhaohui.shared.basics.State;

public class ChessView extends Composite implements View {
  interface ChessViewUiBinder extends UiBinder<Widget, ChessView> {
  }

  private static GameBundle gameImages = GWT.create(GameBundle.class);
  private static ChessViewUiBinder uiBinder = GWT.create(ChessViewUiBinder.class);

  @UiField
  GameCss css;
  @UiField
  Grid gameGrid;
  @UiField
  Grid blackPromotionGrid;
  @UiField
  Grid whitePromotionGrid;
  @UiField
  Label gameStatus;
  @UiField
  Button saveGame;
  @UiField
  Button loadGame;
  @UiField
  Button startGame;
  @UiField
  Button quickStart;

  private final Image[][] board = new Image[8][8];
  private final Image[][] promotionWhite = new Image[1][4];
  private final Image[][] promotionBlack = new Image[1][4];

  public ChessView() {
    initWidget(uiBinder.createAndBindUi(this));
    initView();
  }

  private void initView() {
    saveGame.setText("Save");
    loadGame.setText("Load");
    startGame.setText("Start");

    quickStart.setText("Quick Start");

    gameGrid.resize(8, 8);
    gameGrid.setCellPadding(0);
    gameGrid.setCellSpacing(0);
    gameGrid.setBorderWidth(0);
    gameGrid.setWidth("50px");

    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        final Image image = new Image();
        board[row][col] = image;
        image.setWidth("100%");
        image.setResource(gameImages.empty());
        gameGrid.setWidget(row, col, image);

      }
    }
    
    whitePromotionGrid.resize(1, 4);
    whitePromotionGrid.setCellPadding(0);
    whitePromotionGrid.setCellSpacing(0);
    whitePromotionGrid.setBorderWidth(0);

    blackPromotionGrid.resize(1, 4);
    blackPromotionGrid.setCellPadding(0);
    blackPromotionGrid.setCellSpacing(0);
    blackPromotionGrid.setBorderWidth(0);
    blackPromotionGrid.setWidth("50px");

    for (int col = 0; col < 4; col++) {
      final Image image = new Image();
      promotionWhite[0][col] = image;
      image.setWidth("100%");
      image.setResource(gameImages.empty());
      whitePromotionGrid.setWidget(0, col, image);
    }
    
    for (int col = 0; col < 4; col++) {
      final Image image = new Image();
      promotionBlack[0][col] = image;
      image.setWidth("100%");
      image.setResource(gameImages.empty());
      blackPromotionGrid.setWidget(0, col, image);
    }
    
    promotionWhite[0][0].setResource(gameImages.whiteRook());
    promotionWhite[0][1].setResource(gameImages.whiteBishop());
    promotionWhite[0][2].setResource(gameImages.whiteKnight());
    promotionWhite[0][3].setResource(gameImages.whiteQueen());
    
    promotionBlack[0][0].setResource(gameImages.blackRook());
    promotionBlack[0][1].setResource(gameImages.blackBishop());
    promotionBlack[0][2].setResource(gameImages.blackKnight());
    promotionBlack[0][3].setResource(gameImages.blackQueen());
    
    whitePromotionGrid.setVisible(false);
    blackPromotionGrid.setVisible(false);

  }

  @Override
  public void setPiece(int row, int col, Piece piece) {
    int r = State.ROWS - row - 1;
    int c = col;
    if (piece == null) {
      board[r][c].setResource(gameImages.empty());
    } else {
      PieceKind pieceKind = piece.getKind();
      switch (pieceKind) {
      case PAWN:
        if (piece.getColor() == Color.WHITE) {
          board[r][c].setResource(gameImages.whitePawn());
        } else {
          board[r][c].setResource(gameImages.blackPawn());
        }
        break;
      case ROOK:
        if (piece.getColor() == Color.WHITE) {
          board[r][c].setResource(gameImages.whiteRook());
        } else {
          board[r][c].setResource(gameImages.blackRook());
        }
        break;
      case BISHOP:
        if (piece.getColor() == Color.WHITE) {
          board[r][c].setResource(gameImages.whiteBishop());
        } else {
          board[r][c].setResource(gameImages.blackBishop());
        }
        break;
      case KNIGHT:
        if (piece.getColor() == Color.WHITE) {
          board[r][c].setResource(gameImages.whiteKnight());
        } else {
          board[r][c].setResource(gameImages.blackKnight());
        }
        break;
      case QUEEN:
        if (piece.getColor() == Color.WHITE) {
          board[r][c].setResource(gameImages.whiteQueen());
        } else {
          board[r][c].setResource(gameImages.blackQueen());
        }
        break;
      case KING:
        if (piece.getColor() == Color.WHITE) {
          board[r][c].setResource(gameImages.whiteKing());
        } else {
          board[r][c].setResource(gameImages.blackKing());
        }
        break;
      default:
        break;
      }
    }

  }
  
  @Override
  public void setWhoseTurn(Color color) {
    String currTurn = color == Color.WHITE? "White": "Black";
    gameStatus.setText("Turn: " + currTurn);
  }
  
  @Override
  public void setGameResult(GameResult gameResult) {
    if(gameResult != null){
      if(gameResult.getWinner() == null ){
        gameStatus.setText("Draw: " + gameResult.getGameResultReason());
      }else{
        String winner = gameResult.getWinner() == Color.WHITE? "White": "Black";
        gameStatus.setText("Winner: " + winner);
      }
    }

  }
  
  @Override
  public void setHighlighted(int row, int col, boolean highlighted) {
    Element element = board[State.ROWS - row - 1][col].getElement();
    if(highlighted){
      element.setClassName(css.highlighted());
    }else{
      element.removeClassName(css.highlighted());
    }
  }
  
  @Override
  public void setPromotionGrid(boolean flag, Color color) {
    if(color == Color.WHITE){
      whitePromotionGrid.setVisible(flag);
    }else{
      blackPromotionGrid.setVisible(flag);
    }
  }
	@Override
	public void setOpponent(String opponentName) {
		
	}

	@Override
	public void setPlayerColor(Color color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayerInfo(String email, String nickName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getOpponentEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasClickHandlers getClickMakeMatch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasClickHandlers getClickFindOpponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPromotionPiece() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HasClickHandlers getClickCellOnChessBoard(int row, int col) {
	  return board[State.ROWS - row - 1][col];
	}

	/*
	@Override
	public HasDragStartHandlers getDraggedCellOnChessBoard(int row, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasDropHandlers getDroppedCellOnChessBoard(int row, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasDragOverHandlers getDraggedOverCellOnChessBoard(int row, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasDragLeaveHandlers getDraggedLeaveCellOnChessBoard(int row, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	*/
	@Override
	public HasClickHandlers getClickPromotionKindWhite(int row) {
	  return promotionWhite[0][row];
	}

	@Override
	public HasClickHandlers getClickPromotionKindBlack(int row) {
	  return promotionBlack[0][row];
	}

	@Override
	public HasClickHandlers getClickStartGame() {
		return startGame;
	}

	@Override
	public HasClickHandlers getClickSaveGame() {
		return saveGame;
	}

	@Override
	public HasClickHandlers getClickLoadGame() {
		return loadGame;
	}

	@Override
	public void addHistoryItem(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getHistoryItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addHistoryHandler(ValueChangeHandler<String> handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void animation(Position p) {
		// TODO Auto-generated method stub
		
	}

}
