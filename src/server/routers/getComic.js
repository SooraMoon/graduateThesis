const express = require('express')
const fs = require('fs')
const path = require('path')
const router = express.Router()
const superagent = require("superagent")
// 导入 cheerio
const cheerio = require('cheerio')
//const axios = require("axios")

router.get('/', () => {
    // 章节里的漫画图片子链接 如/m1016479/
    let imgHref = []
    // 漫画每话的页数
    let numOfOneChapterArr = []

    // 获取漫画源 html 结构
    superagent.get('https://www.dm5.com/manhua-nanjiasanzimei/').end((err, res) => {
        if (err) {
            console.log('爬取失败', err.message)
        } else {
            // 访问成功，请求的页面所返回的数据会包含在res
            getComicChapter(res).forEach( item => {
                // 这里存储的就是每一话的网页首页地址
                imgHref.push('https://www.dm5.com' + item.chapterHref)
                // 爬取并写入漫画图片地址
                fs.writeFile(path.join(__dirname, '../../../public/cartoon/comicImg.json'), JSON.stringify(imgHref), ( err) => {
                    if (err) {
                        console.log('写入失败', err.message)
                    }
                })

            })

            imgHref.forEach( (value) => {
                // 拼接每一章节路径（由于资源时动态加载的，故真正的资源URL在div#chapterpager的 a 标签里的 href）
                superagent.get(value).end((err, res) => {
                    if (err) {
                        console.log('拼接路径时爬取失败', err.message)
                    } else {
                        const $ = cheerio.load(res.text)
                        numOfOneChapterArr.push($('div#chapterpager').children('a').eq(-1).index())
                        console.log($('div#showimage div#cp_img img').attr('id'))
                    }
            })
            // 写入漫画每一话的页数
            fs.writeFile(path.join(__dirname, '../../../public/cartoon/comicNum.json'), JSON.stringify(numOfOneChapterArr), ( err) => {
                    if (err) {
                        console.log('写入失败', err.message)
                    }
                })
            })
            // 遍历每一章的路径
            // 抓取热漫画章节并写入文件
            fs.writeFile(path.join(__dirname, '../../../public/cartoon/chapter.json'), JSON.stringify(getComicChapter(res)), ( err) => {
                if (err) {
                    console.log(err.message)
                }
            })
        }
    })
    //console.log(imgHref)

    // 获取章节里的漫画图片地址（此处读取文件会出问题，具体看浏览器收藏的爬虫专栏）
    /*axios.get('../../../public/cartoon/chapter.json').then(
        response => {
            response.data.forEach( item => {
                imgHref.push(item.chapterHref)
            })
        },
        error => {
            console.log(error.message)
        }
    )*/
   /* imgHref.forEach( item => {
        console.log(item)
    })*/
    /*// 遍历每一章节的路径
    imgHref.forEach( item => {
        // 拼接每一章节路径
        superagent.get('https://www.dm5.com' + item[0]).end((err, res) => {
            if (err) {
                console.log('爬取失败', err.message)
            } else {
                // 爬取并写入漫画图片地址
                fs.writeFile(path.join(__dirname, '../../../public/cartoon/comicImg.json'), JSON.stringify(getComicImg(res)), ( err) => {
                    if (err) {
                        console.log('写入失败', err.message)
                    }
                })
            }
        })
    })*/

})

/*
* @intent 抓取漫画内容
* @tool 使用cheerio
* */
// 获取漫画章节
let getComicChapter = (res) => {
    let comicChapterArr = []
    // 访问成功，请求的页面所返回的数据会包含在res.text中。

    /* 使用cheerio模块的cheerio.load()方法，将HTMLdocument作为参数传入函数
       以后就可以使用类似jQuery的$(selectior)的方式来获取页面元素
     */
    let $ = cheerio.load(res.text)

    // 找到目标数据所在的页面元素，获取数据
    $('div#chapterlistload ul li a').each((index, element) => {
        // cherrio中$('selector').each()用来遍历所有匹配到的DOM元素
        // 参数 index 是当前遍历的元素的索引，element 就是当前遍历的DOM元素
        let item = {
            chapterTitle: $(element).text(),        // 获取章节名称
            chapterHref: $(element).attr('href')    // 获取漫画章节链接地址
        }
        comicChapterArr.push(item)              // 存入最终结果数组
    })
    return comicChapterArr
}
// 获取漫画图片
/*let getComicImg = (res) => {
    let comicImgArr = []
    let $ = cheerio.load(res.text)
    $('div#showimage div#cp_img img').each((index, element) => {
        // cherrio中$('selector').each()用来遍历所有匹配到的DOM元素
        // 参数 index 是当前遍历的元素的索引，element 就是当前遍历的DOM元素
        let item = {
            imgHref: $(element).attr('src')    // 获取漫画图片地址
        }
        comicImgArr.push(item)  // 存入最终结果数组
        console.log('这是comicArr', comicImgArr)
    })
    return comicImgArr
}*/

module.exports = router