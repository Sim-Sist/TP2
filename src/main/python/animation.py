"""
Para hacer:
    1) Leer archivo txt
    2) Generamos particulas
    3) Dibujamos particulas
"""
from p5 import *

from asyncore import read

import math


def readStatic():
    with open('../output/static-infoparticles.txt') as info:
        for line in info:
            yield line


def readDynamic():
    with open('../output/dynamic-info.txt') as info:
        for line in info:
            yield line


RESIZE_FACTOR = 30

static = readStatic()
dynamic = readDynamic()

cantParticles = int(static.__next__())
canvasSize = float(static.__next__()) * RESIZE_FACTOR
criticalRadio = float(static.__next__()) * RESIZE_FACTOR
# blancLine
static.__next__()

radios = []

selectedColor = "#DA2929"
noSelectedColor = "#3E3B3B"
backgroundColor = "#212121"
neighbourColor = "#29ACDA"

L = canvasSize
RC = criticalRadio


def getParticlesStaticInfo():
    for index in range(cantParticles):
        radios.append(float(static.__next__()))


getParticlesStaticInfo()


def stringCompatible(str):
    if (len(str) > 0 and str != '\n'):
        return int(str)
    return -1


def updateParticles():
    frame = dynamic.__next__()
    for index in range(cantParticles):

        parsedInfo = list(map(lambda str: float(
            str), dynamic.__next__().split(' ')))

        print(frame)
        print(parsedInfo)

        position = [parsedInfo[0], parsedInfo[1]]
        velocity = [parsedInfo[2], parsedInfo[3]]

        yield Particle(
            index,
            position[0],
            position[1],
            velocity[0],
            velocity[1],
            2 * radios[index],
            noSelectedColor
        )


class Particle:

    def __init__(self, id, x, y, vx, vy, d, color):
        self.id = id
        self.x = x * RESIZE_FACTOR
        self.y = y * RESIZE_FACTOR
        self.vx = vx
        self.vy = vy
        self.d = d * RESIZE_FACTOR
        self.color = color

    def drawWithColor(self, color):
        no_stroke()
        fill(color)
        circle(self.x, self.y, self.d)
        stroke(selectedColor)
        stroke_weight(1)
        line(self.x, self.y, self.x + self.vx * 10, self.y + self.vy * 10)

    def draw(self):
        self.drawWithColor(self.color)


def drawParticles():
    for particle in updateParticles():
        particle.draw()


def setup():
    size(canvasSize, canvasSize)
    no_stroke()


def draw():
    background(backgroundColor)
    drawParticles()

    if(frame_count == 2):
        no_loop()


run(frame_rate=1)
