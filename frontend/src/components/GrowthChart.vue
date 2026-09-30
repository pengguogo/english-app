<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: { type: String, required: true },
  unit: { type: String, required: true },
  field: { type: String, required: true },
  records: { type: Array, required: true }
})

const points = computed(() => props.records
  .filter(item => item[props.field] != null)
  .map(item => ({ ...item, value: Number(item[props.field]), time: Date.parse(`${item.measuredAt}T00:00:00`) })))
const graph = computed(() => {
  const values = points.value.map(point => point.value)
  const times = points.value.map(point => point.time)
  if (!values.length) return { dots: [], path: '', min: 0, max: 0 }
  const min = Math.min(...values)
  const max = Math.max(...values)
  const span = Math.max(max - min, props.field === 'heightCm' ? 2 : 0.5)
  const low = min - span * 0.2
  const high = max + span * 0.2
  const first = Math.min(...times)
  const last = Math.max(...times)
  const dots = points.value.map(point => ({
    ...point,
    x: first === last ? 180 : 32 + (point.time - first) / (last - first) * 296,
    y: 144 - (point.value - low) / (high - low) * 112
  }))
  return { dots, path: dots.map((dot, index) => `${index ? 'L' : 'M'} ${dot.x} ${dot.y}`).join(' '),
    min: min.toFixed(props.field === 'heightCm' ? 1 : 2),
    max: max.toFixed(props.field === 'heightCm' ? 1 : 2) }
})
</script>

<template>
  <section class="chart-card" :aria-label="`${title}图`">
    <header><h3>{{ title }}</h3><span>{{ unit }}</span></header>
    <p v-if="!points.length" class="empty">添加测量记录后，这里会显示轨迹。</p>
    <template v-else>
      <svg viewBox="0 0 360 180" role="img" :aria-label="`${title}从 ${graph.min} 到 ${graph.max}${unit}，共 ${points.length} 次测量`">
        <line x1="32" y1="144" x2="328" y2="144" class="axis" />
        <line x1="32" y1="32" x2="32" y2="144" class="axis" />
        <text x="2" y="36">{{ graph.max }}</text>
        <text x="2" y="146">{{ graph.min }}</text>
        <path v-if="graph.dots.length > 1" :d="graph.path" class="trend" />
        <circle v-for="dot in graph.dots" :key="dot.id" :cx="dot.x" :cy="dot.y" r="5" class="dot">
          <title>{{ dot.measuredAt }}：{{ dot.value }} {{ unit }}</title>
        </circle>
      </svg>
      <div class="range"><span>{{ points[0].measuredAt }}</span><span>{{ points[points.length - 1].measuredAt }}</span></div>
    </template>
  </section>
</template>

<style scoped>
.chart-card { background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-md); padding: var(--space-4); }
header, .range { display: flex; align-items: center; justify-content: space-between; }
header h3 { margin: 0; font-size: var(--text-base); }
header span, .range, .empty { color: var(--text-secondary); font-size: var(--text-sm); }
svg { display: block; width: 100%; margin-top: var(--space-4); overflow: visible; }
svg text { fill: var(--text-secondary); font-size: 10px; }
.axis { stroke: var(--border-light); stroke-width: 2; }
.trend { fill: none; stroke: var(--color-primary); stroke-width: 3; stroke-linecap: round; stroke-linejoin: round; }
.dot { fill: var(--color-primary); stroke: var(--bg-card); stroke-width: 2; }
.range { margin-left: 9%; }
</style>
