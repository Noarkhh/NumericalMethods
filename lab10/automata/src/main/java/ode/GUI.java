package ode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GUI extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private Timer timer;
	private Board board;
	private JButton start;
	private JButton clear;
	private JComboBox<Type> drawType;
	private JSlider pred;
	private JFrame frame;
	private int iterNum = 0;
	private final int maxDelay = 500;
	private final int initDelay = 100;
	private boolean running = false;

	public GUI(JFrame jf) {
		frame = jf;
		timer = new Timer(initDelay, this);
		timer.stop();
	}

	public void initialize(Container container) {
		container.setLayout(new BorderLayout());
		container.setSize(new Dimension(1024, 768));

		JPanel buttonPanel = new JPanel();

		start = new JButton("Start");
		start.setActionCommand("Start");
		start.addActionListener(this);

		clear = new JButton("Clear");
		clear.setActionCommand("clear");
		clear.addActionListener(this);
		
		pred = new JSlider();
		pred.setMinimum(0);
		pred.setMaximum(maxDelay);
		pred.addChangeListener(this);
		pred.setValue(maxDelay - timer.getDelay());
		
		drawType = new JComboBox<Type>(Point.types);
		drawType.addActionListener(this);
		drawType.setActionCommand("drawType");

		buttonPanel.add(start);
		buttonPanel.add(clear);
		buttonPanel.add(drawType);
		buttonPanel.add(pred);

		board = new Board(1024, 768 - buttonPanel.getHeight());
		container.add(board, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(timer)) {
			iterNum++;
			frame.setTitle("Species interaction simulation (" + Integer.toString(iterNum) + " iteration)");
			board.iteration();
		} else {
			String command = e.getActionCommand();
			if (command.equals("Start")) {
				if (!running) {
					timer.start();
					start.setText("Pause");
				} else {
					timer.stop();
					start.setText("Start");
				}
				running = !running;
				clear.setEnabled(true);

			} else if (command.equals("clear")) {
				iterNum = 0;
				timer.stop();
				start.setEnabled(true);
				board.clear();
			}
			else if (command.equals("drawType")){
				Type newType = (Type)drawType.getSelectedItem();
				board.editType = newType;
			}

		}
	}

	public void stateChanged(ChangeEvent e) {
		timer.setDelay(maxDelay - pred.getValue());
	}
}
