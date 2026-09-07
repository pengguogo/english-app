<script setup>
defineProps({ step: { type: Number, default: 0 }, total: { type: Number, default: 1 }, calm: Boolean })
</script>

<template>
  <div class="train-track" :class="{ calm }" role="progressbar" aria-label="本站旅程" :aria-valuenow="step" :aria-valuemax="total" :aria-valuemin="0">
    <div class="rail"><div class="engine-position" :style="{ left: `${Math.min(100, step / Math.max(1, total) * 100)}%` }">
      <span class="engine" aria-hidden="true"><i class="chimney"></i><i class="cab"></i><i class="wheel one"></i><i class="wheel two"></i></span>
    </div></div>
    <div class="track-labels"><span>出发</span><span>{{ step }} / {{ total }} 小站</span><span>到站</span></div>
  </div>
</template>

<style scoped>
.train-track { padding: var(--space-8) var(--space-5) var(--space-3); }
.rail { position: relative; height: 8px; border-block: 2px solid var(--text-secondary); background: repeating-linear-gradient(90deg, var(--border-light) 0 6px, var(--bg-card) 6px 14px); }
.engine-position { position: absolute; bottom: 9px; width: 46px; margin-left: -23px; }
.engine { position: relative; display: block; width: 42px; height: 21px; background: var(--subject-math); border-radius: 5px 10px 3px 3px; }
.cab { position: absolute; left: 2px; top: -12px; width: 19px; height: 17px; border: 5px solid var(--subject-math); border-top: 5px solid var(--color-warning); background: var(--bg-card); border-radius: 3px; }
.chimney { position: absolute; right: 6px; top: -9px; width: 7px; height: 12px; background: var(--text-secondary); }
.wheel { position: absolute; bottom: -5px; width: 11px; height: 11px; border-radius: 50%; background: var(--text-primary); border: 2px solid var(--bg-card); }.one { left: 4px; }.two { right: 4px; }
.track-labels { display: flex; justify-content: space-between; color: var(--text-secondary); font-size: var(--text-xs); margin-top: var(--space-2); }
@media(prefers-reduced-motion: no-preference) { .train-track:not(.calm) .engine-position { transition: left .6s ease; } }
</style>
