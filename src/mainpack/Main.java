package mainpack;

import java.io.IOException;
import java.util.ArrayList;

import instructions.Instruction;
import utilities.Parser;
import data.Cache;
import data.Memory;

public class Main {
	
	public static Cache ICache = new Cache(2, 8, 0, 1);
	public static Cache DCache = new Cache(4, 4, 256, 4);
	
	public static void main(String[] args) throws IOException, CloneNotSupportedException
	{
		ArrayList<Instruction> instList = Parser.parseInst(args[0]);
		Processor processor = new Processor(instList);
		Memory.initMemory(Parser.parseData(args[1]));
		processor.runInstructionSet(args[2]);
	}

}
