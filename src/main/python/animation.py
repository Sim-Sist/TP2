from functools import lru_cache
from p5 import *
from p5.core.primitives import Arc
from time import time


def readStatic():
    with open('../output/static-info.txt') as info:
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
particles = []

selectedColor = "#DA2929"
noSelectedColor = "#3E3B3B"
backgroundColor = "#212121"
neighbourColor = "#29ACDA"

L = canvasSize
RC = criticalRadio


def getParticlesStaticInfo():
    for index in range(cantParticles):
        radios.append(float(static.__next__()))


def initializeParticles():
    frame = dynamic.__next__()
    for index in range(cantParticles):

        # parsedInfo = list(map(float, dynamic.__next__().split(' ')))
        parsedInfo = [float(x) for x in dynamic.__next__().split(' ')]

        # print(frame)
        # print(parsedInfo)

        position = [parsedInfo[0], parsedInfo[1]]
        velocity = [parsedInfo[2], parsedInfo[3]]

        # yield Particle(
        #     index,
        #     position[0],
        #     position[1],
        #     velocity[0],
        #     velocity[1],
        #     2 * radios[index],
        #     selectedColor
        # )
        particles.append(Particle(
            index,
            position[0],
            position[1],
            velocity[0],
            velocity[1],
            2 * radios[index],
            selectedColor)
        )


@lru_cache(maxsize=None)
def shape(d, color):
    return Arc((0, 0), (d / 2, ) * 2, 0, 360, fill_color=color)


def updateParticles():
    frame = dynamic.__next__()
    for index in range(cantParticles):

        #start = time()
        parsedInfo = [float(x) for x in dynamic.__next__().split(' ')]

        #parsedInfo = [0, 0, 0, 0]

        #print(time() - start)
        # print(frame)
        # print(parsedInfo)

        position = [parsedInfo[0], parsedInfo[1]]
        velocity = [parsedInfo[2], parsedInfo[3]]

        particles[index].move(position[0], position[1],
                              velocity[0], velocity[1])


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
        translate(self.x, self.y)
        draw_shape(shape(self.d, color))
        translate(-self.x, -self.y)

        # no_stroke()
        # fill(color)
        # circle(self.x, self.y, self.d)
        # stroke(neighbourColor)
        # stroke_weight(2)
        # line(self.x, self.y, self.x + self.vx * 20, self.y + self.vy * 20)

    def draw(self):
        self.drawWithColor(self.color)

    def move(self, x, y, vx, vy):
        self.x = x * RESIZE_FACTOR
        self.y = y * RESIZE_FACTOR
        self.vx = vx
        self.vy = vy
        self.draw()


getParticlesStaticInfo()

# for particle in initializeParticles():
#    particles.append(particle)
initializeParticles()


def drawParticles():
    start = time()
    updateParticles()
    print(time() - start)

    # for particle in particles:
    #    particle.draw()


def setup():
    size(canvasSize, canvasSize)
    no_stroke()
    print("Setup")


def draw():
    print("Draw")
    background(backgroundColor)
    drawParticles()

    # save_frame("images/000.png")

    if(frame_count == 50):
        no_loop()
        exit()


run(frame_rate=50)
