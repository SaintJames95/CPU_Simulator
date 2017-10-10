package mainpack;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import instructions.*;
import stages.StageEnum;

public class Processor {
	
	private static ArrayList<Instruction> instList;
	private ArrayList<Instruction> queue;
	private String[][] results;
	private String[][] parseResults;
	private int instIndex;
	private int instCount;
	private int cycles;
	
	private static boolean halt = false;
	private static boolean imFinished = false;
	public static boolean noDCache = false;
	
	public static int ICacheReq = 0;
	public static int ICacheHit = 0;

	public static int DCacheReq = 0;
	public static int DCacheHit = 0;


	public static void halt()
	{
		Processor.halt = true;
	}
	
	public Processor(ArrayList<Instruction> i)
	{
		instList = i;
		queue = new ArrayList<Instruction>();
		instIndex = 0;
		cycles = 0;
		instCount = 0; 
		results = new String[100][300];
		parseResults = new String[100][6];
		fillResults();
	}
	
	public void runInstructionSet(String outfile) throws CloneNotSupportedException, FileNotFoundException
	{
		do
		{
			this.cycle();
		}while(!imFinished);
		
		PrintStream out = new PrintStream(new FileOutputStream("execTrace.txt"));
		System.setOut(out);
		
		printResults();
		
		PrintStream out2 = new PrintStream(new FileOutputStream(outfile));
		System.setOut(out2);
		
		parseResults();
		printParseResults();
	}
	
	private void cycle() throws CloneNotSupportedException
	{
		//Iterate through the queue
		ArrayList<Integer> executedInsts = iterQueue();
		
		//If empty or the newest instruction is not in the start stage, prepare another one
		prepareNextInstruction();
		
		//Update Caching and Forward Data
		updateCycleResults(executedInsts);
	}
	
	//Iterate through the instruction in queue. Return a list of insts that were succesfully executed
	private ArrayList<Integer> iterQueue()
	{
		cycles++;

		ArrayList<Integer> executedInsts = new ArrayList<Integer>();
		int counter = 0;
		for(Instruction inst : this.queue)
		{
			inst.wasExecuted = false;
			if(counter > 0)
			{
				//If the previous inst is in the stage we are trying to get to, need to stall
				if(inst.nextStage() == queue.get(counter - 1).getCurStage())
				{
					counter++;
					continue;
				}
			}
			
			//TRYING TO EXECUTE NEXT STAGE 
			if(inst.execute(inst.nextStage()))
			{
				inst.setCurStage(inst.nextStage());	
				executedInsts.add(counter);
				inst.wasExecuted = true;
			}
			counter++;
		}
		return executedInsts;
	}
	
	private void prepareNextInstruction() throws CloneNotSupportedException
	{
		if((queue.isEmpty() || !(queue.get(queue.size() - 1).getCurStage() == StageEnum.BE)) && !halt)
		{
			Instruction i = this.InstructionFetch(instIndex);	
			if(i != null)
			{
				this.instCount++;
				this.instIndex++;
			
				i.instNum = instCount;
				this.queue.add(i);
				
				parseResults[instCount][0] = i.toString;
			}
		}
		if(queue.isEmpty() && halt)
		{
			imFinished = true;
		}
	}
	
	private void updateCycleResults(ArrayList<Integer> executedInsts) throws CloneNotSupportedException
	{	
		boolean branchFound = this.handleDataAndBranching(executedInsts);
		
		//Popping finished instructions
		for(int i = 0; i < queue.size(); i++)
		{
			if(queue.get(i).isFin())
			{
				queue.remove(i);
				i--;
			}
		}
		
		//Print
		this.writeQueue(executedInsts);
		
		//Caching
		handleCaching();
		
		//Pop the bad instruction if there was a branch found
		if(branchFound)
		{
			//Will need to check if this instruction is caching the ICache here
			if(queue.size() > 1)
			{queue.remove(queue.size() - 1);}
			
			if(queue.size() > 1)
			{queue.remove(queue.size() - 1);}
			
			instCount--;
			prepareNextInstruction();
		}
	}
	
	//Return if branch was found
	public boolean handleDataAndBranching(ArrayList<Integer> executedInsts)
	{
		boolean branchFound = false;
		//Data forwarding and branching
		for(int i: executedInsts)
		{
			Instruction inst = queue.get(i);
			inst.updateDataReady();
					
			//Update Branch Determined
			if(inst.isBranch())
			{
				//Acknowledge Branch Found
				inst.setBranch(false);
				
				//Remove the bad instruction when we are finished
				if(!branchFound)
				{
					branchFound = true;
				}
				else
				{
					System.out.println("ERROR: FOUND TWO BRANCHES IN ONE CYCLE");
				}
				
				//Locate branch label in instruction set
				this.instIndex = branch(inst.getToLabel());
			}
		}
		return branchFound;
	}	
	
	public void handleCaching()
	{
		if(!memInUse())
		{
			int cacheMe = -1;
			
			//0 for i-cache, 1 for d-cache
			int cacheMeType = -1;
			
			int counter = 0;
			for(Instruction i: queue)
			{
				//If cache miss AND not in write buffer
				if(i.needsICaching)
				{
					cacheMe = counter;
					cacheMeType = 0;
					break;
				}
				
				else if(i.needsDCaching && !noDCache)
				{
					if(Main.DCache.isInBuffer(i.getOffsetPlusAddress()))
					{
						System.out.println("ERROR: CACHE MISS, BUT THE ADDRESS IS IN THE WRITE BUFFER");
						System.out.println("This program will try to handle it but is not tested");
						System.out.println("For this case");
					}
					else
					{
						cacheMe = counter;
						cacheMeType = 1;
					}
				}
				counter++;
			}
			
			if(cacheMe != -1)
			{
				if(cacheMeType == 0)
				{
					queue.get(cacheMe).iCaching = true;
				}
				else
				{
					queue.get(cacheMe).dCaching = true;
				}
				queue.get(cacheMe).updateCaching();
			}
			else
			{
				if(!noDCache)
				{Main.DCache.popBuffer();}
			}
		}
		//This is set true during store instructions
		noDCache = false;
	}
	
	//Checks for pop from the write buffer or instruction currently caching
	//Also handles cases where is in use
	public boolean memInUse()
	{
		if(Main.DCache.popInProgress)
		{
			Main.DCache.popBuffer();
			return true;
		}
		
		for(Instruction i: queue)
		{
			if(i.dCaching || i.iCaching)
			{
				i.updateCaching();
				return true;
			}
		}
		return false;
	}
	
	public void writeQueue(ArrayList<Integer> executedInsts)
	{
		for(Instruction i : queue)
		{
			if(i.wasExecuted)
			{
				results[i.instNum][cycles - 1] = i.getCurStage().toString() + " ";
			}
			else if(i.justPreped)
			{
				//i.justPreped = false;
				continue;
			}
			else
			{
				results[i.instNum][cycles - 1] = "ST ";
			}
		}
	}
	
	public void printResults()
	{
		for(int i = 0; i < this.results.length; i++)
		{
			for(int j = 0; j < this.results[i].length; j++)
			{
				System.out.print(this.results[i][j]);
			}
			System.out.println();
		}
	}
	
	public void printParseResults()
	{
		for(int i = 1; i < this.parseResults.length; i++)
		{
			if((this.parseResults[i][0]).equals(""))
			{continue;}
			for(int j = 0; j < this.parseResults[i].length; j++)
			{
				if(j == 0)
				{System.out.printf("%-35s", this.parseResults[i][j]);}
				else
				{
					System.out.printf("%-5s", this.parseResults[i][j]);
				}
			}
			System.out.println();
		}
		
		System.out.println();
		System.out.println("Total Number of Access Requests for the instruction cache: " + Processor.ICacheReq);
		System.out.println("Number of instruction cache hits: " + Processor.ICacheHit);
		
		System.out.println("Total Number of Access Requests for the data cache: " + Processor.DCacheReq);
		System.out.println("Number of data cache hits: " + Processor.DCacheHit);
	}
	
	//0th index preset
	public void parseResults()
	{
		for(int i = 1; i < this.results.length; i++)
		{
			int index = 1;
			for(int j = 1; j < this.results[i].length; j++)
			{
				//Continue if whitespace
				if(results[i][j].equals("   "))
				{
					continue;
				}
				
				//Continue if X1 or X2
				if(results[i][j].equals("X2 ") || results[i][j].equals("X1 "))
				{
					continue;
				}
				
				//Continue if stall
				if(results[i][j].equals("ST "))
				{
					continue;
				}
				
				//Default - lookahead
						//Last index case						//Good to go
				if((j < (results[i].length - 1)) && !results[i][j].equals(results[i][j+1]))
				{
					parseResults[i][index] = results[0][j];
					index++;
					if(index >= 6)
					{
						break;
					}
				}
			}
		}
		
		//Clean up branch special case
		for(int i = 1; i < this.parseResults.length; i++)
		{
			if(this.parseResults[i][0].contains("BNE") || this.parseResults[i][0].contains("BEQ"))
			{
				if(this.parseResults[i][3] != "")
				{
					this.parseResults[i][2] = this.parseResults[i][3];
					this.parseResults[i][3] = "";
				}
			}
		}
	}

	private void fillResults()
	{
		for(int i = 0; i < this.results.length; i++)
		{
			Arrays.fill(this.results[i], "   ");
			String str = String.valueOf(i);
			str = str + "   ";
			str = str.substring(0, 3);
			results[i][0] = str;
		}
		
		for(int i = 0; i < this.results[0].length; i++)
		{
			String str = String.valueOf(i);
			str = str + "   ";
			str = str.substring(0, 3);
			results[0][i] = str;
		}
		
		for(int i = 0; i < this.parseResults.length; i++)
		{
			Arrays.fill(this.parseResults[i], "");
		}
	}
	
	private Instruction InstructionFetch(int instIndex) throws CloneNotSupportedException
	{
		if(instIndex < 0 || instIndex >= instList.size())
		{
			//System.out.println("You tried to fetch a non-existent instruction");
			return null;
		}
		
		return (Instruction) instList.get(instIndex).clone();
	}
	
	public int branch(String label)
	{
		for(int i = 0; i < instList.size(); i++)
		{
			if(label.equals(instList.get(i).getLabel()))
			{
				return i;
			}
		}
		System.out.println("Label not found");
		return -1;
	}
}
