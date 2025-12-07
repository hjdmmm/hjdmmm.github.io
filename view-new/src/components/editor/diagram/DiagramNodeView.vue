<script lang="ts" setup>
import {Button} from "@/components/ui/button";
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle,} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select";
import {Textarea} from "@/components/ui/textarea";
import {type NodeViewProps, NodeViewWrapper} from "@tiptap/vue-3";
import {Scaling} from 'lucide-vue-next';
import {computed, onUnmounted, ref, watch} from "vue";
import {DiagramType} from "./utils.ts";

const DiagramStatus = {
  Init: 1,
  Valid: 2,
  Invalid: 3,
} as const;

const props = defineProps<NodeViewProps>();

const diagramHtml = ref('');
const status = ref<(typeof DiagramStatus)[keyof typeof DiagramStatus]>(DiagramStatus.Init);
const dialogOpen = ref(false);
const code = ref(props.node.attrs.code || '');
const diagramType = ref(props.node.attrs.type);
const scale = ref(props.node.attrs.ratio || 1);
const resizing = ref(false);
const startX = ref(0);
const startY = ref(0);
const startScale = ref(0);
const resizeScale = ref(0);
const originalSize = ref({width: 0, height: 0});
const loadingMermaid = ref(false);
let mermaid: any = null;

const loadMermaid = async () => {
  if (mermaid || loadingMermaid.value) {
    return;
  }
  loadingMermaid.value = true;
  try {
    // @ts-expect-error
    const module = await import('https://cdn.jsdelivr.net/npm/mermaid@11.12.2/+esm');
    mermaid = module.default;
    mermaid.initialize({
      startOnLoad: false,
      suppressErrorRendering: true,
    });
  } catch (e) {
    mermaid = null;
    throw e;
  } finally {
    loadingMermaid.value = false;
  }
};

const renderMermaid = async (newCode: string) => {
  if (!newCode) {
    status.value = DiagramStatus.Init;
    diagramHtml.value = '';
    return;
  }
  try {
    await loadMermaid();
  } catch (e) {
    status.value = DiagramStatus.Invalid;
    return;
  }

  try {
    const {svg} = await mermaid.render(
        `mermaid-${Math.random().toString(36).substring(2, 15)}`,
        newCode
    );
    status.value = DiagramStatus.Valid;
    diagramHtml.value = svg;

    const tempDiv = document.createElement('div');
    tempDiv.style.cssText = 'position:absolute;left:-9999px;top:-9999px;visibility:hidden;';
    tempDiv.innerHTML = svg;
    document.body.appendChild(tempDiv);
    const svgElement = tempDiv.firstElementChild!;
    const rect = svgElement.getBoundingClientRect();
    originalSize.value = {
      width: rect.width,
      height: rect.height
    };
    document.body.removeChild(tempDiv);
  } catch (e) {
    status.value = DiagramStatus.Invalid;
  }
};

const onSave = () => {
  dialogOpen.value = false;
  props.updateAttributes({
    code: code.value,
    type: diagramType.value
  });
};

const onDoubleClick = () => {
  if (props.editor.isEditable) {
    code.value = props.node.attrs.code;
    diagramType.value = props.node.attrs.type;
    dialogOpen.value = true;
  }
};

const onCancel = () => {
  dialogOpen.value = false;
};

const containerStyle = computed(() => {
  if (status.value !== DiagramStatus.Valid) {
    return {
      width: 'auto',
      height: 'auto',
    }
  }
  const width = originalSize.value.width * scale.value;
  const height = originalSize.value.height * scale.value;
  return {
    width: `${width}px`,
    height: `${height}px`,
  };
});
const diagramStyle = computed(() => {
  if (resizing.value) {
    return {
      transform: `scale(${resizeScale.value})`,
      transformOrigin: 'top left',
    };
  }
  return {};
});

const startResize = (e: MouseEvent) => {
  if (!props.editor.isEditable) {
    return;
  }
  resizing.value = true;
  startX.value = e.clientX;
  startY.value = e.clientY;
  startScale.value = scale.value;
  resizeScale.value = scale.value;
  document.addEventListener('mousemove', resize);
  document.addEventListener('mouseup', stopResize);
};

const resize = (e: MouseEvent) => {
  if (!resizing.value) {
    return;
  }
  const deltaX = e.clientX - startX.value;
  const deltaY = e.clientY - startY.value;
  const delta = (deltaX + deltaY) / 200;
  const newScale = Math.min(3, startScale.value + delta);
  resizeScale.value = Number.isNaN(newScale) ? 1 : newScale;
};

const stopResize = () => {
  resizing.value = false;
  document.removeEventListener('mousemove', resize);
  document.removeEventListener('mouseup', stopResize);
  props.updateAttributes({
    ratio: resizeScale.value
  });
};

watch(() => props.node.attrs.code, async (newCode) => {
  await renderMermaid(newCode);
}, {immediate: true});

watch(() => props.node.attrs.ratio, async (newRatio) => {
  scale.value = (newRatio || 1);
}, {immediate: true});

onUnmounted(() => {
  document.removeEventListener('mousemove', resize);
  document.removeEventListener('mouseup', stopResize);
});
</script>

<template>
  <NodeViewWrapper class="flex py-2">
    <div
        class="min-w-80 rounded-lg border border-border bg-background cursor-pointer"
        @dblclick="onDoubleClick"
    >
      <div v-if="status === DiagramStatus.Init" class="min-h-10 flex items-center justify-center">
        双击编辑代码
      </div>
      <div
          v-else-if="status === DiagramStatus.Valid"
          :style="containerStyle"
      >
        <div :style="diagramStyle" class="min-h-12" v-html="diagramHtml"/>
      </div>
      <div
          v-else-if="status === DiagramStatus.Invalid"
          class="min-h-10 px-2 flex items-center justify-center text-destructive"
      >
        {{ props.node.attrs.type }}渲染失败
      </div>

      <div class="flex">
        <div class="flex ms-auto rounded border-t border-l bg-gray-400/20">
          <div class="text-sm text-muted-foreground p-1">
            {{ props.node.attrs.type }}示意图
          </div>
          <div
              class="cursor-se-resize mt-auto pb-0.5 pr-0.5"
              @mousedown="startResize"
          >
            <Scaling class="w-3 h-3"/>
          </div>
        </div>
      </div>
    </div>

    <Dialog v-model:open="dialogOpen">
      <DialogContent class="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>编辑示意图代码</DialogTitle>
        </DialogHeader>

        <Select v-model="diagramType" class="col-span-3">
          <SelectTrigger>
            <SelectValue placeholder="选择图表类型"/>
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>图表类型</SelectLabel>
              <SelectItem
                  v-for="(value, key) in DiagramType"
                  :key="key"
                  :value="key"
              >
                {{ value }}
              </SelectItem>
            </SelectGroup>
          </SelectContent>
        </Select>

        <Textarea
            v-model="code"
            class="min-h-[300px] font-mono text-sm"
        />

        <DialogFooter>
          <Button @click="onSave">保存</Button>
          <Button variant="outline" @click="onCancel">取消</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </NodeViewWrapper>
</template>
