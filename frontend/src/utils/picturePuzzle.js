import { shuffle } from './kidsGames.js'

export function createPicturePieces(size, random = Math.random) {
  const pieces = shuffle(Array.from({ length: size * size }, (_, index) => index), random)
  if (pieces.every((value, index) => value === index)) [pieces[0], pieces[1]] = [pieces[1], pieces[0]]
  return pieces
}

export function swapPicturePieces(pieces, first, second) {
  const result = [...pieces]
  ;[result[first], result[second]] = [result[second], result[first]]
  return result
}

export function hintPicturePieces(pieces) {
  const target = pieces.findIndex((value, index) => value !== index)
  return target === -1 ? [...pieces] : swapPicturePieces(pieces, target, pieces.indexOf(target))
}
