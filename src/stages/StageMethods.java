package stages;

import java.util.HashMap;
import java.util.Map;

import data.Dependencies;
import data.Registers;
import instructions.Instruction;
import mainpack.Main;
import mainpack.Processor;
import utilities.Operator;

interface run
{
	boolean exectute(Instruction i);
}

public abstract class StageMethods {
	
	public Map<StageEnum, run> map = new HashMap<StageEnum, run>();
	
	public StageMethods()
	{
		map.put(StageEnum.BE,   new run(){public boolean exectute(Instruction i)  {return true;}});
		map.put(StageEnum.IF,   new run(){public boolean exectute(Instruction i)  {return IF(i);}});
		map.put(StageEnum.ID,   new run(){public boolean exectute(Instruction i)  {return ID(i);}});
		map.put(StageEnum.X1,  new run(){public boolean exectute(Instruction i)  {return EX(i);}});
		map.put(StageEnum.X2,  new run(){public boolean exectute(Instruction i)  {return EX(i);}});
		map.put(StageEnum.X3,  new run(){public boolean exectute(Instruction i)  {return EX(i);}});
		map.put(StageEnum.ME,   new run(){public boolean exectute(Instruction i)  {return ME(i);}});
		map.put(StageEnum.WB,   new run(){public boolean exectute(Instruction i)  {return WB(i);}});
		map.put(StageEnum.FIN,   new run(){public boolean exectute(Instruction i)  {return true;}});
	}
	
	public boolean IF(Instruction i)
	{	
		i.justPreped = false;
		
		if(Main.ICache.isInCache(i.instAddress))
		{
			if(!i.iCacheAcc)
			{
				i.iCacheAcc = true;
				Processor.ICacheReq++;
				Processor.ICacheHit++;
			}
			
			return true;
		}
		
		if(!i.iCacheAcc)
		{
			i.iCacheAcc = true;
			Processor.ICacheReq++;
		}
		
		i.needsICaching = true;

		return false;
	}
	
	public abstract boolean ID(Instruction i);
	public abstract boolean EX(Instruction i);
	public abstract boolean ME(Instruction i);
	
	//Will always return true but need to specify the write
	public  boolean WB(Instruction i)
	{
		Registers.writeRegister(i.getWrite(), i.getData());
		return true;
	}
	
	public boolean executeStage(StageEnum curStage, Instruction i)
	{
		return map.get(curStage).exectute(i);
	}
	
	
	//Helper methods for arithmetic immediate and R types, the most common instruction types
	public boolean arithREx(Instruction i, Operator o)
	{
		if(Dependencies.handleDependency(i.getRead1(), i) && Dependencies.handleDependency(i.getRead2(), i))
		{
			i.setData(o.doArith(i.getValue1(), i.getValue2()));
			return true;
		}
		return false;
	}
	
	public boolean arithIEx(Instruction i, Operator o)
	{
		if(Dependencies.handleDependency(i.getRead1(), i))
		{
			i.setData(o.doArith(i.getValue1(), i.getImmediate()));
			return true;
		}
		return false;
	}
}
