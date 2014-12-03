package pippin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class CodeViewPanel implements Observer {
	private Code code = new Code();
	private Processor cpu = new Processor();
	private JScrollPane scroller = new JScrollPane();
	private JTextField[] codeText = new JTextField[code.CODE_MAX];
	private JTextField[] codeHex = new JTextField[code.CODE_MAX];
	
	public CodeViewPanel(Machine machine) {
		code = machine.getCode();
		cpu = machine.getCpu();
		machine.addObserver(this);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
	}
	
	public JPanel createCodeDisplay() {
		JPanel returnPanel = new JPanel();
		JPanel panel = new JPanel();
		JPanel numPanel = new JPanel();
		JPanel sourcePanel = new JPanel();
		JPanel hexPanel = new JPanel();
		
        returnPanel.setPreferredSize(new Dimension(300,150));;
        returnPanel.setLayout(new BorderLayout());
        panel.setLayout(new BorderLayout());
        numPanel.setLayout(new GridLayout(0,1));
        sourcePanel.setLayout(new GridLayout(0,1));
        hexPanel.setLayout(new GridLayout(0,1));
        
        for(int i = 0; i < Code.CODE_MAX; i++) {
        	numPanel.add(new JLabel(i + ": ", JLabel.RIGHT));
        	codeText[i] = new JTextField(10);
        	codeHex[i] = new JTextField(10);
        	sourcePanel.add(codeText[i]);
        	hexPanel.add(codeHex[i]);
        }
        
        Border border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), "Code Memory View",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        returnPanel.setBorder(border);
        
        panel.add(numPanel, BorderLayout.LINE_START);
        panel.add(sourcePanel, BorderLayout.CENTER);
        panel.add(hexPanel, BorderLayout.LINE_END);
        scroller = new JScrollPane(panel);
        returnPanel.add(scroller);
        
        return returnPanel;
	}
	
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CodeViewPanel codeViewPanel = new CodeViewPanel(new Machine());
                JFrame frame = new JFrame("Code View Panel");
                frame.add(codeViewPanel.createCodeDisplay());
                frame.setSize(300,600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

}
