<script setup>
import { ref } from 'vue'
import AppButton from '../components/AppButton.vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import ModelViewer from '../components/models/ModelViewer.vue'
import { externalVehicleModels, isExternalVehicle } from '../components/models/externalVehicleModels'
const router = useRouter()
const model = ref('gp7')

const modelInfo = {
  steam: {
    eyebrow: '交通工具 · 蒸汽时代',
    title: '蒸汽火车',
    english: 'Steam locomotive',
    description: '一座可以转动的小小火车展台。看看圆圆的锅炉，再找找驾驶室和大车轮。'
  },
  diesel: {
    eyebrow: '交通工具 · 机械细节探索',
    title: '燃油火车',
    english: 'Diesel-electric locomotive',
    description: '从整辆机车，走近一颗螺栓。打开机罩，看柴油机；靠近底盘，探索轮组和悬挂。'
  },
  gp7: {
    eyebrow: '真实车型 · 1950 年代',
    title: externalVehicleModels.gp7.title,
    english: 'Diesel-electric locomotive',
    description: '观察真实 GP7 柴油电力机车的驾驶室、长机罩、转向架和车身涂装。'
  },
  deltic: {
    eyebrow: '真实车型 · 英国铁路',
    title: externalVehicleModels.deltic.title,
    english: 'Diesel locomotive',
    description: '观察英国铁路蓝色 Deltic 机车独特的流线型车头、双驾驶室与六轴转向架。'
  },
  cc206: {
    eyebrow: '真实车型 · 印度尼西亚铁路',
    title: externalVehicleModels.cc206.title,
    english: 'Diesel-electric locomotive',
    description: '观察印度尼西亚铁路 CC 206 的宽大驾驶室、长机罩、六轴底盘和红白涂装。'
  },
  porsche: {
    eyebrow: '公路交通 · 经典跑车',
    title: externalVehicleModels.porsche.title,
    english: 'Sports car',
    description: '观察经典 1975 年保时捷 911 Turbo 的圆形车灯、宽体轮拱、尾翼和驾驶舱。'
  },
  motorcycle: {
    eyebrow: '公路交通 · 警用摩托',
    title: externalVehicleModels.motorcycle.title,
    english: 'Police motorcycle',
    description: '观察警用摩托车的发动机、车架、悬挂、制动盘、警灯和边箱。'
  },
  fighter: {
    eyebrow: '航空交通 · 喷气式飞机',
    title: externalVehicleModels.fighter.title,
    english: 'Jet aircraft',
    description: '从不同角度观察 F-35A 的机翼、进气道、驾驶舱、尾翼和起落架。'
  },
  ship: {
    eyebrow: '水上交通 · 远洋货运',
    title: externalVehicleModels.ship.title,
    english: 'Container ship',
    description: '观察大型货船的船艏、驾驶台、甲板集装箱、吊装区域和船尾结构。'
  },
  steamEngine: {
    eyebrow: '内部结构 · 动力机械',
    title: externalVehicleModels.steamEngine.title,
    english: 'Animated steam engine',
    description: '播放动画，从开放结构中观察活塞、连杆和飞轮怎样把往复运动变成旋转运动。'
  },
  turbofan: {
    eyebrow: '内部结构 · 航空动力',
    title: externalVehicleModels.turbofan.title,
    english: 'Turbofan jet engine',
    description: '沿发动机轴线观察风扇、压气机、燃烧核心和涡轮的层层排列。'
  },
  submarineCutaway: {
    eyebrow: '内部结构 · 水下交通',
    title: externalVehicleModels.submarineCutaway.title,
    english: 'Submarine cutaway',
    description: '通过切开的艇体观察基础潜艇内部的舱室、通道、设备与人员活动空间。'
  },
  y25Bogie: {
    eyebrow: '火车零件 · 走行与制动',
    title: externalVehicleModels.y25Bogie.title,
    english: 'Freight wagon bogie',
    description: '单独观察轮对、轴箱、悬挂、转向架构架和基础制动装置怎样组合在一起。'
  },
  shibataCoupler: {
    eyebrow: '火车零件 · 车辆连接',
    title: externalVehicleModels.shibataCoupler.title,
    english: 'Shibata coupler',
    description: '近距离观察电力列车常用车钩的连接面、定位结构和机械锁闭部位。'
  },
  br52Mechanism: {
    eyebrow: '火车零件 · 蒸汽传动',
    title: externalVehicleModels.br52Mechanism.title,
    english: 'Steam locomotive running gear',
    description: '播放动画，观察蒸汽机车车轮、主连杆和侧连杆之间的同步运动关系。'
  },
  locomotiveCab: {
    eyebrow: '火车零件 · 驾驶与控制',
    title: externalVehicleModels.locomotiveCab.title,
    english: 'Locomotive cab interior',
    description: '进入机车驾驶室，观察操纵台、仪表、控制手柄和司机工作空间的布局。'
  },
  thsrInterior: {
    eyebrow: '火车零件 · 高速列车',
    title: externalVehicleModels.thsrInterior.title,
    english: 'High-speed train interior',
    description: '观察 700T 高速列车车头内部设备、驾驶区域和车体空间之间的关系。'
  },
  railwayWheel: {
    eyebrow: '火车零件 · 轮轨系统',
    title: externalVehicleModels.railwayWheel.title,
    english: 'Railway wheelset',
    description: '近距离观察车轮、轮缘、踏面和车轴，理解列车如何沿钢轨保持方向。'
  }
}
</script>

<template>
  <main class="models-page">
    <BackBar title="3D 交通工具馆" @back="router.push('/')" />
    <header class="intro">
      <div class="model-tabs" aria-label="选择交通工具模型">
        <AppButton :variant="model === 'steam' ? 'primary' : 'ghost'" :aria-pressed="model === 'steam'" @click="model = 'steam'">01 蒸汽火车</AppButton>
        <AppButton :variant="model === 'diesel' ? 'primary' : 'ghost'" :aria-pressed="model === 'diesel'" @click="model = 'diesel'">02 燃油火车</AppButton>
        <AppButton :variant="model === 'gp7' ? 'primary' : 'ghost'" :aria-pressed="model === 'gp7'" @click="model = 'gp7'">03 GP7 真实机车</AppButton>
        <AppButton :variant="model === 'deltic' ? 'primary' : 'ghost'" :aria-pressed="model === 'deltic'" @click="model = 'deltic'">04 Deltic</AppButton>
        <AppButton :variant="model === 'cc206' ? 'primary' : 'ghost'" :aria-pressed="model === 'cc206'" @click="model = 'cc206'">05 CC 206</AppButton>
        <AppButton :variant="model === 'porsche' ? 'primary' : 'ghost'" :aria-pressed="model === 'porsche'" @click="model = 'porsche'">06 911 Turbo</AppButton>
        <AppButton :variant="model === 'motorcycle' ? 'primary' : 'ghost'" :aria-pressed="model === 'motorcycle'" @click="model = 'motorcycle'">07 摩托车</AppButton>
        <AppButton :variant="model === 'fighter' ? 'primary' : 'ghost'" :aria-pressed="model === 'fighter'" @click="model = 'fighter'">08 F-35A</AppButton>
        <AppButton :variant="model === 'ship' ? 'primary' : 'ghost'" :aria-pressed="model === 'ship'" @click="model = 'ship'">09 集装箱船</AppButton>
        <AppButton :variant="model === 'steamEngine' ? 'primary' : 'ghost'" :aria-pressed="model === 'steamEngine'" @click="model = 'steamEngine'">10 蒸汽机内部</AppButton>
        <AppButton :variant="model === 'turbofan' ? 'primary' : 'ghost'" :aria-pressed="model === 'turbofan'" @click="model = 'turbofan'">11 涡扇内部</AppButton>
        <AppButton :variant="model === 'submarineCutaway' ? 'primary' : 'ghost'" :aria-pressed="model === 'submarineCutaway'" @click="model = 'submarineCutaway'">12 潜艇剖面</AppButton>
        <AppButton :variant="model === 'y25Bogie' ? 'primary' : 'ghost'" :aria-pressed="model === 'y25Bogie'" @click="model = 'y25Bogie'">13 转向架</AppButton>
        <AppButton :variant="model === 'shibataCoupler' ? 'primary' : 'ghost'" :aria-pressed="model === 'shibataCoupler'" @click="model = 'shibataCoupler'">14 柴田式车钩</AppButton>
        <AppButton :variant="model === 'br52Mechanism' ? 'primary' : 'ghost'" :aria-pressed="model === 'br52Mechanism'" @click="model = 'br52Mechanism'">15 车轮连杆</AppButton>
        <AppButton :variant="model === 'locomotiveCab' ? 'primary' : 'ghost'" :aria-pressed="model === 'locomotiveCab'" @click="model = 'locomotiveCab'">16 机车驾驶室</AppButton>
        <AppButton :variant="model === 'thsrInterior' ? 'primary' : 'ghost'" :aria-pressed="model === 'thsrInterior'" @click="model = 'thsrInterior'">17 高铁车头内部</AppButton>
        <AppButton :variant="model === 'railwayWheel' ? 'primary' : 'ghost'" :aria-pressed="model === 'railwayWheel'" @click="model = 'railwayWheel'">18 铁路轮对</AppButton>
      </div>
      <span class="eyebrow">{{ modelInfo[model].eyebrow }}</span>
      <h1>{{ modelInfo[model].title }} <span>{{ modelInfo[model].english }}</span></h1>
      <p>{{ modelInfo[model].description }}</p>
    </header>
    <ModelViewer :key="model" :model="model" />
    <aside class="note">
      <h2>试着换个角度</h2>
      <p v-if="model === 'steamEngine'">播放模型动画，沿着活塞杆找到连杆和飞轮，观察各零件怎样同步运动。</p>
      <p v-else-if="model === 'turbofan'">从正面找到大风扇，再转到侧面，沿中心轴观察空气经过的多级结构。</p>
      <p v-else-if="model === 'submarineCutaway'">从切开的侧面进入艇内，比较不同舱室的用途和有限空间中的设备布局。</p>
      <p v-else-if="model === 'y25Bogie'">从侧面寻找弹簧和制动闸瓦，再从上方观察两组轮对如何安装在同一构架中。</p>
      <p v-else-if="model === 'shibataCoupler'">把镜头转向车钩正面和背面，比较连接面、锁闭部位与车体安装端的形状。</p>
      <p v-else-if="model === 'br52Mechanism'">播放动画后盯住一个车轮，沿主连杆观察动力怎样传递给其他动轮。</p>
      <p v-else-if="model === 'locomotiveCab'">把视角移进驾驶室，寻找速度表、压力表、控制手柄和司机座椅。</p>
      <p v-else-if="model === 'thsrInterior'">从车头侧面和上方观察，比较驾驶空间与后方设备区域的布局。</p>
      <p v-else-if="model === 'railwayWheel'">放大轮缘与踏面，观察车轮接触钢轨的位置以及两轮如何由车轴固定。</p>
      <p v-else-if="isExternalVehicle(model)">拖动旋转模型并放大关键结构，观察真实交通工具的造型、连接方式与使用痕迹。</p>
      <p v-else-if="model === 'diesel'">先选择“轮组与悬挂”，放大观察轴箱上的六角螺栓。再选择“柴油机”，打开机罩寻找两排气缸盖和细细的管线。</p>
      <p v-else>从侧面观察车轮和连杆，从正面看看车灯，从上方找一找装煤的车厢。</p>
      <span v-if="isExternalVehicle(model)">模型：{{ externalVehicleModels[model].author }}，Creative Commons Attribution 4.0；写实外观展示，内部结构以作者实际建模内容为准。</span>
      <span v-else>{{ model === 'diesel' ? '原创柴油电力机车教学模型，参考真实机械结构；内部布局与比例经过简化，并非特定车型的工程复刻。' : '风格化展示模型，比例与细节经过简化。' }}</span>
    </aside>
  </main>
</template>

<style scoped>
.model-tabs { display: flex; flex-wrap: wrap; gap: var(--space-2); margin-bottom: var(--space-5); padding-bottom: var(--space-2); }
.model-tabs :deep(button) { flex: 0 0 auto; }
.models-page { padding: var(--space-4); }
.intro { padding: var(--space-4) 0 var(--space-6); }
.eyebrow { font-size: var(--text-xs); font-weight: var(--font-bold); letter-spacing: .12em; color: var(--color-primary-hover); }
h1 { font-size: clamp(26px, 4vw, 36px); margin: var(--space-3) 0; color: var(--text-primary); }
h1 span { display: inline-block; font-size: var(--text-base); font-weight: var(--font-normal); color: var(--text-secondary); }
p { color: var(--text-secondary); line-height: 1.8; }
.note { margin-top: var(--space-6); padding: var(--space-5); border-radius: var(--radius-md); background: var(--bg-card); }
.note h2 { font-size: var(--text-base); margin-bottom: var(--space-2); }
.note span { display: block; margin-top: var(--space-3); font-size: var(--text-xs); color: var(--text-secondary); }
@media (max-width: 720px) { .model-tabs { flex-wrap: nowrap; overflow-x: auto; scrollbar-width: thin; } }
</style>
