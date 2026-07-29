/**
 * 单元配图映射：构建时自动收集 unit-{id}.webp，避免手工维护 99 条 import。
 */
const imageModules = import.meta.glob('../assets/units/unit-*.webp', {
  eager: true,
  query: '?url',
  import: 'default'
})

const unitImages = Object.fromEntries(
  Object.entries(imageModules).map(([path, url]) => {
    const id = Number(path.match(/unit-(\d+)\.webp$/)?.[1])
    return [id, url]
  })
)

export function getUnitImage(unitId) {
  return unitImages[Number(unitId)] || ''
}
