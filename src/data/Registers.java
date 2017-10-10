package data;

public final class Registers {
	
	private static int[] registers = new int[32];

	public static int deref(int index) {
		return registers[index];
	}

	public static void writeRegister(Integer index, Integer data) {
		
		if(index == null)
		{
			//do nothing
		}
		else if(data == null)
		{
			System.out.println("ERROR: There is a write register specified"
					+ " but no data to write");
		}
		else
		{
			registers[index] = data;
		}
	}
}
