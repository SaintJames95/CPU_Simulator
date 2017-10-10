package data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import instructions.Instruction;

public class Dependencies {
	
	private static Map<Integer, ArrayList<Instruction>> dependencies 
	= new HashMap<Integer, ArrayList<Instruction>>();

	public static void updateDependcies(Instruction i)
	{
		//if any register is for writing, there is a dependency
		if(!(i.getWrite() == null))
		{
			//If this register is already listed, modify the list
			if(dependencies.containsKey(i.getWrite()))
			{ 
				ArrayList<Instruction> myList = dependencies.get(i.getWrite());
				myList.add(0, i);
				dependencies.put(i.getWrite(), myList);
			}
			
			//otherwise new list
			else
			{
				ArrayList<Instruction> myList =  new ArrayList<Instruction>();
				myList.add(i);
				dependencies.put(i.getWrite(), myList);
			}
		}
	}
	
	public static boolean handleDependency(int readReg, Instruction i) {
		if(dependencies.containsKey(readReg))
		{ 
			//int j = 0;
			ArrayList<Instruction> looper = dependencies.get(readReg);
			for(Instruction myDep : looper)
			{
				//If the instruction is done or the instruction came before the dependency
				if(myDep.isFin())
				{
					//dependencies.get(readReg).remove(j);
					//j++;
					continue;
				}
				
				else if(i.instNum <= myDep.instNum)
				{
					//j++;
					continue;
				}
			
				//Else check if the instructions data is ready
				else if(myDep.isDataReady())
				{
					//Should work but questionable
					i.recieveForwardData(readReg, myDep.getData());
					return true;
				}
			
				//Else we need to stall
				return false;
			}
			//All dependencies are not relevant, so we are good
			return true;
		}
		//No dependency
		return true;
	}
}
