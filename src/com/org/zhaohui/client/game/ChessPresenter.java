package com.org.zhaohui.client.game;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDragLeaveHandlers;
import com.google.gwt.event.dom.client.HasDragOverHandlers;
import com.google.gwt.event.dom.client.HasDragStartHandlers;
import com.google.gwt.event.dom.client.HasDropHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.org.zhaohui.shared.basics.Color;
import com.org.zhaohui.shared.basics.GameResult;
import com.org.zhaohui.shared.basics.Piece;
import com.org.zhaohui.shared.basics.Position;
import com.org.zhaohui.shared.basics.State;

public class ChessPresenter {
  public interface View {
    // Renders the piece at this position. If piece is null then the
    // position is empty.
    void setPiece(int row, int col, Piece piece);

    // Turns the highlighting on or off at this cell. Cells that can be
    // clicked should be highlighted
    void setHighlighted(int row, int col, boolean highlighted);

    // Indicate whose turn it is.
    void setWhoseTurn(Color color);

    // Indicate whether the game is in progress or over.
    void setGameResult(GameResult gameResult);

    void setPromotionPiece();

    void setPromotionGrid(boolean flag, Color color);

    HasClickHandlers getClickCellOnChessBoard(int row, int col);

    // HasDragStartHandlers getDraggedCellOnChessBoard(int row, int col);

    // HasDropHandlers getDroppedCellOnChessBoard(int row, int col);

    // HasDragOverHandlers getDraggedOverCellOnChessBoard(int row, int col);

    // HasDragLeaveHandlers getDraggedLeaveCellOnChessBoard(int row, int col);

    HasClickHandlers getClickPromotionKindWhite(int row);

    HasClickHandlers getClickPromotionKindBlack(int row);

    HasClickHandlers getClickStartGame();

    HasClickHandlers getClickSaveGame();

    HasClickHandlers getClickLoadGame();

    void addHistoryItem(String str);

    String getHistoryItem();

    void addHistoryHandler(ValueChangeHandler<String> handler);

    void animation(Position p);

    // Indicate opponent
    void setOpponent(String opponentName);

    // Indicate
    void setPlayerColor(Color color);

    void setPlayerInfo(String email, String nickName);

    String getOpponentEmail();

    HasClickHandlers getClickMakeMatch();

    HasClickHandlers getClickFindOpponent();

  }

  private View view;
  private State state;
  private Storage storage = Storage.getLocalStorageIfSupported();
  private String historyToken = null;
  private final StateSerializer stateSerializer = StateSerializer.getInstance();

  public void initView(ChessView graphics) {
    this.setView(graphics);
    state = new State();
    this.setState(state);
    view.setPromotionPiece();
    bindHandler();
  }

  public void setView(View view) {
    this.view = view;
  }

  public void setState(State state) {
    view.setWhoseTurn(state.getTurn());
    if (state.getGameResult() != null) {
      Audio gameResultAudio = creatGameResultAudio();
      gameResultAudio.play();
    }
    view.setGameResult(state.getGameResult());
    for (int row = 0; row < State.ROWS; row++) {
      for (int col = 0; col < State.COLS; col++) {
        view.setPiece(row, col, state.getPiece(row, col));
      }
    }
    view.addHistoryItem(stateSerializer.serializeState(state));

  }

  public void bindHandler() {
    for (int row = 0; row < State.ROWS; row++) {
      for (int col = 0; col < State.COLS; col++) {
        final int r = row;
        final int c = col;
        view.getClickCellOnChessBoard(row, col).addClickHandler(
            new ClickHandler() {
              public void onClick(ClickEvent event) {
                clickedOn(r, c);
              }
            });
      }
    }

    for (int row = 0; row < 4; row++) {
      final int n = row;
      view.getClickPromotionKindBlack(row).addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
         // getPromotionType(n, Color.BLACK);
        }
      });
      view.getClickPromotionKindWhite(row).addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          //getPromotionType(n, Color.WHITE);
        }
      });
    }

    view.getClickStartGame().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        state = new State();
        setState(state);
      }

    });

    view.addHistoryHandler(new ValueChangeHandler<String>() {

      @Override
      public void onValueChange(ValueChangeEvent<String> event) {
        historyToken = event.getValue();
        String currentState = stateSerializer.serializeState(state);
        if (!historyToken.equals(currentState)) {
          State newState = stateSerializer.unSrializeState(historyToken);
          state = newState;
          setState(state);
        }

      }

    });

    view.getClickSaveGame().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (storage != null) {
          storage.setItem("storedState", historyToken);
        } else {
          Window
              .alert("The save & load function is not supported. Try upgrading to a newer browser!");
        }
      }

    });

    view.getClickLoadGame().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        if (storage != null) {
          String value = storage.getItem("storedState");
          State newState = stateSerializer.unSrializeState(value);
          state = newState;
          setState(state);
        } else {
          Window
              .alert("The save & load function is not supported. Try upgrading to a newer browser!");
        }

      }
    });

  }

  public Audio creatMoveAudio() {
    Audio audio = Audio.createIfSupported();
    if (audio == null) {
      Window
          .alert("The sound of the chess is not supported. Try upgrading to a newer browser!");
    }
    audio.addSource("sound_zhaohuizhang/move.ogg", AudioElement.TYPE_OGG);
    audio.addSource("sound_zhaohuizhang/move.wav", AudioElement.TYPE_WAV);
    audio.setControls(true);
    return audio;
  }

  public Audio creatCaptureAudio() {
    Audio audio = Audio.createIfSupported();
    if (audio == null) {
      Window
          .alert("The sound of the chess is not supported. Try upgrading to a newer browser!");
    }
    audio.addSource("sound_zhaohuizhang/capture.ogg", AudioElement.TYPE_OGG);
    audio.addSource("sound_zhaohuizhang/capture.wav", AudioElement.TYPE_WAV);
    audio.setControls(true);
    return audio;
  }

  public Audio creatGameResultAudio() {
    Audio audio = Audio.createIfSupported();
    if (audio == null) {
      Window
          .alert("The sound of the chess is not supported. Try upgrading to a newer browser!");
    }
    audio.addSource("sound_zhaohuizhang/gameresult.ogg", AudioElement.TYPE_OGG);
    audio.addSource("sound_zhaohuizhang/gameresult.wav", AudioElement.TYPE_WAV);
    audio.setControls(true);
    return audio;
  }
  
  private void clickedOn(int row, int col){
    
  }
  
}
