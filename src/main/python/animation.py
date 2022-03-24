"""
Para hacer:
    1) Leer archivo txt
    2) Generamos particulas
    3) Dibujamos particulas
"""
from p5 import *

from asyncore import read

import math

def readInfo():
    with open('./output/particles.txt') as info:
        for line in info:
            yield line


def readNeighbours():
    with open('./output/neighbours.txt') as info:
        for line in info:
            yield line


RESIZE_FACTOR = 30

neighbours = readNeighbours()
# Salto 1 lineas vacias
neighbours.__next__()
#
info = readInfo()
cantParticles = int(info.__next__())
canvasSize = float(info.__next__()) * RESIZE_FACTOR
criticalRadio = float(info.__next__()) * RESIZE_FACTOR
blancLine = info.__next__()

radios = []

selectedColor = "#DA2929"
noSelectedColor = "#3E3B3B"
backgroundColor = "#212121"
neighbourColor = "#29ACDA"

L = canvasSize
M = 10 * RESIZE_FACTOR
RC = criticalRadio

def getParticlesStaticInfo():
    index = 0
    radiosLine = info.__next__().split(' ')
    for index in range(cantParticles):
        radios.append(float(radiosLine[index]))


def stringCompatible(str):
    if (len(str) > 0 and str != '\n'):
        return int(str)
    return -1


def initializeParticle():
    for index in range(cantParticles):
        infoLine = info.__next__().split(' ')
        neighLine = neighbours.__next__().split(' ')

        numInfoLine = list(map(lambda str: float(str), infoLine))
        numNeighLine = set(map(lambda str: stringCompatible(str), neighLine))

        # Aca tambien estaria la velocidad y eso cuando haga falta
        position = [numInfoLine[1], numInfoLine[2]]
        radio = numInfoLine[0]
        velocity = [0, 0]
        yield Particle(index, position[0], position[1], velocity[0],
                       velocity[1], 2 * radio, noSelectedColor, False,
                       numNeighLine, False)


class Particle:

    def __init__(self, id, x, y, vx, vy, d, color, active, neighbours,
                 isNeighbour):
        self.id = id
        self.x = x * RESIZE_FACTOR
        self.y = y * RESIZE_FACTOR
        self.vx = vx
        self.vy = vy
        self.d = d * RESIZE_FACTOR
        self.color = color
        self.active = active
        self.neighbours = neighbours
        self.isNeighbour = isNeighbour

    def drawWithColor(self, color):
        no_stroke()
        fill(color)
        circle(self.x, self.y, self.d)
        fill('#ffffff')
        text_align('CENTER')
        text(
            str(self.id),
            self.x,
            self.y,
        )

    def draw(self):

        if (self.active):
            self.color = selectedColor
            drawCriticalRadio(self.x, self.y, self.d / 2)
        else:
            if (self.isNeighbour):
                self.color = neighbourColor
            else:
                self.color = noSelectedColor

        self.drawWithColor(self.color)

    def activate(self):
        self.active = True

    def deactivate(self):
        self.active = False

    def activateNeighbour(self):
        self.isNeighbour = True

    def deActivateNeighbour(self):
        self.isNeighbour = False


particles = []
for particle in initializeParticle():
    particles.append(particle)


def drawParticles():
    for particle in particles:
        particle.draw()


def drawNeighbours(particleNeighbours):
    #print(particleNeighbours)
    # for particle in particles:
    #     if particle.id in particleNeighbours:
    #         # particle.drawWithColor(neighbourColor)
    #         particle.activateNeighbour()
    #     else:
    #         particle.deActivateNeighbour()
    for particleId in particleNeighbours:
        particles[particleId].activateNeighbour()


def deactivateAll():
    for particle in particles:
        particle.deActivateNeighbour()


def setup():
    size(canvasSize, canvasSize)
    no_stroke()
    no_loop()

def draw():
    background(backgroundColor)
    drawParticles()
    drawGrid()


def mouse_pressed(event):
    deactivateAll()

    for particle in particles:
        if (math.sqrt((event.x - particle.x)**2 +
                      (event.y - particle.y)**2) < particle.d / 2):
            particle.activate()
            drawNeighbours(particle.neighbours)
        else:
            particle.deactivate()
    redraw()


def drawCriticalRadio(x, y, particle_radius):
    no_fill()
    stroke(selectedColor)
    stroke_weight(2)
    circle(x, y, 2 * (RC + particle_radius))


def drawGrid():
    stroke(90, 90, 90, 63)
    stroke_weight(0.1)

    begin_shape()
    width = int(L / M)
    height = int(L / M)

    for i in range(height):
        vertex(0, i * M)
        vertex(L, i * M)
        vertex(0, i * M)

    for j in range(width):
        vertex(j * M, 0)
        vertex(j * M, L)
        vertex(j * M, 0)

    end_shape()


run()
