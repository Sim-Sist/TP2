import math
import subprocess
from matplotlib.pyplot import plot
from numpy import double
from plotter import Plotter
from utils import move_to_src, get_src
import os
from statistics import mean,stdev
import numpy as np
import matplotlib.pyplot as plt
import pandas as pnd

PYTHON_LOCAL_PATH = "/main/python/"
OUTPUT_LOCAL_PATH = "/main/output/"
POLARIZATIONS_FILENAME = "polarizations.txt"
PLOTS_LOCAL_PATH = OUTPUT_LOCAL_PATH + "plots/"

####################### Main

FRAMES_PER_SIM = 2000
VELOCITY = 0.3
variable_values = [5]
# variable_values.extend([(i+1)*0.5 for i in range(20)])
SIM_AMOUNT = len(variable_values)
print(SIM_AMOUNT)

NAME_OF_VARIABLE="Density"
PLOT_EXTENSION="png" # png or pdf (better quality)

mean_polarizations = []
deviations = []
raw_polarizations = []

# Read simulation output and compute polarization
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
    static_values = polarizations[1000:]
    mean_polarizations.append(mean(static_values))
    deviations.append(stdev(static_values))
    raw_polarizations.append(polarizations)

plot_output_path = get_src()+PLOTS_LOCAL_PATH

plt.figure(figsize=(16,10),dpi=80)
plt.errorbar([i * 0.5 for i in range(len(mean_polarizations))], mean_polarizations, yerr = deviations,ecolor='red')
plt.ylim(0,1)
plt.xlim(0,5)
plt.grid(axis='both', alpha=.3)
plt.xticks([i * 0.5 for i in range(len(mean_polarizations))],[f'{i:g}' for i in variable_values])
plt.yticks([i * 0.1 for i in range(11)])
ax_label_fontsize = 15
plt.xlabel(NAME_OF_VARIABLE, fontsize=ax_label_fontsize )
plt.ylabel("Average velocity",fontsize=ax_label_fontsize )
# remove borders
plt.gca().spines["top"].set_alpha(0.0)
plt.gca().spines["bottom"].set_alpha(0.3)
plt.gca().spines["right"].set_alpha(0.0)
plt.gca().spines["left"].set_alpha(0.3)
  

plt.title('Speed polarization through %s variation'%(NAME_OF_VARIABLE),fontsize=ax_label_fontsize*1.5)

plt.savefig(plot_output_path+"%s.%s"%(NAME_OF_VARIABLE,PLOT_EXTENSION),bbox_inches='tight')

for i in range(len(raw_polarizations)):
    fig = plt.figure(figsize=(20,13),dpi=100)
    yvals = raw_polarizations[i]
    xvals = [i for i in range(len(yvals))]
    plt.title("Polarization for %s=%g"%(NAME_OF_VARIABLE,variable_values[i]), fontsize=ax_label_fontsize *1.5)
    plt.plot(xvals,yvals)

    plt.ylim(0,1)
    plt.xlim(0,len(xvals))

    plt.grid(axis='both', alpha=.3)
    
    
    plt.xlabel("frame number", fontsize=ax_label_fontsize )
    plt.ylabel("average speed", fontsize=ax_label_fontsize )
    # remove borders
    plt.gca().spines["top"].set_alpha(0.0)
    plt.gca().spines["bottom"].set_alpha(0.3)
    plt.gca().spines["right"].set_alpha(0.0)
    plt.gca().spines["left"].set_alpha(0.3)

    plot_name = "plot%02d.%s"%(i,PLOT_EXTENSION)
    plt.savefig(plot_output_path+plot_name,bbox_inches='tight')
    plt.clf()
    plt.close('all')

df = pnd.DataFrame(
    {
        "Density": [(i+1) * 0.5 for i in range(len(mean_polarizations))],
        "Polarization":mean_polarizations,
        "Error":deviations
    })

print(df)


# print("Mean Polarizations:")
# print("Density: polarization")
# for i in range(len(mean_polarizations)):
#     print("%d: %d"%((i+1)*0.5,mean_polarizations[i]))

