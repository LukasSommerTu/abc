package abc.ja.bytecode;

import abc.ja.jrag.BooleanLiteral;
import abc.ja.jrag.Expr;
import abc.ja.jrag.IntegerLiteral;


class CONSTANT_Integer_Info extends CONSTANT_Info {
	public int value;

	public CONSTANT_Integer_Info(Parser parser) {
		super(parser);
		value = p.readInt();
	}

	public String toString() {
		return "IntegerInfo: " + Integer.toString(value);
	}

	public Expr expr() {
		//return new IntegerLiteral(Integer.toString(value));
		return new IntegerLiteral("0x" + Integer.toHexString(value));
	}
	public Expr exprAsBoolean() {
		return new BooleanLiteral(value == 0 ? "false" : "true");
	}
}
