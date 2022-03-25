package particles.cim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import particles.Particle;
import particles.Space;

public class CellIndexMethod {
    private static boolean DEBUG = false;
    private static boolean periodicBoundary = true;

    private static int[] cells;
    private static int[] particleRefs;
    private static int gridSize;
    private static Set<Integer>[] neighbours;
    private static double spaceSize;

    private static Set<Integer> setsProvider() {
        return new HashSet<>();
    }

    private CellIndexMethod() {
    }

    @SuppressWarnings("unchecked")
    public static Set<Integer>[] apply(Space s) {
        Particle[] particles = s.getParticles();
        spaceSize = s.getSize();
        double criticalRadius = s.getCriticalRadius();

        double maxRadius = Arrays.asList(particles).stream().mapToDouble(p -> p.radius).max().getAsDouble();
        gridSize = (int) Math.floor(spaceSize / (criticalRadius + 2 * maxRadius));

        neighbours = (Set<Integer>[]) (new HashSet[particles.length]);
        Arrays.setAll(neighbours, (i) -> setsProvider());
        BiFunction<Particle, Particle, Double> computeDistance = periodicBoundary
                ? (p1, p2) -> getPeriodicDistance(p1, p2)
                : (p1, p2) -> p1.distanceTo(p2);

        if (DEBUG)
            System.out.println(String.format("CIM:\n\tSpace size: %.3f\n\tCells per side: %d\n", spaceSize, gridSize));

        // Initialize arrays
        cells = new int[gridSize * gridSize];
        Arrays.fill(cells, -1);
        particleRefs = new int[particles.length];
        Arrays.fill(particleRefs, -1);

        // Compute cell coordinates for each particle and asign to corresponding array
        for (Particle p : particles) {
            int cell = getCell(p);
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

        // Check in each cell for particles and get distances
        for (int i = 0; i < cells.length; i++) {
            if (DEBUG)
                System.out.println("\nIn Cell " + i + ":");
            int current = cells[i];
            while (current != -1) {
                // Get candidate particles for this cell to measure distance
                List<Integer> candidates = new LinkedList<>(Arrays.asList(getCandidates(i)));
                candidates.remove((Integer) current);// Remove current particle to avoid innecesary calculations
                if (DEBUG) {
                    StringBuilder str = new StringBuilder(String.format("For Particle %d candidates are: ", current));
                    for (int candidate : candidates) {
                        str.append(candidate + "||");
                    }
                    System.out.println(str.toString());
                }
                for (int candidate : candidates) {
                    double distance;
                    distance = computeDistance.apply(particles[current], particles[candidate]);
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

    private static List<Integer> getCellsToCheck(int cellIndex) {
        List<Integer> cellsToCheck = new LinkedList<>();
        cellsToCheck.add(cellIndex);
        // Most of the cells will be internal so it's faster to discard them early
        if (!isBorder(cellIndex)) {
            cellsToCheck.addAll(Arrays.asList(
                    cellIndex + 1,
                    (cellIndex + gridSize),
                    (cellIndex + gridSize) + 1,
                    (cellIndex - gridSize) + 1));
            return cellsToCheck;
        }
        // In this case, the cell is at the border of the matrix
        int[] coords = getAsCoordinates(cellIndex);
        int x = coords[0], y = coords[1];
        cellsToCheck.addAll(
                Arrays.asList(
                        getAsIndex(new int[] { x - 1, y + 1 }),
                        getAsIndex(new int[] { x, y + 1 }),
                        getAsIndex(new int[] { x + 1, y + 1 }),
                        getAsIndex(new int[] { x + 1, y }),
                        getAsIndex(new int[] { x + 1, y - 1 })));
        cellsToCheck.removeIf(i -> i == null);
        return cellsToCheck;
    }

    private static Integer[] getCandidates(int cellIndex) {
        List<Integer> candidates = new ArrayList<>();
        List<Integer> cellsToCheck = getCellsToCheck(cellIndex);

        for (int cellToCheck : cellsToCheck) {
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

    // private static boolean outOfHorizontalBoundaries(int origin, int target) {
    // return Math.abs(origin % gridSize - target % gridSize) > 1;
    // }

    private static int getCell(Particle particle) {
        int x = (int) (particle.x / spaceSize * gridSize);
        int y = (int) (particle.y / spaceSize * gridSize);
        int cell = y * gridSize + x;
        if (DEBUG)
            System.out.println(String.format("For particle %d, its position on the grid is: %d %d. So it's in cell %d",
                    particle.getIndex(), x, y, cell));
        return cell;
    }

    public static void showResults(int[] cells, int[] particleRefs) {
        System.out.println("\nNeighbours:");
        for (int i = 0; i < neighbours.length; i++) {
            StringBuilder sb = new StringBuilder(i + ": ");
            neighbours[i].forEach(n -> sb.append(n).append(" | "));
            if (neighbours[i].size() > 0)
                sb.delete(sb.length() - 3, sb.length());
            System.out.println(sb.toString());
        }

    }

    // [x, y]
    private static int[] getAsCoordinates(int index) {
        int[] coord = new int[2];
        coord[0] = index % gridSize;
        coord[1] = (int) Math.floor((double) index / gridSize);
        return coord;
    }

    private static Integer getAsIndex(int[] coord) {
        int x = coord[0];
        int y = coord[1];

        if (x > gridSize || x < -1 || y > gridSize || y < -1) {
            throw new RuntimeException(String.format("Invalid coordinates (%d,%d)", x, y));
        }

        if (periodicBoundary) {

            if (x == gridSize) {
                x = 0;
            }
            if (x == -1) {
                x = gridSize - 1;
            }
            if (y == gridSize) {
                y = 0;
            }
            if (y == -1) {
                y = gridSize - 1;
            }
        } else {
            if (x == gridSize || x == -1 || y == gridSize || y == -1) {
                return null;
            }
        }
        return y * gridSize + x;
    }

    private static double getPeriodicDistance(Particle p1, Particle p2) {
        double dx = Math.abs(p1.x - p2.x);
        double dy = Math.abs(p1.y - p2.y);
        if (dx > 0.5 * spaceSize)
            dx = spaceSize - dx;
        if (dy > 0.5 * spaceSize)
            dy = spaceSize - dy;

        return (Math.sqrt(dx * dx + dy * dy)) - (p1.radius + p2.radius);
    }
}
