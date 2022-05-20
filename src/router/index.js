import {createRouter, createWebHashHistory} from "vue-router"
import store from "@/store"

const router = createRouter({
    // 需配置模式(无论何种模式)
    history: createWebHashHistory(),
    routes: [
        // 重定向
        {
            path: '/',
            redirect: '/comic-vue3/home'
        },
        // 主页
        {
            path: '/comic-vue3',
            name: 'comic-vue3',
            // 动态引入
            component: () => import('@/pages/ComicHome'),
            // 分类组件
            children: [
                {
                    path: 'home',
                    name: 'home',
                    component: () => import('@/pages/comicHomePages/HomeComic')
                },
                {
                    path: 'boyComic',
                    name: 'boyComic',
                    component: () => import('@/pages/comicTypePages/BoyComic'),
                    props: true
                },
                {
                    path: 'girlComic',
                    name: 'girlComic',
                    component: () => import('@/pages/comicTypePages/GirlComic'),
                    props: true
                }
            ]
        },
        // 我的
        {
            path: '/my',
            name: 'my',
            component: () => import('@/pages/UserConfig')
        },
        // 漫画详情
        {
            path: '/detail',
            name: 'detail',
            component: () => import('@/pages/ComicDetail')
        },
        // 漫画内部
        {
            path: '/look',
            name: 'look',
            component: () => import('@/pages/ComicLook')
        },
        // 搜索
        {
            path: '/search',
            name: 'search',
            component: () => import('@/pages/ComicSearch'),
            beforeEnter: (to, from, next) => {
                console.log(to.name,from.name)
                next()
            }
        },
        {
            path: '/upload',
            name: 'upload',
            component: () => import('@/pages/comicHomePages/UploadComic')
        }
    ]
})
// 全局后置路由守卫 1、初始时触发 2、路由切换时触发
router.afterEach(() => {
    localStorage.setItem('store', JSON.stringify(store.state))
    // 路由跳转后定位到顶部
    window.scrollTo(0, 0)
})

export default router
