export const DEFAULT_LIST_PAGE_SIZE = 20

export function getVisibleItems(items, visibleCount) {
  return items.slice(0, Math.max(0, visibleCount))
}

export function getNextVisibleCount(current, total, pageSize = DEFAULT_LIST_PAGE_SIZE) {
  return Math.min(total, current + pageSize)
}
