package instructions;
import java.util.ArrayList;

import stages.*;
import utilities.Parser;

public class LI extends Instruction {
	
	public static final int NUMCYCLES = 0;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			i.setData(i.getImmediate());
			return true;
		}

		@Override
		public boolean EX(Instruction i) {
			return true;
		}

		@Override
		public boolean ME(Instruction i) {
			return true;
		}
	};
	
	public LI() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		super.write = Parser.registerParse(params.get(0));
		super.immediate = Integer.parseInt(params.get(1));
	}
}