<template>
  <div id="utilSide">
    <div class="goTop" v-show="utilSideObj.goTopShow" @click="goTop">
      <i class="iconfont icon-fanhuidingbu"></i>
    </div>
    <div class="eyeShield" @click="eyeShield">
      <span>护眼</span>
    </div>
  </div>
  <!-- 遮罩层 -->
  <div id="shield"></div>
</template>

<script>
  import {onMounted, onUnmounted, reactive, watchEffect} from "vue"
  export default {
    name: "GoTop",
    setup () {

      const utilSideObj = reactive({
        goTopShow: false,
        eyeShieldShow: false,
        scrollTop: null
      })

      function goTop () {
        const timer = setInterval(() => {
          let current = document.scrollingElement.scrollTop
          if (current === 0) clearInterval(timer)
          document.scrollingElement.scrollTop = current - 100
        }, 16)
      }
      // 护眼模式
      function eyeShield () {
        utilSideObj.eyeShieldShow = !utilSideObj.eyeShieldShow
        if (utilSideObj.eyeShieldShow) {
          // 动态引入护眼样式
          // document.querySelector('html').setAttribute('style', 'background-color: #F7EED6; opacity: .8; z-index: 9999;')
          //document.querySelector('body').setAttribute('style', 'background-color: #F7EED6; opacity: .8; z-index: 9999;')
          document.getElementById('shield').style.display = 'block'
        } else {
          // 移除护眼样式
          // document.querySelector('html').removeAttribute('style')
          //document.querySelector('body').removeAttribute('style')
          document.getElementById('shield').style.display = 'none'
        }
        console.log(utilSideObj.eyeShieldShow)
      }

      // 获取滚动条高度
      function handleScroll () {
        utilSideObj.scrollTop = document.scrollingElement.scrollTop
      }
      // 挂载完毕时往window里添加监听事件
      onMounted(() => {
        window.addEventListener("scroll", handleScroll)
      })

     /* watch(() => userScroll.scrollTop, () => {
        userScroll.goTopShow = userScroll.scrollTop > 500
      })*/
      // 监视滚动条高度变化
      watchEffect(() => {
        utilSideObj.goTopShow = utilSideObj.scrollTop > 500
      })
      // 卸载前移除监听事件
      onUnmounted(() => {
          window.removeEventListener("scroll", handleScroll)
      })
      return {
        utilSideObj,
        goTop,
        eyeShield
      }
    }
  }
</script>

<style lang="less">
  #utilSide {
    .goTop,.eyeShield {
      position: fixed;
      right: 20px;
      border-radius: 10%;
      background: #fff;
      cursor: pointer;
      box-shadow: 0 0 6px rgba(0, 0, 0, 0.12);
      opacity: .8;
      text-align: center;
      font-size: 15px;
    }
    .goTop {
      padding: 17px;
      bottom: 120px;
    }
    .eyeShield {
      padding: 15px 10px;
      bottom: 60px;
    }
    .goTop:hover .goTopIcon {
      color: rgba(51, 153, 255, 1);
    }
    .goTopIcon {
      font-size: 20px;
      color: rgba(51, 153, 255, 0.8);
    }
  }
  #shield {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 9999;
    display: none;
    background-color: #F7EED6;
    opacity: 0.3;
    pointer-events: none;
  }
</style>
