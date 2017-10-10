package instructions;
import java.util.ArrayList;
import stages.*;
import utilities.Operator;

public class ADD extends Instruction {
	
	public static final int NUMCYCLES = 2;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			return true;
		}

		@Override
		public boolean EX(Instruction i) {
			return super.arithREx(i, Operator.ADD);
		}

		@Override
		public boolean ME(Instruction i) {
			return true;
		}
	};
	
	public ADD() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		super.arithRParse(params);
	}
}