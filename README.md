# TP2
```bash
cd ./src
chmod +x manualRun.sh
```
and then
```bash
./manualRun.sh
```
to generate output files in folder _src/output_

The output files have the following formats:

**static-info.txt:**

##### Header
- Number of particles
- Size of Space
- Length of critical radius
- One free line to put a comment/name. This can also be left blank.

##### Body
- Body of textfile consists of one line for each particle with its radius

**dynamic-info.txt:**

This file contains a list of entries, each one for a specific time $t_n$. Each entry goes like:
- $t_n$
- One line for each particle with its position and speed (decomposed in x and y), separated by spaces
