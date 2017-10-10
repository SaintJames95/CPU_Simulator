package instructions;
import java.util.ArrayList;
import stages.*;
import utilities.Operator;

public class MULT extends Instruction {
	
	public static final int NUMCYCLES = 3;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			return true;
		}

		@Override
		public boolean EX(Instruction i) {
			return super.arithREx(i, Operator.MULT);
		}

		@Override
		public boolean ME(Instruction i) {
			return true;
		}
	};
	
	public MULT() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		super.arithRParse(params);
	}
}