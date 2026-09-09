export function buildDieselBody(b) {
  const shell = b.part('body')
  b.box(shell, [8.65, .22, 1.94], 'dark', [0, 1.19, 0])
  b.box(shell, [8.55, .08, 2.04], 'steel', [0, 1.34, 0])
  b.box(shell, [2, 1.2, 1.3], 'red', [-2.8, 1.96, 0])
  const hood = b.part('hood')
  b.box(hood, [3.5, .12, 1.4], 'red', [-.05, 2.56, 0])
  for (const side of [-1, 1]) {
    b.box(hood, [3.5, 1.08, .055], 'red', [-.05, 1.99, side * .68])
    b.box(hood, [3.5, .12, .02], 'brass', [-.05, 1.6, side * .715])
    for (let i = 0; i < 7; i++) {
      const x = -1.52 + i * .48
      b.box(hood, [.013, .83, .025], 'dark', [x - .21, 2.02, side * .716])
      b.box(hood, [.035, .15, .04], 'steel', [x + .14, 1.96, side * .73])
      for (const y of [1.7, 2.39]) b.cylinder(hood, .021, .025, 'steel', [x, y, side * .723], 'z', 6)
      for (let j = 0; j < 5; j++) b.box(hood, [.29, .02, .025], 'dark', [x, 2.17 + j * .045, side * .722])
    }
  }
  b.cylinder(hood, .11, .22, 'dark', [-1.4, 2.71, 0])
  // 走道扶手和防滑踏板。
  for (const side of [-1, 1]) {
    for (let i = 0; i < 13; i++) {
      const x = -3.9 + i * .5
      b.cylinder(shell, .023, .68, 'brass', [x, 1.71, side * .96])
    }
    b.cylinder(shell, .026, 6.3, 'brass', [-.9, 2.05, side * .96], 'x')
    for (let i = 0; i < 65; i++) b.box(shell, [.023, .015, .22], 'dark', [-4 + i * .125, 1.39, side * .83])
    for (const x of [-4, 4]) for (let i = 0; i < 3; i++) {
      b.box(shell, [.35, .055, .3], 'steel', [x, .55 + i * .26, side * (1.02 - i * .07)])
    }
  }
  return hood
}

export function buildCab(b) {
  const g = b.part('cab')
  b.box(g, [1.42, .65, 1.68], 'red', [2.5, 1.73, 0])
  b.box(g, [1.54, .12, 1.84], 'dark', [2.5, 2.92, 0])
  for (const x of [1.84, 3.16]) for (const z of [-.78, .78]) b.box(g, [.1, .84, .1], 'red', [x, 2.46, z])
  for (const z of [-.42, .42]) {
    b.box(g, [.045, .57, .65], 'glass', [3.18, 2.49, z])
    b.box(g, [.035, .035, .52], 'dark', [3.21, 2.32, z], [.24, 0, 0])
    b.box(g, [.2, .12, .18], 'steel', [3.22, 2.83, z])
    b.cylinder(g, .055, .03, 'glass', [3.33, 2.83, z], 'x')
  }
  for (const side of [-1, 1]) {
    b.box(g, [.78, .53, .035], 'glass', [2.6, 2.49, side * .81])
    b.box(g, [.07, .7, .04], 'red', [2.16, 2.43, side * .83])
    b.box(g, [.3, 1.22, .04], 'dark', [1.97, 2, side * .84])
    b.box(g, [.035, .13, .05], 'steel', [2.06, 1.96, side * .875])
    b.cylinder(g, .023, .25, 'steel', [2.99, 2.52, side * 1.02], 'z')
    b.box(g, [.13, .2, .045], 'steel', [2.99, 2.52, side * 1.14])
  }
  b.box(g, [.79, .72, 1.38], 'red', [3.62, 1.85, 0])
  b.box(g, [.82, .12, 1.42], 'dark', [3.62, 2.23, 0], [0, 0, -.08])
  for (const z of [-.43, .43]) b.cylinder(g, .07, .06, 'glass', [4.04, 1.9, z], 'x')
  b.cylinder(g, .02, .34, 'steel', [2.65, 3.1, 0])
  for (const z of [-.19, .19]) b.cylinder(g, .055, .23, 'brass', [2.1, 3.03, z], 'x')
}

export function buildCouplers(b) {
  const g = b.part('coupler')
  for (const side of [-1, 1]) {
    b.box(g, [.12, .55, 1.88], 'dark', [side * 4.29, .94, 0])
    b.box(g, [.5, .17, .2], 'steel', [side * 4.48, .99, 0])
    b.box(g, [.22, .26, .32], 'dark', [side * 4.76, .99, .06])
    b.box(g, [.19, .23, .12], 'steel', [side * 4.85, .99, -.12])
    b.cylinder(g, .04, .3, 'brass', [side * 4.76, 1.02, .16])
    for (const z of [-.52, .52]) {
      b.pipe(g, [[side * 4.36, 1.1, z], [side * 4.5, .64, z], [side * 4.58, .68, z * .7]], .035, 'dark')
      b.cylinder(g, .058, .08, 'brass', [side * 4.58, .7, z * .7])
    }
    for (let i = -3; i <= 3; i++) b.box(g, [.02, .055, .18], 'brass', [side * 4.36, 1.13, i * .24])
  }
}
