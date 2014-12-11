package pippin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

public class Assembler {
	public static Set<String> noArgument = new TreeSet<String>();
	public static Map<String, Integer> opcode = new TreeMap<>();
	public static Map<Integer, String> mnemonics = new TreeMap<>();

	static {
		noArgument.add("HALT");
		noArgument.add("NOP");
		noArgument.add("NOT");

		opcode.put("LOD", 0x1);
		opcode.put("STO", 0x2);
		opcode.put("JUMP", 0xB);
		opcode.put("JMPZ", 0xC);
		opcode.put("NOP", 0x0);
		opcode.put("HALT", 0xF);
		opcode.put("ADD", 0x3);
		opcode.put("SUB", 0x4);
		opcode.put("MUL", 0x5);
		opcode.put("DIV", 0x6);
		opcode.put("AND", 0x7);
		opcode.put("NOT", 0x8);
		opcode.put("CMPZ", 0x9);
		opcode.put("CMPL", 0xA);
		opcode.put("LODI", 0x101);
		opcode.put("ADDI", 0x103);
		opcode.put("SUBI", 0x104);
		opcode.put("MULI", 0x105);
		opcode.put("DIVI", 0x106);
		opcode.put("ANDI", 0x107);
		opcode.put("LODN", 0x201);
		opcode.put("STON", 0x202);
		opcode.put("JUMPN", 0x20B);
		opcode.put("JMPZN", 0x20C);
		opcode.put("ADDN", 0x203);
		opcode.put("SUBN", 0x204);
		opcode.put("MULN", 0x205);
		opcode.put("DIVN", 0x206);

		for(String str : opcode.keySet()) 
		{
			mnemonics.put(opcode.get(str), str);
		}
	}

	public static boolean upperCaseCheck(String string){
		for(int i = 0; i < string.length(); i++)
			if(Character.isLowerCase(string.charAt(i)))
				return false;
		return true;

	}

	public static boolean assemble(File input, File output) 
	{
		boolean goodProgram = false; // will be used at end of method
		try {
			goodProgram = true;
			Scanner inp = new Scanner(input);
			PrintWriter outp = new PrintWriter(output);
			boolean blankLineHit = false; //keep track of when we hit a blank line
			boolean inCode = true; //keep track that we are in the code, not in data
			int lineCounter = 0;
			while(inp.hasNextLine() && goodProgram) 
			{
				if(!blankLineHit)
					lineCounter++;
				String line = inp.nextLine();
				if(line.trim().length() == 0)
					blankLineHit = true;
				else{
					if(blankLineHit && line.trim().length()>0){
						//ERROR BLOCK
						goodProgram = false;
						JOptionPane.showMessageDialog(null,
								"Blank line in the source file on line " + lineCounter,
								"Source Error", JOptionPane.WARNING_MESSAGE);
					}
					if(goodProgram && Character.isWhitespace(line.charAt(0))){
						//ERROR BLOCK
						goodProgram = false;
						JOptionPane.showMessageDialog(null,
								"Blank character at the start of line " + lineCounter,
								"Source Error", JOptionPane.WARNING_MESSAGE);
					}

					if(goodProgram && line.trim().equals("DATA")) {
						inCode = false;
						outp.println(-1);
					} else if(goodProgram){
						String[] parts = line.trim().split("\\s+");
						if(parts.length > 2)
						{
							//ERROR BLOCK
							goodProgram = false;
							JOptionPane.showMessageDialog(null,
									"There are too many items on line " + lineCounter,
									"Source Error", JOptionPane.WARNING_MESSAGE);
						}
						if(goodProgram && inCode)
						{
							if(!upperCaseCheck(parts[0]))
							{
								//ERROR BLOCK
								goodProgram = false;
								JOptionPane.showMessageDialog(null,
										"Mnemonic is not in upper case on line " + lineCounter,
										"Source Error", JOptionPane.WARNING_MESSAGE);
							}
							if(goodProgram && parts.length == 1)
							{
								if(!noArgument.contains(parts[0]))
								{
									//ERROR BLOCK
									goodProgram = false;
									JOptionPane.showMessageDialog(null,
											"Illegal mnemonic or missing argument on line " + lineCounter,
											"Source Error", JOptionPane.WARNING_MESSAGE);
								}else{
									outp.println(Integer.toHexString(opcode.get(parts[0])));
									outp.println(0);
								}	
							}else if(goodProgram)
							{
								if(noArgument.contains(parts[0]))
								{
									//ERROR BLOCK
									goodProgram = false;
									JOptionPane.showMessageDialog(null,
											"Illegal argument on line " + lineCounter,
											"Source Error", JOptionPane.WARNING_MESSAGE);
								}
								if(goodProgram && !(opcode.keySet().contains(parts[0])))
								{
									//ERROR BLOCK
									goodProgram = false;
									JOptionPane.showMessageDialog(null,
											"Illegal mnemonic on line " + lineCounter,
											"Source Error", JOptionPane.WARNING_MESSAGE);
								}
								if(goodProgram)
								{
									try{
										Integer.parseInt(parts[1], 16);
										outp.println(Integer.toHexString(opcode.get(parts[0])));
			                            outp.println(parts[1]);   
									}catch(NumberFormatException e){
										//ERROR BLOCK
										goodProgram = false;
										JOptionPane.showMessageDialog(null,
												"Argument is not an int on line " + lineCounter,
												"Source Error", JOptionPane.WARNING_MESSAGE);
									}
								}
							}
						}else if(goodProgram){
							if(parts.length != 2)
							{
								//ERROR BLOCK
								goodProgram = false;
								JOptionPane.showMessageDialog(null,
										"There is no address/value pair on line " + lineCounter,
										"Source Error", JOptionPane.WARNING_MESSAGE);
							}else{
								try{
									int addr = Integer.parseInt(parts[0], 16);
									if(addr < 0)
									{
										//ERROR BLOCK
										goodProgram = false;
										JOptionPane.showMessageDialog(null,
												"Memory address cannot be negative on line " + lineCounter,
												"Source Error", JOptionPane.WARNING_MESSAGE);
									}
								}catch(NumberFormatException e){
									//ERROR BLOCK
									goodProgram = false;
									JOptionPane.showMessageDialog(null,
											"Memory address is not an int on line " + lineCounter,
											"Source Error", JOptionPane.WARNING_MESSAGE);
								}
							}
							if(goodProgram)
							{
								try{
									Integer.parseInt(parts[1], 16);
									outp.println(parts[0]);
									outp.println(parts[1]);                                                     
								}catch(NumberFormatException e){
									//ERROR BLOCK
									goodProgram = false;
									JOptionPane.showMessageDialog(null,
											"Memory value is not an int on line " + lineCounter,
											"Source Error", JOptionPane.WARNING_MESSAGE);
								}
							}
						}
					}
				}
			}
			inp.close();
			outp.close();
		} catch (IOException e){
			System.out.println("Unable to open the necessary files");
		}
		if(!goodProgram && output != null && output.exists()) {
			output.delete();
		}
		return goodProgram;
	}

	public static void main(String[] args) 
	{
		String name;
		Scanner keyboard = new Scanner(System.in);

		System.out.println("Please input a file name:");
		name = keyboard.nextLine();

		assemble(new File(name + ".pasm"), new File(name + ".pexe"));
	}
}
