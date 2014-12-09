package pippin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Machine extends Observable {
    public final Map<Integer, Instruction> INSTRUCTION_MAP = new TreeMap<>();
    private Memory memory = new Memory();
    private Processor cpu = new Processor();
    private Code code = new Code();
    private boolean running = false;
    private boolean autoStepOn = false;
    private File currentlyExecutingFile = null;
    private States state;
    private CodeViewPanel codeViewPanel;
    private MemoryViewPanel memoryViewPanel1;
    private MemoryViewPanel memoryViewPanel2;
    private MemoryViewPanel memoryViewPanel3;
    private ControlPanel controlPanel;
    private ProcessorViewPanel processorPanel;
    private JFrame frame;
    private MenuBarBuilder menuBuilder;
    private String sourceDir; 
    private String executableDir;
    private String eclipseDir;
    private Properties properties = null; // this is the Properties from java.util
    
// ADD DELEGATE METHODS FOR int setData, int getData, and int[] getData from memory
// all the setters and getters of cpu, and the incrementCounter
// Also add the delegate of "get" from the INSTRUCTION_MAP

// Here are two lambda expressions for instructions
    public Machine() {
    	//ADD
        INSTRUCTION_MAP.put(0x3,(int arg, boolean immediate, boolean indirect) -> {
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
        INSTRUCTION_MAP.put(0x9,(int arg, boolean immediate, boolean indirect) -> {
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
        INSTRUCTION_MAP.put(0x1, (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		cpu.setAccumulator(arg);
        	else if(indirect)
        		cpu.setAccumulator(memory.getData(memory.getData(arg)));
        	else
        		cpu.setAccumulator(memory.getData(arg));
        	
        	cpu.incrementCounter();
        });
        //STO
        INSTRUCTION_MAP.put(0x2, (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immediate STO");
        	else if(indirect)
        		memory.setData(memory.getData(arg), cpu.getAccumulator());
           	else
           		memory.setData(arg, cpu.getAccumulator());
        		
        	cpu.incrementCounter();
        });
        //JUMP
        INSTRUCTION_MAP.put(0xB, (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immediate JUMP");
        	else if(indirect)
        		cpu.setProgramCounter(memory.getData(arg));
        	else
        		cpu.setProgramCounter(arg);
        });
        //JMPZ
        INSTRUCTION_MAP.put(0xC,  (int arg, boolean immediate, boolean indirect) -> {
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
        INSTRUCTION_MAP.put(0x0, (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immeduate NOP");
        	else if(indirect)
        		throw new IllegalInstructionModeException("attempt to execute indirect NOP");
        	else
        		cpu.incrementCounter();
        });
        //HALT
        INSTRUCTION_MAP.put(0xF, (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		throw new IllegalInstructionModeException("attempt to execute immediate HALT");
        	else if(indirect)
        		throw new IllegalInstructionModeException("attempt to execute indirect HALT");
        	else
        		halt();
        });
        //SUB
        INSTRUCTION_MAP.put(0x4, (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		cpu.setAccumulator(cpu.getAccumulator() - arg);
        	else if(indirect)
        		cpu.setAccumulator(cpu.getAccumulator() - memory.getData(memory.getData(arg)));
        	else
        		cpu.setAccumulator(cpu.getAccumulator() - memory.getData(arg));
        	
        	cpu.incrementCounter();
        });
        //MUL
        INSTRUCTION_MAP.put(0x5, (int arg, boolean immediate, boolean indirect) -> {
        	if(immediate)
        		cpu.setAccumulator(cpu.getAccumulator() * arg);
        	else if(indirect)
        		cpu.setAccumulator(cpu.getAccumulator() * memory.getData(memory.getData(arg)));
        	else
        		cpu.setAccumulator(cpu.getAccumulator() * memory.getData(arg));
        	
        	cpu.incrementCounter();
        });
        //DIV
        INSTRUCTION_MAP.put(0x6, (int arg, boolean immediate, boolean indirect) -> {
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
        INSTRUCTION_MAP.put(0x7,  (int arg, boolean immediate, boolean indirect) -> {
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
        INSTRUCTION_MAP.put(0x8, (int arg, boolean immediate, boolean indirect) -> {
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
        INSTRUCTION_MAP.put(0xA,  (int arg, boolean immediate, boolean indirect) -> {
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
        
        //CODE TO DISCOVER THE ECLIPSE DEFAULT DIRECTORY:
        File temp = new File("propertyfile.txt");
        if(!temp.exists()) {
            PrintWriter out;
            try {
                out = new PrintWriter(temp);
                out.close();
                eclipseDir = temp.getAbsolutePath();
                temp.delete();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            eclipseDir = temp.getAbsolutePath();
        }
        // change to forward slashes
        eclipseDir = eclipseDir.replace('\\','/');
        int lastSlash = eclipseDir.lastIndexOf('/');
        eclipseDir  = eclipseDir.substring(0, lastSlash + 1);
        //System.out.println(eclipseDir);           
        try { // load properties file "propertyfile.txt", if it exists
            properties = new Properties();
            properties.load(new FileInputStream("propertyfile.txt"));
            sourceDir = properties.getProperty("SourceDirectory");
            executableDir = properties.getProperty("ExecutableDirectory");
            // CLEAN UP ANY ERRORS IN WHAT IS STORED:
            if (sourceDir == null || sourceDir.length() == 0 
                    || !new File(sourceDir).exists()) {
                sourceDir = eclipseDir;
            }
            if (executableDir == null || executableDir.length() == 0 
                    || !new File(executableDir).exists()) {
                executableDir = eclipseDir;
            }
        } catch (Exception e) {
            // PROPERTIES FILE DID NOT EXIST
            sourceDir = eclipseDir;
            executableDir = eclipseDir;
        }
        
        createAndShowGUI();
    }
    
    public void halt() {
    	setAutoStepOn(false);
    	setRunning(false);
    }

    public void step() {
		try{
			int idx = getProgramCounter();
			int opCode = code.getOpcode(idx);
			boolean imm = code.getImmediate(idx);
			boolean ind = code.getIndirect(idx);
			int arg = code.getArg(idx);
			INSTRUCTION_MAP.get(opCode).execute(arg, imm, ind);
		} catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(
                    frame, 
                    "Error in accessing data memory\n" +
                            "Cannot execute the program",
                            "Warning",
                            JOptionPane.OK_OPTION);
            halt();
		} catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(
                    frame, 
                    "Error in accessing code of the program.\n" +
                            "Cannot execute the program",
                            "Warning",
                            JOptionPane.OK_OPTION);
            halt();
		} catch (NullPointerException e) {
            JOptionPane.showMessageDialog(
                    frame, 
                    "Error:  There was a null pointer.  This indicates a problem in the program.\n" +
                            "Cannot load the program",
                            "Warning",
                            JOptionPane.OK_OPTION);
            halt();
		}
    	
    	setChanged();
    	notifyObservers();
    }

    public void clearAll() {
    	memory.clear();
    	code.clear();
    	cpu.setAccumulator(0);
    	cpu.setProgramCounter(0);
    	state = States.NOTHING_LOADED;
    	state.enter();
    	setChanged();
    	notifyObservers("Clear");
    }
    
    public void reload() {
    	clearAll();
    	finalLoad_ReloadStep();
    }
    
    public void toggleAutoStep() {
    	if(autoStepOn)
    		setAutoStepOn(false);
    	else
    		setAutoStepOn(true);
    }
    
    public void setAutoStepOn(boolean b) {
    	autoStepOn = b;
    	if(autoStepOn)
    		state = States.AUTO_STEPPING;
    	else
    		state = States.PROGRAM_LOADED_NOT_AUTOSTEPPING;
    	
    	state.enter();
    	setChanged();
    	notifyObservers();
    }
    
    public  Map<Integer, Instruction> getInstructionMap() {
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
    
	public Instruction get(int key) {
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
	
	public States getState() {
		return state;
	}
	
	private void createAndShowGUI() {
		menuBuilder = new MenuBarBuilder(this);
        codeViewPanel = new CodeViewPanel(this);
        memoryViewPanel1 = new MemoryViewPanel(this, 0, 160);
        memoryViewPanel2 = new MemoryViewPanel(this, 160, 240);
        memoryViewPanel3 = new MemoryViewPanel(this, 240, Memory.DATA_SIZE);
        controlPanel = new ControlPanel(this);
        processorPanel = new ProcessorViewPanel(this);
        frame = new JFrame("Pippin Simulator");
        JPanel center = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout(1,1));
        content.setBackground(Color.BLACK);
        frame.setSize(1200,600);
        center.setLayout(new GridLayout(1,3));
        frame.add(codeViewPanel.createCodeDisplay(),BorderLayout.LINE_START);
        frame.add(center,BorderLayout.CENTER);
        center.add(memoryViewPanel1.createMemoryDisplay());
        center.add(memoryViewPanel2.createMemoryDisplay());
        center.add(memoryViewPanel3.createMemoryDisplay());
        frame.add(controlPanel.createControlDisplay(),BorderLayout.PAGE_END);
        frame.add(processorPanel.createProcessorDisplay(),BorderLayout.PAGE_START);
        JMenuBar bar = new JMenuBar();
        frame.setJMenuBar(bar);
        bar.add(menuBuilder.createFileMenu());
        bar.add(menuBuilder.createExecuteMenu());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new ExitAdapter());
        
        //Set initial state of machine
        state = States.NOTHING_LOADED;
        state.enter();
        setChanged();
        notifyObservers();
        
        //****ADD TIMER HERE
        javax.swing.Timer timer = new javax.swing.Timer(TICK, new TimerListener());
        timer.start();
        frame.setVisible(true);
	}
	
    public void exit() { // method executed when user exits the program
        int decision = JOptionPane.showConfirmDialog(
                frame, 
                "Do you really wish to exit?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (decision == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Translate method reads a source "pasm" file and saves the
     * file with the extension "pexe" 
     * 
     */
    public void assembleFile() {
        File source = null;
        File outputExe = null;
        JFileChooser chooser = new JFileChooser(sourceDir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Pippin Source Files", "pasm");
        chooser.setFileFilter(filter);
        // CODE TO LOAD DESIRED FILE
        int openOK = chooser.showOpenDialog(null);
        if(openOK == JFileChooser.APPROVE_OPTION) {
            source = chooser.getSelectedFile();
        }
        if(source != null && source.exists()) {
            // CODE TO REMEMBER WHICH DIRECTORY HAS THE pexe FILES
            // WHICH WE WILL ALLOW TO BE DIFFERENT
            sourceDir = source.getAbsolutePath();
            sourceDir = sourceDir.replace('\\','/');
            int lastDot = sourceDir.lastIndexOf('.');
            String outName = sourceDir.substring(0, lastDot + 1) + "pexe";          
            int lastSlash = sourceDir.lastIndexOf('/');
            sourceDir = sourceDir.substring(0, lastSlash + 1);
            outName = outName.substring(lastSlash+1); 
            filter = new FileNameExtensionFilter(
                    "Pippin Executable Files", "pexe");
            if(executableDir.equals(eclipseDir)) {
                chooser = new JFileChooser(sourceDir);
            } else {
                chooser = new JFileChooser(executableDir);
            }
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outName));
            int saveOK = chooser.showSaveDialog(null);
            if(saveOK == JFileChooser.APPROVE_OPTION) {
                outputExe = chooser.getSelectedFile();
            }
            if(outputExe != null) {
                executableDir = outputExe.getAbsolutePath();
                executableDir = executableDir.replace('\\','/');
                lastSlash = executableDir.lastIndexOf('/');
                executableDir = executableDir.substring(0, lastSlash + 1);
                try { 
                    properties.setProperty("SourceDirectory", sourceDir);
                    properties.setProperty("ExecutableDirectory", executableDir);
                    properties.store(new FileOutputStream("propertyfile.txt"), 
                            "File locations");
                } catch (Exception e) {
                    System.out.println("Error writing properties file");
                }
                if(Assembler.assemble(source, outputExe)){
                    JOptionPane.showMessageDialog(
                        frame, 
                        "The source was assembled to an executable",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }               
            } else {// outputExe Still null
                JOptionPane.showMessageDialog(
                        frame, 
                        "The output file has problems.\n" +
                                "Cannot assemble the program",
                                "Warning",
                                JOptionPane.OK_OPTION);
            }
        } else {// outputExe does not exist
            JOptionPane.showMessageDialog(
                    frame, 
                    "The source file has problems.\n" +
                            "Cannot assemble the program",
                            "Warning",
                            JOptionPane.OK_OPTION);             
        }
    }
    
    public void setRunning(boolean b) {
    	running = b;
    	if(running)
    		state = States.PROGRAM_LOADED_NOT_AUTOSTEPPING;
    	else {
    		autoStepOn = false;
    		state = States.PROGRAM_HALTED;
    	}
    	state.enter();
    	setChanged();
    	notifyObservers();
    }
    
    private void finalLoad_ReloadStep() {
        try {
            clearAll();
            Loader.load(memory, code, currentlyExecutingFile);
            setRunning(true);
            setAutoStepOn(false);
            setChanged();
            notifyObservers("Load Code");                       
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    frame, 
                    "The file being selected has problems.\n" +
                            "Cannot load the program",
                            "Warning",
                            JOptionPane.OK_OPTION);
        }       
    }
    
    public void loadFile() {
        JFileChooser chooser = new JFileChooser(executableDir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Pippin Executable Files", "pexe");
        chooser.setFileFilter(filter);
        // CODE TO LOAD DESIRED FILE
        int openOK = chooser.showOpenDialog(null);
        if(openOK == JFileChooser.APPROVE_OPTION) {
            currentlyExecutingFile = chooser.getSelectedFile();
        }
        if(currentlyExecutingFile != null && currentlyExecutingFile.exists()) {
            // CODE TO REMEMBER WHICH DIRECTORY HAS THE pexe FILES
            executableDir = currentlyExecutingFile .getAbsolutePath();
            executableDir = executableDir.replace('\\','/');
            int lastSlash = executableDir.lastIndexOf('/');
            executableDir = executableDir.substring(0, lastSlash + 1);
            try { 
                properties.setProperty("SourceDirectory", sourceDir);
                properties.setProperty("ExecutableDirectory", executableDir);
                properties.store(new FileOutputStream("propertyfile.txt"), 
                        "File locations");
            } catch (Exception e) {
                System.out.println("Error writing properties file");
            }           
        }
        finalLoad_ReloadStep();
    }
    
    public void execute() {
    	while(running) {
    		try{
    			int idx = getProgramCounter();
    			int opCode = code.getOpcode(idx);
    			boolean imm = code.getImmediate(idx);
    			boolean ind = code.getIndirect(idx);
    			int arg = code.getArg(idx);
    			INSTRUCTION_MAP.get(opCode).execute(arg, imm, ind);
    		} catch (ArrayIndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(
                        frame, 
                        "Error in accessing data memory\n" +
                                "Cannot execute the program",
                                "Warning",
                                JOptionPane.OK_OPTION);
                halt();
    		} catch (IndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(
                        frame, 
                        "Error in accessing code of the program.\n" +
                                "Cannot execute the program",
                                "Warning",
                                JOptionPane.OK_OPTION);
                halt();
    		} catch (NullPointerException e) {
                JOptionPane.showMessageDialog(
                        frame, 
                        "Error:  There was a null pointer.  This indicates a problem in the program.\n" +
                                "Cannot load the program",
                                "Warning",
                                JOptionPane.OK_OPTION);
                halt();
    		}
    	}
    	
    	setChanged();
    	notifyObservers();
    }
	
    /**
     * Main method that drives the whole simulator
     * @param args command line arguments are not used
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Machine(); 
            }
        });
    }
    
    private class ExitAdapter extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent arg0) {
            exit();
        }
    }
    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(autoStepOn) {
                step();
            }
        }
    } 
    private static final int TICK = 500; // timer tick = 1/2 second
}