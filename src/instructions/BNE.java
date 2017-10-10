package instructions;
import java.util.ArrayList;

import data.Dependencies;
import stages.*;
import utilities.Parser;

public class BNE extends Instruction {
	
	public static final int NUMCYCLES = 0;
	private boolean done = false;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			if(Dependencies.handleDependency(i.getRead1(), i) && 
					Dependencies.handleDependency(i.getRead2(), i))
			{
				if(i.getValue1() != i.getValue2())
				{
					i.setBranch(true);
				}
				return true;
			}
			return false;
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
	
	public BNE() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		super.read1 = Parser.registerParse(params.get(0));
		super.read2 = Parser.registerParse(params.get(1));
		super.toLabel = params.get(2);
	}
	
	@Override
	public boolean execute(StageEnum stage)
	{
		//If in IF, doThing except ALWAYS return true
		if(this.getCurStage() == StageEnum.IF)
		{
			if(stageMethods.executeStage(StageEnum.ID, this))
			{
				this.done = true;
			}
			return true;
		}
		
		//If in ID, ALWAYS execute ID
		else if(this.getCurStage() == StageEnum.ID)
		{
			if(this.done)
			{
				this.setCurStage(StageEnum.WB);
				return true;
			}
			
			else if(stageMethods.executeStage(StageEnum.ID, this))
			{
				this.setCurStage(StageEnum.IF);
				this.done = true;
				return true;
			}
			return false;
		}
		return super.execute(stage);
	}
}