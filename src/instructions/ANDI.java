package instructions;
import java.util.ArrayList;
import stages.*;
import utilities.Operator;

public class ANDI extends Instruction {
	
	public static final int NUMCYCLES = 1;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			return true;
		}

		@Override
		public boolean EX(Instruction i) {
			return super.arithIEx(i, Operator.AND);
		}

		@Override
		public boolean ME(Instruction i) {
			return true;
		}
	};
	
	public ANDI() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		super.arithIParse(params);
	}
}