import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import simulations.SimulationManager;

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
        sManager.simulate();

        // List<Double> noiseValues = new LinkedList<>();
        // noiseValues.addAll(IntStream.rangeClosed(0, 10).mapToDouble(i -> i *
        // 0.5).boxed().toList());
        // sManager.simulationSuiteForNoise(noiseValues);

        // List<Double> densityValues = new LinkedList<>();
        // densityValues.add(0.1);
        // densityValues.addAll(IntStream.rangeClosed(1, 10).mapToDouble(i ->
        // i).boxed().toList());
        // sManager.simulationSuiteForDensity(densityValues);
    }

}
