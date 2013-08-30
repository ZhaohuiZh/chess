package com.org.zhaohui.client.game;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDragLeaveHandlers;
import com.google.gwt.event.dom.client.HasDragOverHandlers;
import com.google.gwt.event.dom.client.HasDragStartHandlers;
import com.google.gwt.event.dom.client.HasDropHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.org.zhaohui.client.game.ChessPresenter.View;
import com.org.zhaohui.client.game.GamePresenter.Display;
import com.org.zhaohui.shared.basics.Color;
import com.org.zhaohui.shared.basics.GameResult;
import com.org.zhaohui.shared.basics.Piece;
import com.org.zhaohui.shared.basics.Position;

public class ChessView extends Composite implements View, Display {
	
	

	@Override
	public void setOpponent(String opponentName) {
		// TODO Auto-generated method stub
		
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
	public void setPiece(int row, int col, Piece piece) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHighlighted(int row, int col, boolean highlighted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWhoseTurn(Color color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGameResult(GameResult gameResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPromotionPiece() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPromotionGrid(boolean flag, Color color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HasClickHandlers getClickCellOnChessBoard(int row, int col) {
		// TODO Auto-generated method stub
		return null;
	}

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

	@Override
	public HasClickHandlers getClickPromotionKindWhite(int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasClickHandlers getClickPromotionKindBlack(int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasClickHandlers getClickStartGame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasClickHandlers getClickSaveGame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasClickHandlers getClickLoadGame() {
		// TODO Auto-generated method stub
		return null;
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
