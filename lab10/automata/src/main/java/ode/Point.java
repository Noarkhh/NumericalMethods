package ode;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class Point {

	public static final double preyBirthCoefficient = 5;
	public static final double predatorEnergyEfficiency = 1.5;
	public static final double predatorHuntingEfficiency = 1;

	public static Type[] types = {Type.EMPTY, Type.PREDATOR, Type.PREY};
	public static Set<Point> predatorPoints = new HashSet<>();
	public static Set<Point> preyPoints = new HashSet<>();

	public static Random RNG = new Random();

	public ArrayList<Point> neighbours = new ArrayList<>();
	public Type type = Type.EMPTY;
	public Type nextType = null;

	public double energy = 10;
	public static int totalCurrentEnergy = 0;

	public Point() {
	}
	
	public void clear() {
		setType(Type.EMPTY);
	}

	public void move() {
		if (type == Type.EMPTY) return;
		Point destinationPoint = neighbours.get(RNG.nextInt(neighbours.size()));
		if (destinationPoint.nextType == Type.EMPTY || (destinationPoint.type == Type.EMPTY && destinationPoint.nextType == null)) {
			destinationPoint.nextType = type;
			if (type == Type.PREDATOR) {
				destinationPoint.energy = energy - 1;
			}
			nextType = Type.EMPTY;
		}
	}

	public void hunt() {
		if (type != Type.PREDATOR) return;
		for (Point neighbour : neighbours) {
			if (neighbour.type != Type.PREY) continue;
			if (RNG.nextFloat() > predatorHuntingEfficiency) continue;
			neighbour.nextType = Type.EMPTY;
			energy = predatorEnergyEfficiency;
			totalCurrentEnergy += predatorEnergyEfficiency;
		}
		if (energy <= 0) nextType = Type.EMPTY;
	}

	public void progressType() {
		if (nextType == null) return;
		setType(nextType);
		nextType = null;
	}

	public void addNeighbor(Point nei) {
		neighbours.add(nei);
	}

	public void setType(Type newType) {
		if (type == Type.PREY) preyPoints.remove(this);
		if (type == Type.PREDATOR) predatorPoints.remove(this);
		type = newType;
		if (type == Type.PREY) preyPoints.add(this);
		if (type == Type.PREDATOR) predatorPoints.add(this);
	}
	
}