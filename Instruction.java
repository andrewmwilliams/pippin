package pippin;
public interface Instruction {
    /** 
     * Method to execute this instruction for the given argument. The details 
     * are explained in list of instructions for the Pippin computer. 
     * NOTE: If the instruction does not use an argument, then the argument 
     * is passed as 0 
     * @param arg the argument passed to the instruction
     * @param immediate indicates if the addressing mode is immediate
     * @param indirect indicates if the addressing mode is indirect
     */
    void execute(int arg, boolean immediate, boolean indirect);
}