package com.org.zhaohui.shared.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class MoveTest {

	Move move1;
	Move move1Eq;
	Move move2Promote;
	Move move2PromoteEq;
	Move move3;

	@Before
	public void setup() {
		this.move1 = new Move(new Position(1, 1), new Position(2, 2), null);
		this.move1Eq = new Move(new Position(1, 1), new Position(2, 2), null);
		this.move2Promote = new Move(new Position(1, 1), new Position(2, 2),
				PieceKind.BISHOP);
		this.move2PromoteEq = new Move(new Position(1, 1), new Position(2, 2),
				PieceKind.BISHOP);
		this.move3 = new Move(new Position(1, 1), new Position(3, 3), null);
	}

	@Test
	public void testEquals() {
		assertFalse(move1.equals(null));
		assertFalse(move1.equals(42));
		assertEquals(move1, move1Eq);
		assertEquals(move2Promote, move2PromoteEq);
		assertFalse(move1.equals(move2Promote));
		assertFalse(move1.equals(move3));
		assertFalse(move2Promote.equals(move3));
	}

	@Test
	public void testHashCode() {
		assertEquals(move1.hashCode(), move1Eq.hashCode());
		assertEquals(move2Promote.hashCode(), move2PromoteEq.hashCode());
	}

	@Test
	public void testToString() {
		assertEquals("(1,1)->(2,2) (promoting to BISHOP)",
				move2Promote.toString());
	}

}
