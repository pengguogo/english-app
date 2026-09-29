import { computed, onMounted, ref } from 'vue'

/**
 * @description 全局 UI 版本状态，负责老版/新版切换与本地持久化。
 */
const UI_VERSION_KEY = 'english-app-ui-version'
const LEGACY_VERSION = 'legacy'
const MODERN_VERSION = 'modern'
const version = ref(LEGACY_VERSION)

let hasHydrated = false

function normalizeVersion(nextVersion) {
  return nextVersion === MODERN_VERSION ? MODERN_VERSION : LEGACY_VERSION
}

function hydrateUiVersion() {
  if (hasHydrated || typeof window === 'undefined') {
    return
  }

  version.value = normalizeVersion(window.localStorage.getItem(UI_VERSION_KEY))
  hasHydrated = true
}

function persistUiVersion(nextVersion) {
  if (typeof window === 'undefined') {
    return
  }

  window.localStorage.setItem(UI_VERSION_KEY, nextVersion)
}

export function useUiVersion() {
  onMounted(hydrateUiVersion)

  const isModern = computed(() => version.value === MODERN_VERSION)

  function setVersion(nextVersion) {
    const normalizedVersion = normalizeVersion(nextVersion)
    version.value = normalizedVersion
    persistUiVersion(normalizedVersion)
  }

  function toggleVersion() {
    setVersion(isModern.value ? LEGACY_VERSION : MODERN_VERSION)
  }

  return {
    version,
    isModern,
    setVersion,
    toggleVersion,
    legacyVersion: LEGACY_VERSION,
    modernVersion: MODERN_VERSION
  }
}
