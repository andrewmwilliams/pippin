package pippin;

import java.util.ArrayList;

public class Code {
	public static final int CODE_MAX = 256;
	
	private ArrayList<IntPair> program = new ArrayList<>();
	
	public void setCode(int op, int arg) {
		program.add(new IntPair(op, arg));
	}
	
	public int getOpcode(int i) {
		return program.get(i).op % 0x100;
	}
	
	public int getArg(int i) {
		return program.get(i).arg;
	}
	
	public boolean getImmediate(int i) {
		if(program.get(i).op > 0x100 && program.get(i).op < 0x200)
			return true;
		
		return false;
	}
	
	public boolean getIndirect(int i) {
		if(program.get(i).op > 0x200)
			return true;
		
		return false;
	}
	
	public void clear() {
		program.clear();
	}
	
    public String getCodeText(int i) {
        if(i < program.size())
            return Assembler.mnemonics.get(program.get(i).op)
                    + " " + program.get(i).arg;
        else return "";
    }
    
    public String getCodeHex(int i) {
        if(i < program.size())
            return Integer.toHexString(program.get(i).op) + " " +
            Integer.toHexString(program.get(i).arg);
        else return "";
    }
	
	private class IntPair{
		private int op, arg;
		
		private IntPair(int op, int arg) {
			this.op = op;
			this.arg = arg;
		}
	}//end private class IntPair
}
