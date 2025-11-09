<script lang="ts" setup>
import BlogHeader from "@/components/BlogHeader.vue";
import TiptapEditor from "@/components/editor/TiptapEditor.vue";
import ArticleSidebar from "@/components/sidebar/ArticleSidebar.vue";
import {SidebarProvider} from "@/components/ui/sidebar";
import {selectedArticleId} from "@/store/articleStore.ts";
</script>

<template>
  <header>
    <BlogHeader/>
  </header>
  <main>
    <SidebarProvider>
      <ArticleSidebar/>
      <TiptapEditor v-if="selectedArticleId"/>
      <div v-else class="w-full p-4"></div>
    </SidebarProvider>
  </main>
</template>
