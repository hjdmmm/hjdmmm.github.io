import {createApp} from 'vue'
import './index.css'
import {createRouter, createWebHashHistory} from 'vue-router'
import App from './App.vue'
import Blog from './components/Blog.vue'

const routes = [
    {path: '/', component: Blog},
    {path: '/article/:id', component: Blog, props: true}
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

const app = createApp(App)
app.use(router)
app.mount('#app')
