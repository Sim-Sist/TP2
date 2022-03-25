package output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import particles.Particle;
import particles.Space;

public class OutputManager {
    private Space s;
    private final String INIT_STATE_DEFAULT_FILENAME = "static-infoparticles.txt";
    private final String DYNAMIC_STATE_DEFAULT_FILENAME = "dynamic-info.txt";
    private final String LOCAL_OUTPUT_PATH = "src/main/output/";

    public OutputManager(Space s) {
        this.s = s;
    }

    private String getRoot() {
        URL u = getClass().getProtectionDomain().getCodeSource().getLocation();
        return (u.toString().replace("file:", "").replace("bin/", ""));
    }

    public boolean outputInitialState() {
        return this.outputInitialState(INIT_STATE_DEFAULT_FILENAME);
    }

    public boolean outputInitialState(String filename) {
        FileWriter fw;
        try {
            String filepath = getRoot() + LOCAL_OUTPUT_PATH;
            File file = new File(filepath, filename);
            file.createNewFile();
            fw = new FileWriter(file);

            /**
             * Header stile goes like:
             * - Number of particles
             * - Size of Space
             * - Length of critical radius
             * - One free line to put a comment/name. This can also be left blank.
             */
            fw.append(Integer.toString(s.getParticles().length)).append('\n');
            fw.append(Double.toString(s.getSize())).append('\n');
            fw.append(Double.toString(s.getCriticalRadius())).append('\n');
            fw.append('\n');

            /**
             * Body of textfile consists of one line for each particle with its radius
             */

            for (Particle p : s.getParticles()) {
                fw.append(String.format("%f\n", p.radius));
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean outputState(int tn) {
        String path = getRoot() + LOCAL_OUTPUT_PATH;
        File f = new File(path, DYNAMIC_STATE_DEFAULT_FILENAME);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("There was an error while creating output file:\n");
                e.printStackTrace();
                return false;
            }
        }
        FileWriter fw;
        try {
            fw = new FileWriter(f, true);

            /**
             * This file contains a list of entries, each one for a specific time tn. Each
             * entry goes like:
             * 
             * - tn
             * - One line for each particle with its position and speed (decomposed in x and
             * y), separated by spaces
             */

            fw.append(Integer.toString(tn)).append('\n');
            for (Particle p : s.getParticles()) {
                fw.append(String.format("%f %f %f %f\n", p.x, p.y, p.vx, p.vy));
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
