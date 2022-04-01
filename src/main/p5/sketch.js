let static, dynamic;

let cantParticles, canvasSize, criticalRadio;

let radios = [],
	particles = [];

RESIZE_FACTOR = 30;

function preload() {
	static = loadStrings('../output/static-info.txt');
	dynamic = loadStrings('../output/dynamic-info.txt');
}

function loadStaticData() {
	cantParticles = static[0];
	canvasSize = static[1] * RESIZE_FACTOR;
	criticalRadio = static[2];

	for (let i = 0; i < cantParticles; i++) {
		radios[i] = float(static[i + 4]);
	}
}

let dynamicIndex = 0;

function loadDynamicData() {
	for (let i = 0; i < cantParticles; i++) {
		info = dynamic[i + 1].split(' ');
		//console.log(radios[i])

		particles[i] = new Particle(
			float(info[0]),
			float(info[1]),
			float(info[2]),
			float(info[3]),
			float(radios[i]),
			'#DA2929'
		);
		//console.log(particles[i])
	}
	dynamicIndex++;
}

function refresh() {
	for (
		let i = cantParticles * dynamicIndex + 1;
		i < cantParticles * (dynamicIndex + 1);
		i++
	) {
		info = dynamic[i + 1].split(' ');
		//console.log(particles[(i - (cantParticles * dynamicIndex + 1))])

		particles[i - (cantParticles * dynamicIndex + 1)].move(
			float(info[0]),
			float(info[1]),
			float(info[2]),
			float(info[3])
		);
	}

	dynamicIndex++;
}

var capturer;
let recordAnimation = false;
let recordingTime = 1; // in seconds MAX:9

function setup() {
	loadStaticData();
	loadDynamicData();
	let canvas = createCanvas(canvasSize, canvasSize);
	canvas.id('canvas');
	if (recordAnimation) {
		capturer = new CCapture({
			format: 'webm',
			framerate: 60,
			verbose: true,
			quelity: 100,
		});
	}
	//frameRate(5)
}

function draw() {
	if (recordAnimation && frameCount == 1) {
		capturer.start();
	}

	background(0, 5, 5);
	refresh();

	// NO USAR
	//saveFrames('image' + frameCount , '.png', 1, 5)

	if (recordAnimation) {
		if (frameCount >= 60 * recordingTime) {
			noLoop();
			capturer.stop();
			capturer.save();
			return;
		}
		capturer.capture(document.getElementById('canvas'));
	}
	if (frameCount == 2000) {
		noLoop();
	}
}

class Particle {
	constructor(x, y, vx, vy, d, color) {
		this.x = x * RESIZE_FACTOR;
		this.y = y * RESIZE_FACTOR;
		this.vx = vx;
		this.vy = vy;
		this.d = d * RESIZE_FACTOR;
		this.color = color;
	}

	drawWithColor(color) {
		//noStroke()
		//fill(color)
		//circle(this.x, this.y, this.d)
		//triangle()
		let velocity = createVector(this.vx, this.vy);
		let theta = velocity.heading() + radians(90);
		let r;
		if (this.d == 0) r = 1;
		else {
			r = this.d / 2;
		}

		colorMode(HSB, 360, 100, 100);
		let angle = ((atan2(this.vy, this.vx) + PI) / (2 * PI)) * 360;
		//console.log(angle)
		fill(angle, 100, 100);
		stroke(angle, 60, 100);

		push();
		translate(this.x, this.y);
		rotate(theta);
		beginShape();
		vertex(0, -r * 2);
		vertex(-r, r * 2);
		vertex(r, r * 2);
		endShape(CLOSE);
		pop();
	}

	draw() {
		this.drawWithColor(this.color);
	}

	move(x, y, vx, vy) {
		this.x = x * RESIZE_FACTOR;
		this.y = y * RESIZE_FACTOR;
		this.vx = vx;
		this.vy = vy;
		this.draw();
	}
}
