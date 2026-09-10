export const externalTrainModels = {
  gp7: {
    uid: '1c89cb9f2c224b78b6fea50f82e042c3',
    title: 'EMD GP7 713',
    viewerTitle: 'EMD GP7 Western Pacific 713',
    author: 'Jesper Landin',
    authorUrl: 'https://sketchfab.com/JesperLandin'
  },
  deltic: {
    uid: '109653d3c26b443fa3e5db322b2b3ef0',
    title: 'Class 55 Deltic',
    viewerTitle: 'British Rail Class 55 Deltic',
    author: 'timblewee',
    authorUrl: 'https://sketchfab.com/timblewee'
  },
  cc206: {
    uid: 'b2c2e6c802774f51955a9479a6c2b901',
    title: 'GE CC 206',
    viewerTitle: 'GE CM20EMP CC 206',
    author: 'pucohensap',
    authorUrl: 'https://sketchfab.com/pucohensap'
  }
}

export function isExternalTrain(model) {
  return Boolean(externalTrainModels[model])
}
