import math
import subprocess
from matplotlib.pyplot import plot
from numpy import double
from plotter import Plotter
from utils import move_to_src, get_src
import os
from statistics import mean,stdev

PYTHON_LOCAL_PATH = "/main/python/"
OUTPUT_LOCAL_PATH = "/main/output/"
POLARIZATIONS_FILENAME = "polarizations.txt"
# File format is -> "noise polarization error"
def noise_study():
    src = get_src()
    filepath = src + OUTPUT_LOCAL_PATH + "noiseStudy.txt"
    plotter = Plotter()
    try:
        os.remove(filepath)
    except OSError:
        pass
    file = open(filepath,"w")
    # Simulation Variables
    PARTICLES = 400
    SIZE = 10
    for i in range(10):
        noise = i * 0.5
        process = subprocess.Popen([get_src() + "/manualRun.sh",str(PARTICLES),str(SIZE),str(noise)])
        process.wait()
        pols = read_polarization_file()
        static_values = pols[-20:]
        polaritation_value = mean(static_values)
        error = stdev(static_values)
        plotter.add_plot("Polarization: " + str(round(polaritation_value,3)) + " | " + str(round(error,3)),"frame","polarization "+ str(i),[*range(len(pols))],pols)
        file.write(str(noise) + " " + str(polaritation_value) + " "+ str(error) + "\n")
    file.close()
    plotter.plot()

def density_study():
    plotter = Plotter()
    # Simulation Variables
    SIZE = 20
    NOISE = 1
    for i in range(1):
        density = 10
        process = subprocess.Popen([get_src() + "/manualRun.sh",str(density * SIZE),str(SIZE),str(NOISE)])
        process.wait()
        pols = read_polarization_file()
        static_values = pols[1000:]
        polaritation_value = mean(static_values)
        error = stdev(static_values)
        plotter.add_plot("Polarization: " + str(round(polaritation_value,3)) + " | " + str(round(error,3)),"frame","polarization "+ str(i),[*range(len(pols))],pols)
    plotter.plot()


def read_polarization_file() -> list:
    file = open(get_src() + OUTPUT_LOCAL_PATH + POLARIZATIONS_FILENAME)
    polarizations = []
    for line in file:
        polarizations.append(double(line))
    return polarizations


## Para cada Simulación:
##  leer el archivo static para conseguir info sobre el tamaño y la cantidad de partículas
##  leer el archivo dynamic y por cada frame ir sumando las velocidades en x e y de cada partícula
##  una vez tengo todos los valores, calcular la polarización para ese frame como sqrt(vxt^2 + vyt^2)/(velocidad * cantParticulas)
SIM_AMOUNT = 11
FRAMES_PER_SIM = 2000
VELOCITY = 0.3

for i in range(SIM_AMOUNT):
    filename = "static-info%03d.txt"%(i)
    static_info = open(get_src() + OUTPUT_LOCAL_PATH +filename)
    particles_amount = int(static_info.readline())
    filename = "dynamic-info%03d.txt"%(i)
    file = open(get_src() + OUTPUT_LOCAL_PATH + filename)
    speed_acum = []
    for line in file:
        sections = line.split(" ")
        if(len(sections) == 1):
            # is header
            speed_acum.append([0,0])
        else:
            speed_acum[-1][0] += double(sections[2]) #vx
            speed_acum[-1][1] += double(sections[3]) #vy
    polarizations = [math.sqrt(v[0]**2 + v[1]**2) / (VELOCITY * particles_amount) for v in speed_acum]
    print(mean(polarizations[1000:]))
    

