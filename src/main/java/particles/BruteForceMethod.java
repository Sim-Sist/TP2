package particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BruteForceMethod {
    @SuppressWarnings("unchecked")
    public static Set<Integer>[] apply(Particle[] particles, double criticalRadius) {
        List<Particle> particlesList = new ArrayList<>(Arrays.asList(particles));
        Set<Integer>[] neighbours;
        neighbours = (Set<Integer>[]) (new HashSet[particles.length]);
        Arrays.setAll(neighbours, (i) -> setsProvider());
        for (Particle p : particlesList) {
            particlesList.remove(p);
            for (Particle q : particlesList) {
                double distance = p.distanceTo(q);
                if (distance <= criticalRadius) {
                    neighbours[p.getIndex()].add(q.getIndex());
                    neighbours[q.getIndex()].add(p.getIndex());
                }
            }
        }
        return neighbours;
    }

    private static Set<Integer> setsProvider() {
        return new HashSet<>();
    }

    private static double getDistance(Particle p1, Particle p2) {
        double deltaX = p1.x - p2.x;
        double deltaY = p1.y - p2.y;
        return (Math.sqrt(deltaX * deltaX + deltaY * deltaY)) - (p1.radius + p2.radius);
    }
}
