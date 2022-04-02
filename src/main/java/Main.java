import java.util.stream.IntStream;

import simulations.SimulationManager;

/*
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

        // sManager.simulationSuiteForNoise(IntStream.range(0, 11).boxed().mapToDouble(i
        // -> i * 0.5).boxed().toList());
        sManager.simulationSuiteForDensity(IntStream.range(1, 21).boxed().mapToDouble(i -> i * 0.5).boxed().toList());
    }

}
