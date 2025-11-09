<script lang="ts" setup>
import CreateArticleDialog from "@/components/sidebar/CreateArticleDialog.vue";
import {Button} from "@/components/ui/button";
import {Collapsible, CollapsibleContent, CollapsibleTrigger} from "@/components/ui/collapsible";
import {ContextMenu, ContextMenuContent, ContextMenuItem, ContextMenuTrigger,} from '@/components/ui/context-menu';
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {SidebarMenu, SidebarMenuAction, SidebarMenuItem} from "@/components/ui/sidebar";
import {articleNodeMap, createNewArticle, reloadChildNodes, removeArticle} from "@/store/articleStore.ts";
import {ChevronDown, ChevronRight, Loader2} from 'lucide-vue-next'
import {type FocusOutsideEvent, type PointerDownOutsideEvent, PopoverClose} from "reka-ui"
import {computed, ref, watch} from 'vue'
import ArticleTreeLeafNode from "./ArticleTreeLeafNode.vue";

const props = defineProps<{
  nodeId: string
}>();

const node = computed(() => {
  return articleNodeMap.value.get(props.nodeId)!;
});

const childrenLoaded = computed(() => {
  if (!node.value) {
    return false;
  }
  for (const childId of node.value.children) {
    const childNode = articleNodeMap.value.get(childId);
    if (!childNode) {
      return false;
    }
  }
  return true;
});

const level = computed(() => {
  let current = node.value;
  let depth = 0;
  while (current.parentId) {
    depth++;
    current = articleNodeMap.value.get(current.parentId)!;
  }
  return depth;
});

const open = ref(false);
const hasChildren = computed(() => (node.value.children.length || 0) > 0);
const loadingChildren = ref(false);

const loadChildren = async (childIds: string[]) => {
  if (childIds.length === 0) {
    return;
  }
  if (loadingChildren.value) {
    return;
  }
  loadingChildren.value = true;
  try {
    await reloadChildNodes(childIds);
  } finally {
    loadingChildren.value = false;
  }
}

const createChildArticle = async (title: string) => {
  await createNewArticle(false, title, props.nodeId);
};

const deleteCurrentArticle = async () => {
  await removeArticle(props.nodeId);
}

watch(() => node.value.children, async (newChildIds) => {
  if (childrenLoaded.value) {
    await loadChildren(newChildIds);
  }
})

const popoverInteractOutside = (event: PointerDownOutsideEvent | FocusOutsideEvent) => {
  // TODO
  event.preventDefault();
};
</script>

<template>
  <ContextMenu>
    <ContextMenuTrigger as-child>
      <template v-if="hasChildren">
        <Collapsible
            v-model:open="open"
            :defaultOpen="false"
            class="group/collapsible"
            @update:open="!childrenLoaded && loadChildren(node.children)">
          <ArticleTreeLeafNode :level="level" :node-id="props.nodeId">
            <CollapsibleTrigger as-child>
              <SidebarMenuAction>
                <ChevronDown v-if="open" class="w-4 h-4"/>
                <ChevronRight v-else class="w-4 h-4"/>
              </SidebarMenuAction>
            </CollapsibleTrigger>
            <CollapsibleContent>
              <SidebarMenu>
                <template v-if="loadingChildren">
                  <SidebarMenuItem>
                    <Loader2 class="w-4 h-4 mr-2 animate-spin"/>
                  </SidebarMenuItem>
                </template>
                <template v-else>
                  <ArticleTreeNode
                      v-for="childId in node.children"
                      :key="childId"
                      :node-id="childId"
                  />
                </template>
              </SidebarMenu>
            </CollapsibleContent>
          </ArticleTreeLeafNode>
        </Collapsible>
      </template>
      <template v-else>
        <ArticleTreeLeafNode :level="level" :node-id="props.nodeId"/>
      </template>
    </ContextMenuTrigger>
    <ContextMenuContent class="min-w-32">
      <CreateArticleDialog v-if="node.canCreateChild" @create-article="createChildArticle">
        <ContextMenuItem @select="event => event.preventDefault()">
          新建子文档
        </ContextMenuItem>
      </CreateArticleDialog>

      <Popover v-if="node.canDelete">
        <PopoverTrigger as-child>
          <ContextMenuItem @select="event => event.preventDefault()">
            删除文档
          </ContextMenuItem>
        </PopoverTrigger>
        <PopoverContent side="right" @interact-outside="popoverInteractOutside">
          <div>
            <div>确认删除该文档 {{ node.title }} 吗？</div>
            <div class="flex justify-end">
              <PopoverClose as-child>
                <Button variant="destructive" @click="deleteCurrentArticle">删除</Button>
              </PopoverClose>
            </div>
          </div>
        </PopoverContent>
      </Popover>
    </ContextMenuContent>
  </ContextMenu>
</template>
