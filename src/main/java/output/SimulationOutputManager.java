package output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import simulations.SimulationManager;

public class SimulationOutputManager extends OutputManager {
    private SimulationManager sim;
    private static final String POLARIZATION_HIST_FILE_NAME = "polarizations.txt";
    private static final String LOCAL_OUTPUT_PATH = "src/main/output/";

    public SimulationOutputManager(SimulationManager sim) {
        this.sim = sim;
    }

}
