package instructions;
import java.util.ArrayList;

import data.Dependencies;
import data.Memory;
import mainpack.Main;
import mainpack.Processor;
import stages.*;
import utilities.Parser;

public class LW extends Instruction {
	
	public static final int NUMCYCLES = 0;
	public static final StageMethods STAGEMETHODS = new StageMethods()
	
	{
		@Override
		public boolean ID(Instruction i) {
			return true;
		}

		@Override
		public boolean EX(Instruction i) {
			if(Dependencies.handleDependency(i.getRead1(), i))
			{
				i.setOffsetPlusAddress(i.getImmediate() + i.getValue1());
				return true;
			}
			return false;
		}

		@Override
		public boolean ME(Instruction i) {
			
			int address = i.getOffsetPlusAddress();
			
			if(Main.DCache.isInCache(address))
			{
				if(!i.dCacheAcc)
				{
					i.dCacheAcc = true;
					Processor.DCacheReq++;
					Processor.DCacheHit++;
				}
				
				i.setData(Memory.deref(address));
				return true;
			}
			
			if(!i.dCacheAcc)
			{
				i.dCacheAcc = true;
				Processor.DCacheReq++;
			}
			
			i.needsDCaching = true;
			return false;
		}
	};
	
	public LW() {
		super(STAGEMETHODS, NUMCYCLES);
	}

	@Override
	public void parse(ArrayList<String> params) {
		super.write = Parser.registerParse(params.get(0));
		
		String str = params.get(1);
		int index = str.indexOf("(");
		
		super.immediate = Integer.parseInt(str.substring(0, index));
		
		//+1 will exclude "(" and -1 will exclude ")"
		str = str.substring(index + 1, str.length() - 1);
		
		super.read1 = Parser.registerParse(str);
	}
}