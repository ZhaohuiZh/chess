package com.org.zhaohui.client.game;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.org.zhaohui.shared.basics.Color;

public class GamePresenter {
	public interface Display {
		// Indicate opponent
		void setOpponent(String opponentName);

		// Indicate
		void setPlayerColor(Color color);

		void setPlayerInfo(String email, String nickName);
		
		String getOpponentEmail();
		
		HasClickHandlers getClickMakeMatch();

		HasClickHandlers getClickFindOpponent();
	}
}
