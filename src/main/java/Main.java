import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import simulations.SimulationManager;

/*
    TODO: hacer output de múltiples simulaciones
    TODO: agregar gráfico con barras de error a la clase Plotter
    TODO: Extra: Integrar mejor la opción de contorno continuo
*/
public class Main {


    public static void main(String[] args) {
        SimulationManager sManager;
        if (args.length < 3)
            sManager = new SimulationManager();
        else {
            sManager = new SimulationManager(
                    Integer.parseInt(args[0]), // particles
                    Double.parseDouble(args[1]), // size
                    Double.parseDouble(args[2])); // noise
        }
        // sManager.simulate();

        sManager.simulationSuiteForNoise(IntStream.range(0, 11).boxed().mapToDouble(i -> i * 0.5).boxed().toList());
    }

    public static void test() {
        // final double size = 20, radius = 1;
        // int gridSize, particles;

        // Map<Integer, Double> elapsedTimes = new TreeMap<>();
        // // Test performance with fixed grid size as 25
        // gridSize = 25;
        // Space s;
        // for (particles = 0; particles < 50000; particles += 1000) {
        // List<Double> times = new ArrayList<>();
        // for (int i = 0; i < 30; i++) {
        // s = new Space(size);
        // s.setCriticalRadius(radius);
        // s.initialize(particles);
        // long start = System.nanoTime();
        // s.calculateCells(gridSize);
        // long end = System.nanoTime();
        // double elapsed = (end - start) / Math.pow(10, 9);
        // times.add(elapsed);
        // }
        // elapsedTimes.put(particles, times.stream().mapToDouble(x ->
        // x).average().getAsDouble());
        // }
        // System.out.println("Performance with increasing particles for grid size of "
        // + gridSize + ":");
        // System.out.println("Particles Elapsed Time");
        // elapsedTimes.forEach((k, v) -> System.out.println(String.format("%9d %12f",
        // k, v)));

    }

}
