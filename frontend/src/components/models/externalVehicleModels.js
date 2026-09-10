export const externalVehicleModels = {
  gp7: model('1c89cb9f2c224b78b6fea50f82e042c3', 'EMD GP7 713', 'EMD GP7 Western Pacific 713', 'Jesper Landin', 'JesperLandin'),
  deltic: model('109653d3c26b443fa3e5db322b2b3ef0', 'Class 55 Deltic', 'British Rail Class 55 Deltic', 'timblewee', 'timblewee'),
  cc206: model('b2c2e6c802774f51955a9479a6c2b901', 'GE CC 206', 'GE CM20EMP CC 206', 'pucohensap', 'pucohensap'),
  porsche: model('8568d9d14a994b9cae59499f0dbed21e', 'Porsche 911 Turbo', '1975 Porsche 911 930 Turbo', 'Lionsharp Studios', 'lionsharp'),
  motorcycle: model('79dc00298bf449d7bcfdf6eb32bd15ac', 'Harley Police', 'Harley-Davidson Police Motorcycle', 'Comrade1280', 'comrade1280'),
  fighter: model('a06d6113cfb44a0aa7b8f17106aca9c4', 'F-35A', 'F-35A Lightning II', 'shangus930', 'shangus930'),
  ship: model('aaa41cca946b4a08bc08cf692b7757be', '集装箱船', 'Container Ship', 'RM02', 'RM02'),
  steamEngine: model('dc1797a6d49e4629bf8ca668bd76e0d0', '动画蒸汽机', 'Animated Steam Engine', 'Robert Crosby', 'robmcrosby'),
  turbofan: model('74c6aceed86b4a41aaad3b93afc3e262', '涡扇发动机', 'Turbofan Jet Engine', 'blenderbirb', 'blenderbirb'),
  submarineCutaway: model('70f0532170b44593a5afa88ffc7c0c1e', '半剖潜艇', 'Submarine with Interior', 'martyn169', 'martyn169')
}

function model(uid, title, viewerTitle, author, username) {
  return {
    uid,
    title,
    viewerTitle,
    author,
    authorUrl: `https://sketchfab.com/${username}`
  }
}

export function isExternalVehicle(modelId) {
  return Boolean(externalVehicleModels[modelId])
}
