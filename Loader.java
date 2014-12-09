package pippin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Loader {
	public static void load(Memory mem, Code code, File file) throws FileNotFoundException {
		Scanner in = new Scanner(file);
		boolean inCode = true;
		
		//This whole try block is still not trusted
		try{
			while(in.hasNextInt(16)) {
				int inp = in.nextInt(16);
				if(inp == -1)
					inCode = false;
				else if(inCode) {
					int arg = in.nextInt(16);
					code.setCode(inp, arg);
				}//end IF in code section
				else { //Must be in data section
					int val = in.nextInt(16);
					mem.setData(inp, val);
				}//end ELSE in data section
			}//end WHILE input file
		}//end TRY block
		catch (ArrayIndexOutOfBoundsException e) {
        JOptionPane.showMessageDialog(null, 
                e.getMessage(), 
                "Failure loading data", JOptionPane.WARNING_MESSAGE);
		}//end CATCH block
		in.close();
	}
}  
