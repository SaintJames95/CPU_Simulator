package instructions;
import java.util.ArrayList;
import stages.*;
import utilities.Operator;

public class MULTI extends Instruction {
	
	public static final int NUMCYCLES = 3;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			return true;
		}

		@Override
		public boolean EX(Instruction i) {
			return super.arithIEx(i, Operator.MULT);
		}

		@Override
		public boolean ME(Instruction i) {
			return true;
		}
	};
	
	public MULTI() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		super.arithIParse(params);
	}
}