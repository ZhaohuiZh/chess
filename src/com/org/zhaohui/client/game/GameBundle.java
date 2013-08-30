package com.org.zhaohui.client.game;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface GameBundle extends ClientBundle {
	@Source("bishopW.png")
	ImageResource whiteBishop();

	@Source("bishopB.png")
	ImageResource blackBishop();

	@Source("kingB.png")
	ImageResource blackKing();

	@Source("kingW.png")
	ImageResource whiteKing();

	@Source("pawnB.png")
	ImageResource blackPawn();

	@Source("pawnW.png")
	ImageResource whitePawn();

	@Source("queenB.png")
	ImageResource blackQueen();

	@Source("queenW.png")
	ImageResource whiteQueen();

	@Source("rookB.png")
	ImageResource blackRook();

	@Source("rookW.png")
	ImageResource whiteRook();

	@Source("knightB.png")
	ImageResource blackKnight();

	@Source("knightW.png")
	ImageResource whiteKnight();

}
