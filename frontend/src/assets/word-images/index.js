/**
 * 单词图片映射（统一入口）
 * 包含: 汪汪队主题 24 张 + 小砾工程队 8 张（新建）= 32 张新图片
 *       + 旧 rubble-crew 16 张 = 共 48 个单词
 */

// === 汪汪队主题: 认识狗狗 (8) ===
import chaseImg from './chase.jpg'
import marshallImg from './marshall.jpg'
import skyeImg from './skye.jpg'
import rubbleImg from './rubble.jpg'
import zumaImg from './zuma.jpg'
import rockyImg from './rocky.jpg'
import puppyImg from './puppy.jpg'
import teamImg from './team.jpg'

// === 汪汪队主题: 狗狗的本领 (8) ===
import policeImg from './police.jpg'
import fireImg from './fire-word.jpg'
import flyImg from './fly.jpg'
import buildImg from './build.jpg'
import swimImg from './swim.jpg'
import recycleImg from './recycle.jpg'
import rescueImg from './rescue.jpg'
import helpImg from './help.jpg'

// === 小砾工程队: 工程家族狗狗 (8) ===
import dozerImg from './dozer.jpg'
import pipesImg from './pipes.jpg'
import boltImg from './bolt.jpg'
import mixerImg from './mixer.jpg'
import nailsImg from './nails.jpg'
import wrenchCorgiImg from './wrench-corgi.jpg'
import hammerBoxerImg from './hammer-boxer.jpg'
import crankImg from './crank.jpg'

// === 小砾工程队: 狗狗的工具 (8) ===
import dozerBladeImg from './dozer-blade.jpg'
import pipeWrenchImg from './pipe-wrench.jpg'
import powerDrillImg from './power-drill-word.jpg'
import cementMixerImg from './cement-mixer.jpg'
import nailGunImg from './nail-gun.jpg'
import adjustableWrenchImg from './adjustable-wrench.jpg'
import sledgehammerImg from './sledgehammer.jpg'
import chainHoistImg from './chain-hoist.jpg'

// === 旧 rubble-crew 图片 (16) ===
import excavatorImg from '../rubble-crew/excavator.jpg'
import bulldozerImg from '../rubble-crew/bulldozer.jpg'
import craneImg from '../rubble-crew/crane.jpg'
import cementImg from '../rubble-crew/cement.jpg'
import hammerImg from '../rubble-crew/hammer.jpg'
import wrenchImg from '../rubble-crew/wrench.jpg'
import drillImg from '../rubble-crew/drill.jpg'
import hardHatImg from '../rubble-crew/hard-hat.jpg'
import brickImg from '../rubble-crew/brick.jpg'
import steelImg from '../rubble-crew/steel.jpg'
import woodImg from '../rubble-crew/wood.jpg'
import concreteImg from '../rubble-crew/concrete.jpg'
import pipeImg from '../rubble-crew/pipe.jpg'
import nailImg from '../rubble-crew/nail.jpg'
import screwImg from '../rubble-crew/screw.jpg'
import paintImg from '../rubble-crew/paint.jpg'

const wordImages = {
  // 汪汪队: 认识狗狗
  chase: chaseImg,
  marshall: marshallImg,
  skye: skyeImg,
  rubble: rubbleImg,
  zuma: zumaImg,
  rocky: rockyImg,
  puppy: puppyImg,
  team: teamImg,

  // 汪汪队: 狗狗的本领
  police: policeImg,
  fire: fireImg,
  fly: flyImg,
  build: buildImg,
  swim: swimImg,
  recycle: recycleImg,
  rescue: rescueImg,
  help: helpImg,

  // 小砾工程队: 工程家族狗狗
  dozer: dozerImg,
  pipes: pipesImg,
  bolt: boltImg,
  mixer: mixerImg,
  nails: nailsImg,
  wrench: wrenchCorgiImg,
  hammer: hammerBoxerImg,
  crank: crankImg,

  // 小砾工程队: 狗狗的工具
  'dozer blade': dozerBladeImg,
  'pipe wrench': pipeWrenchImg,
  'power drill': powerDrillImg,
  'cement mixer': cementMixerImg,
  'nail gun': nailGunImg,
  'adjustable wrench': adjustableWrenchImg,
  sledgehammer: sledgehammerImg,
  'chain hoist': chainHoistImg,

  // 旧 rubble-crew
  excavator: excavatorImg,
  bulldozer: bulldozerImg,
  crane: craneImg,
  cement: cementImg,
  'hard hat': hardHatImg,
  brick: brickImg,
  steel: steelImg,
  wood: woodImg,
  concrete: concreteImg,
  pipe: pipeImg,
  nail: nailImg,
  screw: screwImg,
  paint: paintImg,
}

/**
 * 根据单词获取图片 URL
 * @param {string} word - 单词文本
 * @returns {string|null} 图片 URL 或 null
 */
export function getWordImage(word) {
  return wordImages[word?.toLowerCase()] || null
}