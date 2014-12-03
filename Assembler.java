package pippin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
// fill in the remaining opcodes for all the missing instructions--the numeric hex values come from the handout.
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
// fill in similar opcodes for the remaining instructions that allow immediate addressing
// --"I" is added to the mnemonic and 0x100 to the opcode value
        opcode.put("MULI", 0x105);
        opcode.put("DIVI", 0x106);
        opcode.put("ANDI", 0x107);
        
        
        opcode.put("LODN", 0x201);
        opcode.put("STON", 0x202);
        opcode.put("JUMPN", 0x20B);
// fill in similar opcodes for the remaining instructions that allow indirect addressing
// --"N" is added to the mnemonic and 0x200 to the opcode value
        opcode.put("JMPZN", 0x20C);
        opcode.put("ADDN", 0x203);
        opcode.put("SUBN", 0x204);
        opcode.put("MULN", 0x205);
        opcode.put("DIVN", 0x206);
        
        
        for(String str : opcode.keySet()) {
            mnemonics.put(opcode.get(str), str);
        }
    }

    public static boolean assemble(File input, File output) {
        boolean goodProgram = false; // will be used at end of method
        try {
            goodProgram = true;
            Scanner inp = new Scanner(input);
            PrintWriter outp = new PrintWriter(output);
            boolean blankLineHit = false; //keep track of when we hit a blank line
            boolean inCode = true; //keep track that we are in the code, not in data
            int lineCounter = 0;
            while(inp.hasNextLine() && goodProgram) {
                lineCounter++;
                String line = inp.nextLine().trim();
                if (line.equals("DATA")) {
                    inCode = false;
                    outp.println(-1);
                } else {
                    String[] parts = line.trim().split("\\s+");
                    if(inCode) {
                        if(parts.length == 1) {
                            outp.println(Integer.toHexString(opcode.get(parts[0])));
                            outp.println(0);
                        } else {
                            outp.println(Integer.toHexString(opcode.get(parts[0])));
                            outp.println(parts[1]);
                        }
                    } else {
                        outp.println(parts[0]);
                        outp.println(parts[1]);                                         
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
    public static void main(String[] args) {
        // print a message to ask for a file name, such as factorial8, merge, qsort
        // use a Scanner to read the name
    	Scanner in = new Scanner(System.in);
    	
    	System.out.println("Please enter file name: ");
    	String name = in.nextLine();
    	
        assemble(new File(name + ".pasm"), new File(name + ".pexe"));
    }
}