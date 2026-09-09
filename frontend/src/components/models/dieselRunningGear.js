// 六轴转向架：轮缘、轴箱盖、制动块、螺旋弹簧和牵引电机均为立体几何。
export function buildRunningGear(b) {
  const g = b.part('bogie')
  for (const center of [-2.55, 2.55]) {
    b.box(g, [2.6, .22, 1.4], 'dark', [center, .94, 0])
    for (const x of [center - .87, center, center + .87]) {
      b.cylinder(g, .095, 1.9, 'steel', [x, .62, 0], 'z')
      b.cylinder(g, .23, .78, 'dark', [x + .18, .69, 0], 'z')
      for (const side of [-1, 1]) {
        const z = side * .86
        b.cylinder(g, .47, .18, 'steel', [x, .62, z], 'z', 48)
        b.cylinder(g, .5, .045, 'dark', [x, .62, z - side * .09], 'z', 48)
        b.cylinder(g, .36, .195, 'dark', [x, .62, z], 'z', 40)
        b.cylinder(g, .19, .23, 'steel', [x, .62, z], 'z')
        b.box(g, [.32, .3, .17], 'dark', [x, .63, side * 1.03])
        b.cylinder(g, .115, .05, 'steel', [x, .63, side * 1.13], 'z')
        for (let i = 0; i < 6; i++) {
          const angle = i * Math.PI / 3
          b.cylinder(g, .022, .04, 'brass', [x + Math.cos(angle) * .085, .63 + Math.sin(angle) * .085, side * 1.17], 'z', 6)
        }
        for (const dx of [-.31, .31]) {
          b.box(g, [.1, .26, .17], 'wood', [x + dx, .5, z])
          b.cylinder(g, .025, .33, 'steel', [x + dx, .75, side * 1.04])
        }
      }
    }
    for (const side of [-1, 1]) {
      b.box(g, [2.62, .16, .15], 'dark', [center, .91, side * 1.03])
      for (const dx of [-.44, .44]) {
        const points = Array.from({ length: 81 }, (_, i) => [center + dx + .095 * Math.cos(i * Math.PI / 5), .7 + i * .004, side * 1.05 + .095 * Math.sin(i * Math.PI / 5)])
        b.pipe(g, points, .025, 'steel')
        b.box(g, [.3, .055, .3], 'dark', [center + dx, 1.04, side * 1.05])
      }
    }
  }
  const tank = b.part('fuel')
  b.box(tank, [2.05, .64, 1.52], 'dark', [0, .76, 0])
  for (const x of [-.72, .72]) b.box(tank, [.07, .67, 1.55], 'steel', [x, .76, 0])
  for (const z of [-.8, .8]) {
    b.cylinder(tank, .13, .06, 'brass', [.65, .92, z], 'z')
    b.box(tank, [.3, .07, .07], 'steel', [.65, .94, z * 1.05])
    b.pipe(tank, [[.65, .95, z], [.85, 1.15, z], [1.1, 1.2, z]], .028, 'dark')
  }
}
