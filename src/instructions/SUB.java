package instructions;
import java.util.ArrayList;
import stages.*;
import utilities.Operator;

public class SUB extends Instruction {
	
	public static final int NUMCYCLES = 2;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			return true;
		}

		@Override
		public boolean EX(Instruction i) {
			return super.arithREx(i, Operator.SUB);
		}

		@Override
		public boolean ME(Instruction i) {
			return true;
		}
	};
	
	public SUB() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		super.arithRParse(params);
	}
}