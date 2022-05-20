<template>
  <div class="upload-wrap">
    <!--  上传头部  -->
    <div class="upload-head">
      <h2>新建漫画</h2>
    </div>
    <hr>
    <!--  上传导航栏  -->
    <div class="upload-nav">
      <van-row>
        <van-col class="upload-nav-li" span="4" offset="1">
          <span>
            <em>1</em>
            创建漫画
          </span>
        </van-col>
        <van-col class="upload-nav-li" span="1" offset="2">
          <i class="iconfont icon-xiangyou"></i>
        </van-col>
        <van-col class="upload-nav-li" span="4" offset="2">
          <span>
            <em>2</em>
            创建章节
          </span>
        </van-col>
        <van-col class="upload-nav-li" span="1" offset="2">
          <i class="iconfont icon-xiangyou"></i>
        </van-col>
        <van-col class="upload-nav-li" span="4" offset="2">
          <em>3</em>
          <span>创建成功</span>
        </van-col>
      </van-row>
    </div>
    <!--  上传内容体  -->
    <div class="upload-content-form">
      <form>
        <div class="form-li">
          <label class="item" for="forName">
            <em>*</em>
            <span>漫画名称：</span>
          </label>
          <input type="text" name="name" id="forName" class="forIn" v-model="upForm.comicName">
        </div>
        <div class="form-li">
          <label class="item" for="forNick">
            <em>*</em>
            <span>笔名：</span>
          </label>
          <input type="text" name="author" id="forNick" class="forIn" v-model="upForm.author">
        </div>
        <div class="form-li">
          <label class="item">
            <em>*</em>
            <span>作品题材：</span>
          </label>
          <label>
            <input type="checkbox" name="types" value="1" v-model="upForm.comicTypes">
            少年
          </label>
          <label>
            <input type="checkbox" name="types" value="2" v-model="upForm.comicTypes">
            少女
          </label>
          <label>
            <input type="checkbox" name="types" value="3" v-model="upForm.comicTypes">
            青春
          </label>
          <label>
            <input type="checkbox" name="types" value="4" v-model="upForm.comicTypes">
            恋爱
          </label>
          <label>
            <input type="checkbox" name="types" value="5" v-model="upForm.comicTypes">
            校园
          </label>
          <label>
            <input type="checkbox" name="types" value="6" v-model="upForm.comicTypes">
            热血
          </label>
          <label>
            <input type="checkbox" name="types" value="7" v-model="upForm.comicTypes">
            奇幻
          </label>
        </div>
        <div class="form-li">
          <label class="item">
            <em>*</em>
            <span>创作进程：</span>
          </label>
          <label>
            <input type="radio" name="state" value="1" checked v-model="upForm.comicState">
            连载中
          </label>
          <label>
            <input type="radio" name="state" value="2" v-model="upForm.comicState">
            已完结
          </label>
        </div>
        <div class="form-li">
          <label class="item">
            <em>*</em>
            <span>作品封面：</span>
          </label>
          <img :src="upForm.comicCover" alt="" class="cover-img">
          <input type="button" value="上传图片" class="upload-img-btn" @click="showCanvas">
        </div>
        <div class="form-li">
          <label class="item">
            <em>*</em>
            <span>作品简介：</span>
          </label>
          <textarea rows="5" cols="50" maxlength="255" v-model="upForm.comicIntroduction"/>
        </div>
      </form>
    </div>
  </div>
  <!-- 遮罩层 -->
  <div id="shield"></div>
  <!--  封面图裁剪画布  -->
  <div class="cover-img-canvas" v-show="upForm.canvasShow">
    <div class="canvas-head">
      <i class="iconfont icon-jianqie"></i>
      <span>裁剪图片</span>
      <i class="iconfont icon-guanbi" @click="canvasClose"></i>
    </div>

    <div class="canvas-con">
      <!-- 用于给Cropper.js覆盖使用的img 注意：不给 img 父容器会超出宽高  -->
      <div class="crop-container">
        <img id="cropImg" src="" alt="">
      </div>
      <div class="canvas-con-rightBar">
        <!-- 两个用于预览的div  -->
        <div class="previewText">
          <span>裁剪预览</span>
        </div>
        <div class="previewBox">
          <img src="" alt="">
        </div>
        <div class="previewBoxRound">预览圆形</div>
        <!-- 用于获取上传文件的input，type="file"，并且监听change事件  -->
        <input
            type="file"
            accept="image/*"
            id="imgReader"
            @change="loadingImg($event)"
            :style="{display: 'none'}"
        >
        <div class="btn-group">
          <button @click="uploadImg" class="upload-img-btn">选择图片</button>
          <button @click="saveImg($event)" class="save-img-btn">保存图片</button>
        </div>
      </div>
    </div>
    <div class="canvas-foot">
      <button>完成裁剪</button>
    </div>
  </div>
</template>

<script>
import {reactive} from "vue";
import Cropper from 'cropperjs'
import 'cropperjs/dist/cropper.css';
import axios from "axios";

export default {
    name: "UploadComic",
    setup () {
      const upForm = reactive({
        comicName: null,
        author: null,
        comicTypes: [],
        comicState: null,
        comicCover: null, // 裁剪后的封面
        comicIntroduction: null,
        canvasShow: false,
      })

      // cropperjs全局对象
      let CROPPER = null

      // 显示画布
      function showCanvas () {
        upForm.canvasShow = true
        // 遮罩层
        /*
        * 遮罩层有两种实现方式：
        * 1、第二种方法为以下方式，直接动态操纵 body 或者 html 标签体设置 style 样式
        * 2、外部大 div 作为遮罩层块，并设置样式， 初始时display 属性为 none；触发时其 display 属性为 block，关闭时 display 属性为 none
        * 比较：第二种方法效果更好
        * */
        //document.querySelector('body').setAttribute('style', 'position: fixed; background-color: #cccccc; opacity: 0.3; z-index: 9998; pointer-events: none;')
        document.getElementById('shield').style.display = 'block'
      }
      // 获取图片事件
      function loadingImg (event) {
        //读取上传文件
        let reader = new FileReader();
        if(event.target.files[0]){
          // readAsDataURL方法可以将File对象转化为data:URL格式的字符串（base64编码）
          reader.readAsDataURL(event.target.files[0]);
          reader.onload = (e)=>{
            // 将img的src改为刚上传的文件的转换格式
            document.getElementById('cropImg').src = reader.result;

            const image = document.getElementById('cropImg');
            console.log(e)
            // 创建cropper实例
            CROPPER = new Cropper(image, {
              aspectRatio: 3 / 4,   //固定裁剪框的比例（横/竖）,此处16/16则固定为正方形
              viewMode: 0,
              //background: false,
              //minContainerWidth: 500,  //容器最小的宽度
              //minContainerHeight: 500, //容器最小的高度
              dragMode: 'move',        //设置裁剪框为可以移动
              preview:[document.querySelector('.previewBox')] //设置我们需要添加实时预览的地方
            })
          }
        }
      }

      console.log(CROPPER)
      // 选择图片按钮
      function uploadImg () {
        document.getElementById('imgReader').click()
        // 删除图片实例
        if (CROPPER) {
          CROPPER.destroy()
        }
      }
      // 保存图片按钮
      function saveImg (event) {
        event.preventDefault()
        //getCroppedCanvas方法可以将裁剪区域的数据转换成canvas数据
        CROPPER.getCroppedCanvas({
          width: 135,
          height: 180,
          maxWidth: 4096,
          maxHeight: 4096,
          fillColor: '#fff',
          imageSmoothingEnabled: true,
          imageSmoothingQuality: 'high',
        }).toBlob((blob) => {
          //然后调用浏览器原生的toBlob方法将canvas数据转换成blob数据
          //之后就可以愉快的将blob数据发送至后端啦，可根据自己情况进行发送，我这里用的是axios
          const formData = new FormData();
          // 第三个参数为文件名，可选填.
          formData.append('croppedImage', blob/*, 'example.png' */);
          let config = {
            headers:{'Content-Type':'multipart/form-data'}
          }
          axios.post('http://localhost:8081/#/upload', formData, config)
              .then((response)=>{
                upForm.comicCover = response.data
                console.log(upForm.comicCover)
              })
              .catch((err)=>{
                console.log(err)
              })
        })
        // 关闭画布
        canvasClose()
      }
      // 关闭画布
      function canvasClose () {
        upForm.canvasShow = false
        document.getElementById('shield').style.display = 'none'
        // 删除图片实例
        if (CROPPER) {
          CROPPER.destroy()
        }
      }
      return {
        upForm,
        showCanvas,
        loadingImg,
        uploadImg,
        saveImg,
        canvasClose
      }
    }
  }
</script>

<style lang="less">
  /* 上传体 */
  .upload-wrap {
    /* 上传头部 */
    .upload-head {
      margin-left: 20px;
      padding: 10px;
    }
    /* 上传导航栏 */
    .upload-nav {
      margin-top: 20px;
      border: 1px solid #e4e4e4;
      .upload-nav-li {
        padding: 10px 0;
        font-size: 24px;
      }
    }
    /* 上传内容体 */
    .upload-content-form {
      border: 1px solid #e4e4e4;
      form {
        /* form-li的公共样式 */
        .form-li {
          margin-top: 20px;
          margin-left: 20px;
          label.item {
            display: inline-block;
            width: 100px;
            font-size: 16px;
            text-align: right;
            em {
              padding: 0 5px;
              color: #ff0000;
            }
          }
          input, textarea  {
            margin-left: 20px;
            border: 2px solid #e4e4e4;
            border-radius: 2px;
          }
          input[type="checkbox"], input[type="radio"] {
            /* 缩放比例 */
            transform: scale(1.5);
            vertical-align: middle;
          }
        }
        #forName, #forNick {
          width: 250px;
          height: 30px;
        }
        .form-li:nth-child(5) {
          .cover-img {
            display: inline-block;
            width: 135px;
            height: 180px;
            margin-left: 20px;
            border: 2px solid #e4e4e4;
          }
          .upload-img-btn {
            padding: 10px;
            vertical-align: top;
            background-color: #b3d4fc;
          }
        }
        .form-li:nth-child(6) {
          textarea {
            margin-left: 20px;
            vertical-align: top;
            resize: none;
          }
        }
      }
    }
  }
  /* 全局遮罩层 */
  #shield {
    position: fixed;
    z-index: 5;
    display: none;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    overflow: scroll;
    background: rgba(0, 0, 0, 0.5);
    pointer-events: all;
  }
  /* 裁剪画布 */
  .cover-img-canvas {
    position: absolute;
    top: 15px;
    z-index: 10;
    display: flex;
    flex-flow: column nowrap;
    width: 860px;
    height: 700px;
    background-color: #ffffff;

    .canvas-head {
      padding: 20px;
      border-bottom: 1px solid #e4e4e4;
      font-size: 16px;

      span {
        margin-left: 20px;
      }
      .icon-guanbi {

      }
    }
    .canvas-con {
      display: flex;
      flex-flow: row wrap;
      margin-left: 20px;
      margin-top: 20px;

      .crop-container {
        #cropImg {
          width: 500px;
          height: 500px;
          max-width: 100%;
          border: 1px solid #e4e4e4;
        }
      }
      .canvas-con-rightBar {
        width: 300px;
        margin-left: 20px;
        border: 1px solid #e4e4e4;
        text-align: center;

        .previewText {
          margin-bottom: 20px;
        }
        .previewBox {
          width: 135px;
          height: 180px;
          margin: auto;
          box-shadow: 0 0 5px #adadad;
          overflow: hidden;  /*这个超出设置为隐藏很重要，否则就会整个显示出来*/
        }
        .previewBoxRound {
          display: none;
          border-radius: 50%;     /*设置为圆形*/
        }
        .btn-group {
          display: flex;
          flex-flow: row wrap;
          justify-content: space-evenly;
          margin-top: 50px;

          .upload-img-btn, .save-img-btn {
            padding: 5px;
            font-size: 16px;
            background-color: #b3d4fc;
          }
          .upload-img-btn {

          }
          .save-img-btn {

          }
        }
      }
    }
    .canvas-foot {
      padding: 20px;
      border-top: 1px solid #e4e4e4;
      border-bottom: 1px solid #e4e4e4;
    }
    #cropper-img {
      display: block;
      width: 500px;
      height: 500px;
    }
  }
</style>
