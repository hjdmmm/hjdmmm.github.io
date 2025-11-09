<script lang="ts" setup>
import {Separator} from "@/components/ui/separator";
import Color, {type ColorInstance, type ColorLike} from "color";
import {inject, onMounted, ref} from 'vue';
import {colorPickerKey, type Context} from "./utils.ts";

const context = inject(colorPickerKey) as Context;
const {hue, saturation, lightness, alpha} = context;

const props = withDefaults(
    defineProps<{
      commonColors?: ColorLike[],
      recentColorsCount?: number,
    }>(),
    {
      commonColors: () => [
        '#FF6B6B', '#4ECDC4', '#45B7D1', '#FFBE0B', '#FB5607',
        '#8338EC', '#3A86FF', '#06D6A0', '#118AB2', '#073B4C',
      ],
      recentColorsCount: 5,
    }
);

const emits = defineEmits(['click']);

const commonColors = ref<ColorInstance[]>([]);
const recentColors = ref<ColorInstance[]>([]);

const RECENT_COLORS_KEY = 'recentColors';

const loadRecentColors = () => {
  try {
    const stored = localStorage.getItem(RECENT_COLORS_KEY);
    if (stored) {
      const hexList: string[] = JSON.parse(stored);
      recentColors.value = hexList.map(hex => Color(hex));
    }
  } catch (e) {
    console.error('Failed to load recent colors from localStorage', e);
    recentColors.value = [];
  }
};

const saveRecentColors = (color: ColorInstance) => {
  const filtered = recentColors.value.filter(c => c !== color);
  recentColors.value = [color, ...filtered].slice(0, props.recentColorsCount);
  const hexList = recentColors.value.map(c => c.hex());
  try {
    localStorage.setItem(RECENT_COLORS_KEY, JSON.stringify(hexList));
  } catch (e) {
    console.error('Failed to save recent colors to localStorage', e);
  }
};

const handleColorSelect = (color: ColorInstance) => {
  emits('click', color);

  hue.value = color.hue();
  saturation.value = color.saturationl();
  lightness.value = color.lightness();
  alpha.value = Math.round(color.alpha() * 100);
  saveRecentColors(color);
};

onMounted(() => {
  commonColors.value = props.commonColors.map(c => Color(c));
  loadRecentColors();
});
</script>

<template>
  <div class="p-1">
    <div class="m-1">
      <div class="flex flex-wrap gap-2">
        <div
            v-for="color in commonColors"
            :key="color.hex()"
            :style="{ backgroundColor: color.hex() }"
            :title="color.hex()"
            class="w-4 h-4 rounded cursor-pointer"
            @click="handleColorSelect(color)"
        >
        </div>
      </div>
    </div>
    <div v-if="recentColors.length > 0">
      <Separator/>
      <div class="m-1">
        <div class="flex flex-wrap gap-2">
          <div
              v-for="color in recentColors"
              :key="color.hex()"
              :style="{ backgroundColor: color.hex() }"
              :title="color.hex()"
              class="w-4 h-4 rounded cursor-pointer"
              @click="handleColorSelect(color)"
          >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
