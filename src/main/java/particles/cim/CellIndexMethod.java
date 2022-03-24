package particles.cim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import particles.Particle;

public class CellIndexMethod {
    private static boolean DEBUG = true;

    private static int[] cells;
    private static int[] particleRefs;
    private static int gridSize;
    private static Set<Integer>[] neighbours;

    private static Set<Integer> setsProvider() {
        return new HashSet<>();
    }

    public static Set<Integer>[] apply(double size, double criticalRadius, Particle[] particles) {
        double maxRadius = Arrays.asList(particles).stream().mapToDouble(p -> p.radius).max().getAsDouble();
        gridSize = (int) Math.ceil(size / (criticalRadius + maxRadius)) + 1; // ! TODO: check
        return apply(size, criticalRadius, particles, gridSize);
    }

    @SuppressWarnings("unchecked")
    public static Set<Integer>[] apply(double size, double criticalRadius, Particle[] particles, int grids) {
        gridSize = grids;

        neighbours = (Set<Integer>[]) (new HashSet[particles.length]);
        Arrays.setAll(neighbours, (i) -> setsProvider());

        if (DEBUG)
            System.out.println(String.format("CIM:\n\tSpace size: %.3f\n\tCells per side: %d\n", size, gridSize));

        cells = new int[gridSize * gridSize];
        Arrays.fill(cells, -1);
        particleRefs = new int[particles.length];
        Arrays.fill(particleRefs, -1);

        for (Particle p : particles) {
            int cell = getCell(p, size);
            int nextParticle = cells[cell];
            if (nextParticle == -1) {
                cells[cell] = p.getIndex();
            } else {
                while (particleRefs[nextParticle] != -1) {
                    nextParticle = particleRefs[nextParticle];
                }
                particleRefs[nextParticle] = p.getIndex();
            }
        }

        // Recorrer cada celda y medir las distancias con los vecinos
        for (int i = 0; i < cells.length; i++) {
            if (DEBUG)
                System.out.println("\nIn Cell " + i + ":");
            int current = cells[i];
            while (current != -1) {
                List<Integer> candidates = new ArrayList<>(Arrays.asList(getCandidates(i)));
                candidates.remove((Integer) current);
                if (DEBUG) {
                    StringBuilder str = new StringBuilder(String.format("For Particle %d candidates are: ", current));
                    for (int candidate : candidates) {
                        str.append(candidate + "||");
                    }
                    System.out.println(str.toString());
                }
                for (int candidate : candidates) {
                    double distance = particles[current].distanceTo(particles[candidate]);
                    if (distance < criticalRadius) {
                        if (DEBUG)
                            System.out.println(String.format("%d and %d are neighbours with distance %f", current,
                                    candidate, distance));
                        neighbours[current].add(candidate);
                        neighbours[candidate].add(current);
                    }
                }

                current = particleRefs[current];
            }
        }

        if (DEBUG)
            showResults(cells, particleRefs);
        return neighbours;
    }

    private static Integer[] getCandidates(int cellIndex) {
        List<Integer> candidates = new ArrayList<>();
        List<Integer> cellsToCheck = Arrays.asList(
                cellIndex,
                cellIndex + 1,
                (cellIndex + gridSize),
                (cellIndex + gridSize) + 1,
                (cellIndex - gridSize) + 1);

        for (int cellToCheck : cellsToCheck) {
            if (cellToCheck < 0 || cellToCheck >= cells.length || outOfHorizontalBoundaries(cellIndex, cellToCheck))
                continue;

            // Tengo que agregar todas las particulas de esta celda
            int head = cells[cellToCheck];
            while (head != -1) {
                candidates.add(head);
                head = particleRefs[head];
            }
        }
        return Arrays.copyOf(candidates.toArray(), candidates.size(), Integer[].class);
    }

    private static boolean isBorder(int cell) {
        return cell % gridSize == 0 || cell % gridSize == (gridSize - 1) || cell < gridSize
                || cell > (gridSize * gridSize - gridSize - 1);
    }

    private static boolean outOfHorizontalBoundaries(int origin, int target) {

        return Math.abs(origin % gridSize - target % gridSize) > 1;
    }

    // private static double getDistance(Particle p1, Particle p2) {
    // double deltaX = p1.x - p2.x;
    // double deltaY = p1.y - p2.y;
    // return (Math.sqrt(deltaX * deltaX + deltaY * deltaY)) - (p1.radius +
    // p2.radius);
    // }

    private static int getCell(Particle particle, double size) {
        int x = (int) (particle.x / size * gridSize);
        int y = (int) (particle.y / size * gridSize);
        int cell = y * gridSize + x;
        if (DEBUG)
            System.out.println(String.format("For particle %d, its position on the grid is: %d %d. So it's in cell %d",
                    particle.getIndex(), x, y, cell));
        return cell;
    }

    public static void showResults(int[] cells, int[] particleRefs) {
        // System.out.println("");
        // StringBuilder s1 = new StringBuilder();
        // System.out.println("Cells:");
        // for(int c : cells){
        // s1.append(c + ", ");
        // }
        // System.out.println(s1.toString());
        // System.out.println("");
        // System.out.println("Particle Refs:");

        // StringBuilder s2 = new StringBuilder();

        // for(int r : particleRefs){
        // s2.append(r + ", ");

        // }
        // System.out.println(s2.toString());

        System.out.println("\nNeighbours:");
        for (int i = 0; i < neighbours.length; i++) {
            StringBuilder sb = new StringBuilder(i + ": ");
            neighbours[i].forEach(n -> sb.append(n).append(" | "));
            if (neighbours[i].size() > 0)
                sb.delete(sb.length() - 3, sb.length());
            System.out.println(sb.toString());
        }

    }
}
