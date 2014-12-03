package pippin;

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProcessorViewPanel implements Observer {
	private Processor cpu = new Processor();
	private JTextField acc = new JTextField();
	private JTextField pc = new JTextField();
	
	public ProcessorViewPanel(Machine machine) {
		cpu = machine.getCpu();
		machine.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(cpu != null) {
			acc.setText("" + cpu.getAccumulator());
			pc.setText("" + cpu.getProgramCounter());
		}
	}

	public JComponent createProcessorDisplay() {
		JPanel returnPanel = new JPanel();
		returnPanel.setLayout(new GridLayout(1,0));
		returnPanel.add(new JLabel("Accumulator: ", JLabel.RIGHT));
		returnPanel.add(acc);
		returnPanel.add(new JLabel("Program Counter: ", JLabel.RIGHT));
		returnPanel.add(pc);
		
		return returnPanel;
	}
	
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ProcessorViewPanel cpuViewPanel = new ProcessorViewPanel(new Machine());
                JFrame frame = new JFrame("Code View Panel");
                frame.add(cpuViewPanel.createProcessorDisplay());
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
	
}
