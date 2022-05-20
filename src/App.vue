<template>
  <div class="main">
    <!--   路由填放位置   -->
      <!--    切换路由时控制显示or隐藏底部组件(keep-alive vue3用法，Component等是固定写法)    -->
    <router-view @showNav="showNav" @hideNav="hideNav" v-slot="{ Component }">
      <transition>
        <!--v-bind 里 的JS表达式要这么写-->
        <keep-alive :include="'cachePath'">
          <component :is="Component"/>
        </keep-alive>
      </transition>
    </router-view>
    <router-view name="my"/>
    <GoTop/>
    <!--   底部组件   -->
    <FooterNav v-show="show"/>
  </div>
</template>

<script>
  import FooterNav from "@/components/public/FooterNav"
  import GoTop from "@/components/public/GoTop"
  import {ref, onBeforeUnmount} from "vue"
  import {useStore} from "vuex"

  export default {
    name: 'App',
    components: {
      GoTop,
      FooterNav
    },
    setup () {
      const store = useStore()

      // 路由界面控制底部组件显示or隐藏布尔值
      const show = ref(true)

      function showNav () {show.value = true}

      function hideNav () {show.value = false}
      // 要缓存的路由数据
      function cachePath () {return store.state.cachePath}

      // App卸载前移除state数据
      onBeforeUnmount(() => {
        localStorage.removeItem('store')
        localStorage.removeItem('reqArr')
      })
      return {
        show,
        showNav,
        hideNav,
        cachePath
      }
    }
  }
</script>

<style lang="less">
  @import "assets/css/index.css";
  /*html, body, #app {
    background-color: #F7EED6;
    !*background-color: #999999;*!
    width: 100%;
    min-height: 100%;
    position: absolute;
    !*left: 0;
    right: 0;
    top: 0;
    bottom: 0;*!
    opacity: .8;
    z-index: 99999;
    margin:0 auto;
  }*/
</style>
