const fallback = '/images/picturebooks/train-station.jpg'

export function picturebookCover(content) {
  const source = content?.items?.find((item) => item.image && !item.image.includes('train-station'))?.image
  if (!source) return fallback
  return source.startsWith('/') ? source : `/images/${source}.jpg`
}
