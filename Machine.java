package pippin;

import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

public class Machine extends Observable {
    public final Map<String, Instruction> INSTRUCTION_MAP = new TreeMap<>();
    private Memory memory = new Memory();
    private Processor cpu = new Processor();
    private Code code = new Code();

// ADD DELEGATE METHODS FOR int setData, int getData, and int[] getData from memory
// all the setters and getters of cpu, and the incrementCounter
// Also add the delegate of "get" from the INSTRUCTION_MAP

// Here are two lambda expressions for instructions
    public Machine() {
    	//ADD
        INSTRUCTION_MAP.put("0x3",(int arg, boolean immediate, boolean indirect) -> {
            if (immediate) {
                cpu.setAccumulator(cpu.getAccumulator() + arg);
            } else if (indirect) {
                int arg1 = memory.getData(arg);
                cpu.setAccumulator(cpu.getAccumulator() + memory.getData(arg1));                    
            } else {
                cpu.setAccumulator(cpu.getAccumulator() + memory.getData(arg));         
            }
            cpu.incrementCounter();
        });
        //CMPZ
        INSTRUCTION_MAP.put("0x9",(int arg, boolean immediate, boolean indirect) -> {
            int operand = memory.getData(arg);
            if (immediate) {
                throw new IllegalInstructionModeException("attempt to execute indirect AND");
            } else if (indirect) {
                throw new IllegalInstructionModeException("attempt to execute indirect AND");
            } 
            if(operand == 0) {
                cpu.setAccumulator(1);          
            } else {
                cpu.setAccumulator(0);          
            }
            cpu.incrementCounter();
        });
        //LOD
        INSTRUCTION_MAP.put("0x1", (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		cpu.setAccumulator(arg);
        	else if(indirect)
        		cpu.setAccumulator(memory.getData(memory.getData(arg)));
        	else
        		cpu.setAccumulator(memory.getData(arg));
        	
        	cpu.incrementCounter();
        });
        //STO
        INSTRUCTION_MAP.put("0x2", (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immediate STO");
        	else if(indirect)
        		memory.setData(memory.getData(arg), cpu.getAccumulator());
           	else
           		memory.setData(arg, cpu.getAccumulator());
        		
        	cpu.incrementCounter();
        });
        //JUMP
        INSTRUCTION_MAP.put("0xB", (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immediate JUMP");
        	else if(indirect)
        		cpu.setProgramCounter(memory.getData(arg));
        	else
        		cpu.setProgramCounter(arg);
        });
        //JMPZ
        INSTRUCTION_MAP.put("0xC",  (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immediate JMPZ");
        	else if(indirect) {
        		if(cpu.getAccumulator() == 0)
        			cpu.setProgramCounter(memory.getData(arg));
        		else
        			cpu.incrementCounter();
        	}
        	else {
        		if(cpu.getAccumulator() == 0)
        			cpu.setProgramCounter(arg);
        		else
        			cpu.incrementCounter();
        	}
        });
        //NOP
        INSTRUCTION_MAP.put("0x0", (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immeduate NOP");
        	else if(indirect)
        		throw new IllegalInstructionModeException("attempt to execute indirect NOP");
        	else
        		cpu.incrementCounter();
        });
        /*TO DO: FIX HALT!!*/
        //HALT
        INSTRUCTION_MAP.put("0xF", (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immediate HALT");
        	else if(indirect)
        		throw new IllegalInstructionModeException("attempt to execute indirect HALT");
      //  	else
      //  		halt();
        });
        //SUB
        INSTRUCTION_MAP.put("0x4", (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		cpu.setAccumulator(cpu.getAccumulator() - arg);
        	else if(indirect)
        		cpu.setAccumulator(cpu.getAccumulator() - memory.getData(memory.getData(arg)));
        	else
        		cpu.setAccumulator(cpu.getAccumulator() - memory.getData(arg));
        	
        	cpu.incrementCounter();
        });
        //MUL
        INSTRUCTION_MAP.put("0x5", (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		cpu.setAccumulator(cpu.getAccumulator() * arg);
        	else if(indirect)
        		cpu.setAccumulator(cpu.getAccumulator() * memory.getData(memory.getData(arg)));
        	else
        		cpu.setAccumulator(cpu.getAccumulator() * memory.getData(arg));
        	
        	cpu.incrementCounter();
        });
        //DIV
        INSTRUCTION_MAP.put("0x6", (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate) {
        		if(arg == 0)
        			throw new DivideByZeroException("attempt to divide by immediate zero");
        		cpu.setAccumulator(cpu.getAccumulator() / arg);
        	}
        	else if(indirect) {
        		if(memory.getData(memory.getData(arg)) == 0)
        			throw new DivideByZeroException("attempt to divide by indirect zero");
        		cpu.setAccumulator(cpu.getAccumulator() / memory.getData(memory.getData(arg)));
        	}
        	else {
        		if(memory.getData(arg) == 0)
        			throw new DivideByZeroException("attempt to divide by direct zero");
        		cpu.setAccumulator(cpu.getAccumulator() / memory.getData(arg));
        	}
        	cpu.incrementCounter();
        });
        //AND
        INSTRUCTION_MAP.put("0x7",  (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate) {
        		if(arg != 0 && cpu.getAccumulator() != 0)
        			cpu.setAccumulator(1);
        		else
        			cpu.setAccumulator(0);
        	}
        	else if(indirect)
        		throw new IllegalInstructionModeException("attempt to execute indirect AND");
        	else {
        		if(memory.getData(arg) != 0 && cpu.getAccumulator() != 0)
        			cpu.setAccumulator(1);
        		else
        			cpu.setAccumulator(0);
        	}
        	cpu.incrementCounter();
        });
        //NOT
        INSTRUCTION_MAP.put("0x8", (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immediate NOT");
        	else if (indirect)
        		throw new IllegalInstructionModeException("attempt to execute indirect NOT");
        	else {
        		if(cpu.getAccumulator() == 0)
        			cpu.setAccumulator(1);
        		else
        			cpu.setAccumulator(0);
        	}
        	cpu.incrementCounter();
        });
        //CMPL
        INSTRUCTION_MAP.put("0xA",  (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immediate CMPL");
        	else if(indirect)
        		throw new IllegalInstructionModeException("attempt to execute indirect CMPL");
        	else {
        		if(memory.getData(arg) < 0)
        			cpu.setAccumulator(1);
        		else
        			cpu.setAccumulator(0);
        	}
        	cpu.incrementCounter();
        });
    }

    public  Map<String, Instruction> getInstructionMap() {
    	return INSTRUCTION_MAP;
    }
    
    public int getAccumulator() {
		return cpu.getAccumulator();
	}

	public int getProgramCounter() {
		return cpu.getProgramCounter();
	}

	public void setAccumulator(int in) {
		cpu.setAccumulator(in);
	}

	public void setProgramCounter(int in) {
		cpu.setProgramCounter(in);
	}

	public int[] getData() {
    	return memory.getData();
    }
    
	public Instruction get(Object key) {
		return INSTRUCTION_MAP.get(key);
	}

	public void setData(int index, int value) {
		memory.setData(index, value);
	}

	public int getData(int index) {
		return memory.getData(index);
	}
	
	public Processor getCpu() {
		return cpu;
	}
	
	public Memory getMemory() {
		return memory;
	}
	
	public Code getCode() {
		return code;
	}
}