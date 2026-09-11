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
  submarineCutaway: model('70f0532170b44593a5afa88ffc7c0c1e', '半剖潜艇', 'Submarine with Interior', 'martyn169', 'martyn169'),
  y25Bogie: model('59b720c2616748729910d341d916d6ad', 'Y25 货车转向架', 'Y25 Railway Bogie', 'Víctor Hernández', 'victorhugohc'),
  shibataCoupler: model('9c6e220e0c4f4a9f8cf6489189e534ba', '柴田式车钩', 'Japanese Shibata Train Coupler', 'Syailendra Razak', 'SyailendraRazak'),
  br52Mechanism: model('852f498c26ee457d933f7fbeeb50c8ab', 'BR52 走行机构', 'BR52 Steam Locomotive Animation', 'Ryan.Qin', 'Ryan.Qin'),
  locomotiveCab: model('0383f5afce0945b3bb8309aa77d760cb', '900 号机车驾驶室', 'Locomotive 900 Cab Interior', 'peter54', 'peter54'),
  thsrInterior: model('42b78950675942cb871904f69006783f', '700T 车头内部', 'THSR 700T Locomotive Interior', 'Basic Hsu', 'Hsu.Pei.Ge'),
  railwayWheel: model('8d0b0c78792a48dbb1d78cd6c48b4b00', '铁路轮对', 'Railway Wheelset', 'Mehdi Shahsavan', 'ahmagh2e'),
  aarCoupler: model('074977dc95514e2da29d9a89867e3a1d', 'AAR 关节式车钩', 'AAR Knuckle Coupler', 'josepholiverdev', 'josepholiverdev'),
  switchLever: model('24030034594d4722b3acf486e0f2de9c', '道岔扳杆', 'Railway Switch Lever', 'Blue Scans', 'boat_dfk'),
  railwaySignal: model('f6c97d2382e44f2f942d6fc1c1452e48', '铁路信号机', 'Railway Signal Light', 'turboart', 'turboart')
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
