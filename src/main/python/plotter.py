import math
from random import randint
from matplotlib import markers
import numpy as np
import pandas as pd
import matplotlib as mpl
import matplotlib.pyplot as plt
import seaborn as sns

class Plotter:

    def __init__(self):
        self.plots = []

    def add_plot(self, title, x_title, y_title, x, y,error):
        self.plots.append({
            "main_title": title,
            "data": {x_title: x, y_title: y},
            "x_label": x_title,
            "y_label": y_title
        })

    def plot(self):
        plots_amount = len(self.plots)
        for i in range(plots_amount):
            ax = self.__add_subplot__(plots_amount, i+1)
            self.__draw__(i, ax)
            self.__decorate__(i)

        self.__remove_borders__()
        plt.show()

    def __add_subplot__(self, plots_amount, plot_index):
        if(plots_amount <= 2):
            return self.__get_figure__().add_subplot(1, plots_amount, plot_index)
        return self.__get_figure__().add_subplot(2, int(math.ceil(plots_amount/2)), plot_index)

    def __get_figure__(self):
        try:
            return self.fig
        except AttributeError:
            self.fig = plt.figure(figsize=(16, 10), dpi=80)
            return self.fig

    def __remove_borders__(self):
        plt.gca().spines["top"].set_alpha(0.0)
        plt.gca().spines["bottom"].set_alpha(0.3)
        plt.gca().spines["right"].set_alpha(0.0)
        plt.gca().spines["left"].set_alpha(0.3)

    def __draw__(self, plot_index, ax):
        current_plot = self.plots[plot_index]
        # plt.figure(figsize=(16, 10), dpi=80)
        ax.plot(current_plot.get("x_label"), current_plot.get("y_label"), data=current_plot.get("data"),
                color='tab:red', marker='o')

    def __decorate__(self, plot_index):
        current_plot = self.plots[plot_index]
        plt.ylim(min(current_plot.get("data").get(current_plot.get("y_label"))),
                 max(current_plot.get("data").get(current_plot.get("y_label"))))
        # xtick_location = df.index.tolist()[::12]
        # xtick_labels = [x[-4:] for x in df.date.tolist()[::12]]
        # plt.xticks(ticks=xtick_location, labels=xtick_labels, rotation=0,
        #            fontsize=12, horizontalalignment='center', alpha=.7)
        # plt.yticks(fontsize=12, alpha=.7)
        plt.title(current_plot.get("main_title"), fontsize=15)
        plt.grid(axis='both', alpha=.3)
        plt.xlabel(current_plot.get("x_label"), fontsize=10)
        plt.ylabel(current_plot.get("y_label"), fontsize=10)


