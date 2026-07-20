/**
 * 小砾工程队单词图片映射
 * key = 单词(小写), value = 图片导入
 * LessonView 中当 item.image 存在时优先展示图片
 */
import excavatorImg from './excavator.jpg'
import bulldozerImg from './bulldozer.jpg'
import craneImg from './crane.jpg'
import cementImg from './cement.jpg'
import hammerImg from './hammer.jpg'
import wrenchImg from './wrench.jpg'
import drillImg from './drill.jpg'
import hardHatImg from './hard-hat.jpg'
import brickImg from './brick.jpg'
import steelImg from './steel.jpg'
import woodImg from './wood.jpg'
import concreteImg from './concrete.jpg'
import pipeImg from './pipe.jpg'
import nailImg from './nail.jpg'
import screwImg from './screw.jpg'
import paintImg from './paint.jpg'

const rubbleCrewImages = {
  excavator: excavatorImg,
  bulldozer: bulldozerImg,
  crane: craneImg,
  cement: cementImg,
  hammer: hammerImg,
  wrench: wrenchImg,
  drill: drillImg,
  'hard hat': hardHatImg,
  brick: brickImg,
  steel: steelImg,
  wood: woodImg,
  concrete: concreteImg,
  pipe: pipeImg,
  nail: nailImg,
  screw: screwImg,
  paint: paintImg
}

/**
 * 根据单词获取图片 URL
 * @param {string} word - 单词文本
 * @returns {string|null} 图片 URL 或 null
 */
export function getWordImage(word) {
  return rubbleCrewImages[word?.toLowerCase()] || null
}