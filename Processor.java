package pippin;
public class Processor  {
    private int accumulator;
    private int programCounter;
//ADD GETTERS AND SETTERS FOR BOTH FIELDS
    
    public int getAccumulator() {return accumulator;}
    
    public int getProgramCounter() {return programCounter;}
    
    public void setAccumulator(int in) {this.accumulator = in;}
    
    public void setProgramCounter(int in) {this.programCounter = in;}
    
    public void incrementCounter() {
        programCounter++;
    }
}