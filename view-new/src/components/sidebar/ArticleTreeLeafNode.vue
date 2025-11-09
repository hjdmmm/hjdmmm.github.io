<script lang="ts" setup>
import {SidebarMenuButton, SidebarMenuItem} from "@/components/ui/sidebar";
import {cn} from "@/lib/utils.ts";
import {articleNodeMap, changeParent, selectedArticleId, updateSelectedArticleId} from '@/store/articleStore.ts';
import {user} from "@/store/userStore.ts";
import {computed} from 'vue'

const levelVariants = new Map<number, string>([
  [0, "ps-0"],
  [1, "ps-4"],
  [2, "ps-8"],
  [3, "ps-12"],
  [4, "ps-16"],
  [5, "ps-20"],
  [6, "ps-24"],
  [7, "ps-28"],
]);

const props = defineProps<{
  level: number
  nodeId: string
}>();

const node = computed(() => {
  return articleNodeMap.value.get(props.nodeId)!;
});

const isActive = computed(() => {
  return selectedArticleId.value === props.nodeId;
});

const buttonClass = computed(() => {
  const padding = levelVariants.get(props.level) || levelVariants.get(7);
  return cn(padding, 'text-base')
});

const select = async () => {
  await updateSelectedArticleId(props.nodeId);
};

const isChildNode = (nodeId: string, parentId: string): boolean => {
  if (nodeId === parentId) {
    return true;
  }
  const parentNode = articleNodeMap.value.get(parentId);
  if (!parentNode) {
    return true;
  }
  return parentNode.children.some(childId => isChildNode(nodeId, childId));
};

const handleDragStart = (event: DragEvent) => {
  if (!event.dataTransfer || !node.value.canChangeParent) {
    return;
  }
  event.dataTransfer.effectAllowed = 'move';
  event.dataTransfer.setData('text/plain', props.nodeId);
  console.log("set", props.nodeId)
};

const handleDragOver = (event: DragEvent) => {
  event.preventDefault();
  if (!event.dataTransfer) {
    return;
  }
  if (node.value.canCreateChild) {
    event.dataTransfer.dropEffect = 'move';
  } else {
    event.dataTransfer.dropEffect = 'none';
  }
};

const handleDrop = async (event: DragEvent) => {
  event.preventDefault();
  if (!event.dataTransfer) {
    return;
  }
  const draggedNodeId = event.dataTransfer.getData('text/plain');
  if (draggedNodeId.length === 0) {
    return;
  }
  if (!node.value.canCreateChild || isChildNode(props.nodeId, draggedNodeId)) {
    return;
  }
  await changeParent(draggedNodeId, props.nodeId);
};

const canChangeParent = computed(() => user.value !== null && node.value.canChangeParent);
</script>

<template>
  <SidebarMenuItem>
    <SidebarMenuButton
        :isActive="isActive"
        @click="select"
    >
      <span
          :class="buttonClass"
          :draggable="canChangeParent"
          @dragover="handleDragOver"
          @dragstart="handleDragStart"
          @drop="handleDrop"
      >
        {{ node.title }}
      </span>
    </SidebarMenuButton>
    <slot/>
  </SidebarMenuItem>
</template>
