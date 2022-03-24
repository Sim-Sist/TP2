package output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import particles.Particle;
import particles.Space;

public class OutputManager {
    private Space s;
    private final String INIT_STATE_DEFAULT_FILENAME = "particles.txt";
    private final String NEIGHBOURS_DEFAULT_FILENAME = "neighbours.txt";

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
            String filepath = getRoot() + "src/main/output/";
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
             * Body of textfile consists of one line for each particle
             * with its radius and then its xy coordinates, all separated by a spaces
             */

            for (Particle p : s.getParticles()) {
                fw.append(String.format("%f %f %f\n", p.radius, p.x, p.y));
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean outputNeighbours() {
        return this.outputNeighbours(NEIGHBOURS_DEFAULT_FILENAME);
    }

    public boolean outputNeighbours(String filename) {
        if (s.getNeighbours() == null) {
            return false;
        }
        FileWriter fw;
        int defaultTarget = 1;
        try {
            String filepath = getRoot() + "src/main/output/";
            File file = new File(filepath, filename);
            file.createNewFile();
            fw = new FileWriter(file);

            /**
             * Header stile goes like:
             * - One free line to put a comment/name. This can also be left blank.
             */
            fw.append('\n');

            /**
             * Body of textfile consists of one line for each particle
             * with a list of all its neighbours separated by a space
             */
            for (Set<Integer> s : s.getNeighbours()) {
                StringBuilder sb = new StringBuilder();
                for (Integer n : s) {
                    sb.append(n).append(" ");
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1); // Delete last space
                sb.append('\n');
                fw.append(sb.toString());
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
