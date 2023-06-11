package ode;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private final Random RNG = new Random();
	private final File csvOutputFile = new File("output.csv");
	private Point[][] points;
	private int size = 10;
	public Type editType= Type.EMPTY;
	private int neighbourhood = 1;

	private static final double preyDensity = 0.1;
	private static final double predatorDensity = 0.1;

	public Board(int length, int height) {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
		initialize(length, height);
	}

	public void iteration() {
		System.out.print("Predators:");
		System.out.println(Point.predatorPoints.size());
		System.out.print("Prey:");
		System.out.println(Point.preyPoints.size());
		System.out.println();

		try (PrintWriter pw = new PrintWriter(new FileWriter(csvOutputFile, true))) {
			pw.println(Point.preyPoints.size() + ";" + Point.predatorPoints.size());
		} catch (IOException exception) {
			System.out.println(exception.getMessage());
		}

		int preyToBirth = (int) (Point.preyPoints.size() * Point.preyBirthCoefficient);



		populate(preyToBirth, Type.PREY);

		int predatorsToBirth = (int) (Point.totalCurrentEnergy / Point.predatorEnergyEfficiency);

		populate(predatorsToBirth, Type.PREDATOR);
		Point.totalCurrentEnergy = 0;

		for (int x = 1; x < points.length - 1; ++x)
			for (int y = 1; y < points[x].length - 1; ++y)
				points[x][y].move();

		for (int x = 1; x < points.length - 1; ++x)
			for (int y = 1; y < points[x].length - 1; ++y)
				points[x][y].progressType();

		for (int x = 1; x < points.length - 1; ++x)
			for (int y = 1; y < points[x].length - 1; ++y)
				points[x][y].hunt();

		for (int x = 1; x < points.length - 1; ++x)
			for (int y = 1; y < points[x].length - 1; ++y)
				points[x][y].progressType();

		this.repaint();
	}

	private void populate(int amount, Type type) {
		int attemptsLeft = 100;
		while (amount > 0 && attemptsLeft > 0) {
			attemptsLeft--;
			int newX = RNG.nextInt(points.length);
			int newY = RNG.nextInt(points[0].length);
			if (points[newX][newY].type != Type.EMPTY) continue;
			points[newX][newY].type = type;
			amount--;
		}
	}

	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].clear();
			}
		this.repaint();
	}

	private void initialize(int length, int height) {
		points = new Point[length][height];
		try {
			new PrintWriter(csvOutputFile).close();
		}
		catch (Exception ignored) {}

		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y] = new Point();
			}
		}
		Point.predatorPoints.clear();
		Point.preyPoints.clear();

		for (int x = 1; x < points.length-1; ++x) {
			for (int y = 1; y < points[x].length - 1; ++y) {
				points[x][y].addNeighbor(points[x][y - 1]);
				points[x][y].addNeighbor(points[x + 1][y]);
				points[x][y].addNeighbor(points[x][y + 1]);
				points[x][y].addNeighbor(points[x - 1][y]);
				if (neighbourhood == 1) {
					points[x][y].addNeighbor(points[x + 1][y - 1]);
					points[x][y].addNeighbor(points[x + 1][y + 1]);
					points[x][y].addNeighbor(points[x - 1][y + 1]);
					points[x][y].addNeighbor(points[x - 1][y - 1]);
				}

				boolean isPrey = RNG.nextFloat() < preyDensity;
				boolean isPredator = RNG.nextFloat() < predatorDensity;
				if (isPrey && isPredator) {
					if (RNG.nextBoolean()) points[x][y].setType(Type.PREY);
					else points[x][y].setType(Type.PREDATOR);
				} else if (isPrey) points[x][y].setType(Type.PREY);
				else if (isPredator) points[x][y].setType(Type.PREDATOR);
			}
		}
	}

	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.GRAY);
		drawNetting(g, size);
	}

	private void drawNetting(Graphics g, int gridSpace) {
		Insets insets = getInsets();
		int firstX = insets.left;
		int firstY = insets.top;
		int lastX = this.getWidth() - insets.right;
		int lastY = this.getHeight() - insets.bottom;

		int x = firstX;
		while (x < lastX) {
			g.drawLine(x, firstY, x, lastY);
			x += gridSpace;
		}

		int y = firstY;
		while (y < lastY) {
			g.drawLine(firstX, y, lastX, y);
			y += gridSpace;
		}

		for (x = 0; x < points.length; ++x) {
			for (y = 0; y < points[x].length; ++y) {
				g.setColor(switch (points[x][y].type) {
					case EMPTY -> new Color(1.0f, 1.0f, 1.0f, 0.7f);
					case PREDATOR -> new Color(1.0f, 0.0f, 0.0f, 0.7f);
					case PREY -> new Color(0.0f, 1.0f, 0.0f, 0.7f);
				});
				g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			points[x][y].setType(editType);
			this.repaint();
		}
	}

	public void componentResized(ComponentEvent e) {
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			points[x][y].setType(editType);
			this.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
