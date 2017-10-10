package data;
import java.util.ArrayList;
import java.util.Arrays;

public class Cache {
	
	int WORDSIZE = 32;
	int numBlocks;
	int numWords;
	int memStart;
	int memOffset;

	//Stores Word Blocks
	int[] cache;
	
	//Buffer
	ArrayList<Integer> buffer;
	public boolean popInProgress = false;
	
	public boolean justStored = false;
	
	int popCycle = 0;
	
	public Cache(int numBlocks, int numWords, int memStart, int memOffset)
	{
		this.numBlocks = numBlocks;
		this.numWords = numWords;
		this.memStart = memStart;
		this.memOffset = memOffset;
		cache = new int[numBlocks];
		Arrays.fill(cache, -1);
		buffer = new ArrayList<Integer>();
	}

	//Cache hit or miss
	public boolean isInCache(int address)
	{
		int wordBlock = toBlock((address - memStart) / memOffset);
		int cacheBlock = toCache(wordBlock);
	
		return (this.cache[cacheBlock] == wordBlock);
	}
	
	public void add(int address)
	{
		int wordBlock = toBlock((address - memStart) / memOffset);
		int cacheBlock = toCache(wordBlock);
		
		this.cache[cacheBlock] = wordBlock;
	}
	
	public void writeBuffer(int address)
	{
		int wordBlock = toBlock((address - memStart) / memOffset);
		
		if(!this.isInBuffer(wordBlock))
		{
			buffer.add(wordBlock);
		}
	}
	
	int toBlock(int address)
	{
		return (address / numWords);
	}
	
	int toCache(int block)
	{
		return block % numBlocks;
	}
	
	public boolean isInBuffer(int address)
	{
		int wordBlock = toBlock((address - memStart) / memOffset);
		
		for(int i: buffer)
		{
			if(i == wordBlock)
			{
				return true;
			}
		}
		return false;
	}
	
	//Take 3 Cycles to pop one word, does not interrupt itself
	public void popBuffer()
	{
		if(justStored)
		{
			justStored = false;
			return;
		}
		//Check if already popping
		if(popInProgress)
		{
			popCycle++;
			if(popCycle >= 3)
			{
				buffer.remove(0);
				popCycle = 0;
				popInProgress = false;
			}
		}
		
		//Otherwise get popping
		else if(buffer.size() > 0)
		{
			popInProgress = true;
			popCycle = 1;
		}
	}
}
