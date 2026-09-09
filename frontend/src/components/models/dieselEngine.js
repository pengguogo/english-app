export function buildEngine(b) {
  const g = b.part('engine')
  b.box(g, [2.8, .4, .65], 'dark', [-.35, 1.58, 0])
  for (const side of [-1, 1]) {
    for (let i = 0; i < 8; i++) {
      const x = -1.54 + i * .34
      b.box(g, [.27, .48, .28], 'steel', [x, 1.98, side * .26], [side * .3, 0, 0])
      b.box(g, [.29, .09, .32], 'dark', [x, 2.23, side * .33])
      for (const dx of [-.09, .09]) b.cylinder(g, .025, .035, 'brass', [x + dx, 2.285, side * .33], 'y', 6)
      b.pipe(g, [[x, 1.62, side * .4], [x + .06, 1.96, side * .53], [x, 2.2, side * .4]], .019, 'brass')
    }
    b.cylinder(g, .065, 2.85, 'wood', [-.35, 1.85, side * .5], 'x')
    b.pipe(g, [[-1.7, 1.8, side * .5], [-1.85, 2.3, side * .4], [-1.8, 2.4, 0]], .075, 'steel')
  }
  b.cylinder(g, .34, .6, 'steel', [1.38, 1.86, 0], 'x')
  for (let i = 0; i < 12; i++) {
    const a = i * Math.PI / 6
    b.box(g, [.55, .04, .04], 'dark', [1.38, 1.86 + Math.cos(a) * .34, Math.sin(a) * .34])
  }
  b.torus(g, .18, .065, 'steel', [-1.7, 2.32, 0])
  b.cylinder(g, .095, .35, 'dark', [-1.7, 2.55, 0])
}

export function buildCooling(b) {
  const g = b.part('cooling')
  for (const x of [-3.25, -2.35]) {
    b.cylinder(g, .4, .09, 'dark', [x, 2.64, 0], 'y', 48)
    for (let i = 0; i < 8; i++) {
      const angle = i * Math.PI / 4
      b.box(g, [.3, .03, .12], 'steel', [x + Math.cos(angle) * .2, 2.7, Math.sin(angle) * .2], [.12, -angle, 0])
    }
    b.cylinder(g, .085, .08, 'brass', [x, 2.71, 0])
    for (const radius of [.17, .27, .39]) b.torus(g, radius, .012, 'steel', [x, 2.76, 0], [Math.PI / 2, 0, 0])
    for (let i = -3; i <= 3; i++) {
      const z = i * .095
      b.box(g, [2 * Math.sqrt(.39 ** 2 - z ** 2), .015, .012], 'steel', [x, 2.77, z])
    }
  }
  for (const z of [-.67, .67]) {
    b.box(g, [1.9, .9, .04], 'dark', [-2.8, 2.06, z])
    for (let i = 0; i < 22; i++) b.box(g, [.045, .82, .06], 'steel', [-3.67 + i * .083, 2.06, z])
  }
}
