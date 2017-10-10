package instructions;
import java.util.ArrayList;
import stages.*;

public class J extends Instruction {
	
	public static final int NUMCYCLES = 0;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			i.setBranch(true);
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
	
	public J() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		super.toLabel = params.get(0);
	}
}