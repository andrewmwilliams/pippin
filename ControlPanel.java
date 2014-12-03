package pippin;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ControlPanel implements Observer {
	private Machine machine;
	private JButton stepButton= new JButton("Step");
	private JButton clearButton = new JButton("Clear");
	private JButton runButton = new JButton("Run/Pause");
	private JButton reloadButton = new JButton("Reload");
	
	public ControlPanel(Machine machine) {
		this.machine = machine;
		machine.addObserver(this);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		stepButton.setEnabled(machine.getState().getStepActive());
		clearButton.setEnabled(machine.getState().getClearActive());
		runButton.setEnabled(machine.getState().getRunPauseActive());
		reloadButton.setEnabled(machine.getState().getReloadActive());
	}

	public JComponent createControlDisplay() {
		JPanel returnPanel = new JPanel();
		returnPanel.setLayout(new GridLayout(1,0));
		
		stepButton.setBackground(Color.WHITE);
		stepButton.addActionListener(e -> machine.step());
		returnPanel.add(stepButton);
		
		clearButton.setBackground(Color.WHITE);
		clearButton.addActionListener(e -> machine.clearAll());
		returnPanel.add(clearButton);
		
		runButton.setBackground(Color.WHITE);
		runButton.addActionListener(e -> machine.toggleAutoStep());
		returnPanel.add(runButton);
		
		reloadButton.setBackground(Color.WHITE);
		reloadButton.addActionListener(e -> machine.reload());
		returnPanel.add(reloadButton);
		
		return returnPanel;
	}
	
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ControlPanel controlPanel = new ControlPanel(new Machine());
                JFrame frame = new JFrame("Code View Panel");
                frame.add(controlPanel.createControlDisplay());
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
	
}
