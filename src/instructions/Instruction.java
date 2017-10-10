package instructions;
import stages.*;
import utilities.Parser;

import java.util.ArrayList;

import data.Dependencies;
import data.Registers;
import mainpack.Main;

public abstract class Instruction implements Cloneable{
	
	protected int numCycles;
	public int instNum;
	public boolean wasExecuted;
	public boolean justPreped;
	
	//Cache
	public boolean iCacheAcc = false;
	public boolean dCacheAcc = false;
	
	public boolean needsICaching = false;
	public boolean needsDCaching = false;
	
	public boolean iCaching = false;
	public boolean dCaching = false;
	
	public int iCycles = 24;
	public int dCycles = 12;
	
	//**THESE ARE DETERMINED BY OVERIDDEN PARSE METHOD**
	//All possible register fields - Defined by subclass parse
	//For addressing only
	public int instAddress;
	public String toString;

	protected Integer read1 = null;
	protected Integer read2 = null;
	protected Integer write = null;

	protected Integer immediate = null;
	protected String label = null;
	protected String toLabel = null;	//What label to go to for branch
	//**END PARSE METHOD RETURNS**
	
	//If we need to branch
	protected boolean branch = false;
	
	//Forwarded data from another instruction
	protected Integer fread1 = null;
	protected Integer fread2 = null;
	
	//Data available to other instructions
	protected Integer data = null;
	
	//Because everything is done in parallel, don't set this to true until the cycle after the 
	//Available data is set
	protected boolean dataReady = false;
	
	protected Integer offsetPlusAddress = null;
	
	//Required in constructor. Overwrite what each instruction will do at any stage
	protected StageMethods stageMethods;
	private StageEnum curStage;
	
	public Instruction(StageMethods s, int numCycles)
	{
		this.stageMethods = s;
		this.numCycles = numCycles;
		this.curStage = StageEnum.BE;
		this.justPreped = true;
	}
	
	@Override 
	public Object clone() throws CloneNotSupportedException {
		    return (Instruction) super.clone();
	}
	 
	
	//Overwrite based on operation
	public abstract void parse(ArrayList<String> arrayList);
	
	//Execute the stage
	public boolean execute(StageEnum stage)
	{
		if(stageMethods.executeStage(stage, this))
		{
			return true;
		}
		return false;
	}
	
	public StageEnum nextStage()
	{
		return StageEnum.values()[(curStage.ordinal() + 1)];
	}
	
	//Arithmetic Operations most common, so basic parse and ex methods are defined here
	
	//Parse Arithmetic, R TYPE instruction
	public void arithRParse(ArrayList<String> params)
	{
		write = Parser.registerParse(params.get(0));
		read1 = Parser.registerParse(params.get(1));
		read2 = Parser.registerParse(params.get(2));
	}
	
	//Parse Arithmetic, I TYPE instruction
	public void arithIParse(ArrayList<String> params)
	{
		write = Parser.registerParse(params.get(0));
		read1 = Parser.registerParse(params.get(1));
		immediate = Integer.parseInt(params.get(2));
	}
	
	public void recieveForwardData(int key, int value)
	{
		if(read1 != null)
		{
			if(read1 == key)
			{
				fread1 = value;
			}
		}
		if(read2 != null)
		{
			if(read2 == key)
			{
				fread2 = value;
			}
		}
	}
	
	//If data is not null, say that it is now ready
	public void updateDataReady()
	{
		//If the data has been set
		if(!(this.data == null))
		{
			this.numCycles--;
			
			//Num cycles required for data to be ready
			if(numCycles <= 0)
			{
				this.dataReady = true;
			}
		}
	}
	
	public void updateCaching()
	{
		//If Caching Instruction
		if(this.iCaching)
		{
			iCycles--;
					
			if(iCycles <= 0)
			{
				Main.ICache.add(this.instAddress);
				iCaching = false;
				needsICaching = false;
			}
		}
				
		//If Caching Data
		if(this.dCaching)
		{
			dCycles--;
					
			if(dCycles <= 0)
			{
				Main.DCache.writeBuffer(this.getOffsetPlusAddress());
				Main.DCache.add(this.getOffsetPlusAddress());
				dCaching = false;
				needsDCaching = false;
			}
		}
	}
	
	public boolean isFin()
	{
		if(curStage == StageEnum.FIN)
		{
			return true;
		}
		return false;
	}
	
	//Handle dereference correctly based on if data was forwarded
	public int getValue1()
	{
		if(fread1 == null)
		{
			return Registers.deref(read1);
		}
		return fread1;
	}
	
	public int getValue2()
	{
		if(fread2 == null)
		{
			return Registers.deref(read2);
		}
		return fread2;
	}
	
	//STANDARD GETTER AND SETTER ORGY BELOW
	public Integer getOffsetPlusAddress() {
		return offsetPlusAddress;
	}

	public void setOffsetPlusAddress(Integer offsetPlusAddress) {
		this.offsetPlusAddress = offsetPlusAddress;
	}
	
	public int getRead1() {
		return read1;
	}

	public int getRead2() {
		return read2;
	}

	public boolean isDataReady() {
		return dataReady;
	}

	public void setDataReady(boolean dataReady) {
		this.dataReady = dataReady;
	}

	public void setRead1(int read1) {
		this.read1 = read1;
	}

	public void setRead2(int read2) {
		this.read2 = read2;
	}

	public Integer getWrite() {
		return write;
	}

	public void setWrite(Integer write) {
		this.write = write;
	}

	public Integer getData() {
		return data;
	}

	public void setData(Integer data) {
		this.data = data;
	}

	public Integer getImmediate() {
		return immediate;
	}

	public void setImmediate(Integer immediate) {
		this.immediate = immediate;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isBranch() {
		return branch;
	}

	public void setBranch(boolean branch) {
		this.branch = branch;
	}

	public StageMethods getStageMethods() {
		return stageMethods;
	}

	public void setStageMethods(StageMethods stageMethods) {
		this.stageMethods = stageMethods;
	}

	public StageEnum getCurStage() {
		return curStage;
	}

	public void setCurStage(StageEnum curStage) {
		this.curStage = curStage;
		
		//HEAVY CODE SMELL
		if(curStage == StageEnum.ID)
		{
			Dependencies.updateDependcies(this);
		}
	}
	
	public int getNumCycles() {
		return numCycles;
	}

	public void setNumCycles(int numCycles) {
		this.numCycles = numCycles;
	}
	public String getToLabel() {
		return toLabel;
	}

	public void setToLabel(String toLabel) {
		this.toLabel = toLabel;
	}
}