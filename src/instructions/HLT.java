package instructions;
import java.util.ArrayList;

import mainpack.Processor;

import stages.*;

public class HLT extends Instruction {
	
	public static final int NUMCYCLES = 0;
	private boolean done = false;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			Processor.halt();
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
	
	public HLT() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		//Does nothing
	}
	
	@Override
	public boolean execute(StageEnum stage)
	{
		if(done)
		{
			this.setCurStage(StageEnum.WB);
			return true;
		}
		
		if(stageMethods.executeStage(stage, this))
		{
			if(stage == StageEnum.ID)
			{
				done = true;
			}
			return true;
		}
		return false;
	}
}