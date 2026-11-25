import { CatmullRomCurve3, Group, Mesh, MeshStandardMaterial, SphereGeometry, TorusGeometry, TubeGeometry, Vector3 } from 'three'

export function buildMouse(style) {
  const mouse = new Group()
  const head = new Group()
  const paw = new Group()
  const sphere = new SphereGeometry(1, 24, 16)
  const materials = Object.fromEntries(Object.entries(style).map(([key, color]) => [key,
    new MeshStandardMaterial({ color: color.trim(), roughness: 0.72 })
  ]))

  materials.light.emissive.copy(materials.light.color)
  materials.light.emissiveIntensity = .55

  function shape(parent, key, position, scale) {
    const part = new Mesh(sphere, materials[key])
    part.position.set(...position)
    part.scale.set(...scale)
    parent.add(part)
    return part
  }

  shape(mouse, 'shell', [0, -.58, 0], [.48, .46, .34])
  shape(mouse, 'rim', [0, -.64, .25], [.28, .24, .14])
  shape(mouse, 'light', [0, -.68, .39], [.14, .047, .019])
  head.position.y = .19
  mouse.add(head)
  shape(head, 'pink', [0, 0, 0], [.63, .56, .48])

  const ears = [-1, 1].map(side => {
    const ear = new Group()
    ear.position.set(side * .54, .36, -.05)
    head.add(ear)
    shape(ear, 'pink', [0, .14, 0], [.34, .37, .15])
    shape(ear, 'cream', [0, .14, .125], [.23, .25, .045])
    return ear
  })

  const helmetRim = new Mesh(new TorusGeometry(.625, .045, 10, 40), materials.rim)
  helmetRim.position.z = .22
  helmetRim.scale.y = .93
  head.add(helmetRim)
  for (const side of [-1, 1]) {
    shape(head, 'shell', [side * .63, -.04, .13], [.12, .17, .16])
    shape(head, 'light', [side * .66, -.04, .27], [.059, .091, .038])
  }
  shape(head, 'rim', [.59, .44, 0], [.028, .21, .028])
  shape(head, 'light', [.59, .63, 0], [.074, .074, .074])

  const eyes = [-1, 1].map(side => {
    const eye = new Group()
    eye.position.set(side * .23, .055, .437)
    head.add(eye)
    shape(eye, 'ink', [0, 0, 0], [.072, .091, .045])
    shape(eye, 'cream', [.018, .028, .038], [.022, .025, .012])
    return eye
  })
  for (const side of [-1, 1]) {
    shape(head, 'rose', [side * .37, -.14, .38], [.105, .066, .034])
  }
  const nose = shape(head, 'rose', [0, -.16, .51], [.08, .057, .055])
  const mouth = shape(head, 'ink', [0, -.27, .457], [.09, .045, .037])
  shape(mouse, 'cream', [-.3, -.63, .29], [.12, .12, .11])
  paw.position.set(.3, -.55, .21)
  mouse.add(paw)
  shape(paw, 'pink', [.08, -.11, .07], [.096, .19, .09])
  shape(paw, 'cream', [.08, -.24, .07], [.11, .11, .10])

  const curve = new CatmullRomCurve3([[.28, -.9, -.05], [.73, -.92, -.13], [.96, -.69, -.15],
    [.84, -.45, -.11], [.7, -.5, -.08]].map(point => new Vector3(...point)))
  const tail = new Mesh(new TubeGeometry(curve, 24, .037, 8), materials.rose)
  mouse.add(tail)

  function dispose() {
    sphere.dispose()
    tail.geometry.dispose()
    helmetRim.geometry.dispose()
    Object.values(materials).forEach(material => material.dispose())
  }
  return { mouse, head, paw, eyes, ears, nose, mouth, tail, equipmentLight: materials.light, dispose }
}
