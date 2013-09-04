package com.org.zhaohui.client.game;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Chess implements EntryPoint {

	@Override
	public void onModuleLoad() {
		final ChessView view = new ChessView();
		ChessPresenter chessPresenter = new ChessPresenter();
		chessPresenter.initView(view);
		RootPanel.get().add(view);	
	}
	
}
