import { createApp } from 'vue'
import App from './App.vue'
// 引入路由配置
import router from '@/router'
// 引入store
import store from '@/store'
// 引入vant、懒加载，提示
import Vant,{Lazyload, Toast, Swipe, SwipeItem, Col, Row, Form, Field, CellGroup} from 'vant'
// 引入vant样式
import 'vant/lib/index.css'
// 引入iconfont图标样式
import '@/assets/iconfont/iconfont.css'
const app = createApp(App)

app.use(router)
app.use(store)
app.use(Vant).use(Toast).use(Swipe).use(SwipeItem).use(Col).use(Row).use(Form).use(Field).use(CellGroup)
// 懒加载只是一个性能和用户体验问题
app.use(Lazyload, {
    lazyComponent: true
})
app.mount('#app')
