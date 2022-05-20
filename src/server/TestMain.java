/**
 - 使用说明：
 - 使用时修改 url路径为需要下载的漫画首页
 - static String path必须为已存在目录
 - 随着时间推移可能代码会失效，原因是部分数据可能会不匹配
 - *本代码所有连接失败后都会重试一次，当程序终止后，请检查错误输出以及根目录下的fail.txt文件
 - @author Miku
 *
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Testmain {

    static String rooturl = "https://res.xiaoqinre.com/";  //古风漫画所使用的，保存图片数据的网站根路径
    static String salturl = "https://www.gufengmh9.com";  //古风漫画根地址
    static String path = "F:\\data\\妖怪居酒屋\\";  //自己定义下载的根目录，该目录必须为已存在
    static int page = 36;  //从第个<li>开始下载，默认从头开始  ; 注意是按照古风漫画列表横向计算话数，并非真实第X话

    public static void main(String[] args) {

        /**使用时修改下面这个 url 替换为自己需要下载的漫画主页**/
        String url = "https://www.gufengmh9.com/manhua/yaoguaijujiuwu/";

        String startstr = "chapterImages = ";  //html中获取图片组地址的匹配字串
        String endstr = "chapterPath = ";  //html中获取存放图片路径数据的匹配字串
        Downloadmanhua.failtxt = Testmain.path;  //将下载路径传递至工作java的属性中
        String html = Downloadmanhua.doGet(url);  //获取漫画总目录的html
        if (html == null) {
            System.out.println("网页为空！漫画首页未能成功加载，请检查网络通常或url是否拼错！");
            return ;
        }
        Document document =  Jsoup.parse(html.toString());
        /**注意，这一句在未来不一定有用，目的是获取所有的<li>标签，从而取得漫画章节名与url**/
        /**如果页面有多个列表，需要其他列表请尝试document.getElementsByClass("chapter-body")[1、2、3]等等，可调式输出查看**/
        Element e = document.getElementsByClass("chapter-body").first();  //通过class中chapter-body属性去获得标签，这里取得的只有一个，就是我们需要的，因此.first()
        if (e == null) {System.out.println("获取章节元素失败，html = "+html);return ;}

        /*循环取得 漫画章节名 与对应 url */
        int len = 1 ;
        for (Element li : e.getElementsByTag("li")) {
            /**这里获取<li>标签中的<a>与<span>时，请注意有效性**/
            String chapterurl = li.getElementsByTag("a").first().attr("href");  //获得章节链接
            String chapter = li.getElementsByTag("span").first().text();  //获得章节名
            if (chapterurl == null || chapterurl.equals("") || chapter == null || chapter.equals("")) {
                System.out.println("获取<li>标签中的内容失败，该<li>标签为:"+li.text());
                return ;
            }
            System.out.println("获取到"+chapter+"章节的链接:"+chapterurl);

            if (len < Testmain.page) {
                len++;
                continue;
            }
            /**输入具体章节url，去获得所有图片链接，然后循环下载**/
            GetOne(startstr , endstr , salturl+chapterurl  , chapter , len);
            len++;
        }
        System.out.println("结束");

    }

    static void GetOne(String startstr , String endstr , String url , String time , int len ){
        String html = Downloadmanhua.doGet(url);
        if (html == null || html.equals("")) {
            System.out.println("获取第"+len+"个<li>失败，章节名："+time +" , 重试中ing...");
            html = Downloadmanhua.doGet(url);
            if (html == null || html.equals("")) {
                System.out.println("获取第"+len+"个<li>失败，章节名："+time +" , 重试失败，已强制结束");
                System.exit(-1);
            }
        }
        String pattern = startstr+"\\[(.+?)\\]";  //使用正则匹配去取得该章节全部图片名 及 路径链接
        String pattern2 = endstr+"\"(.+?)\"";
        Matcher m = Pattern.compile(pattern).matcher(html);
        Matcher m2 = Pattern.compile(pattern2).matcher(html);
        String chapterImages = null;
        String chapterPath  = null;
        /*获取到每一话的所有图片地址和相对路径*/
        if (m.find() && m2.find()) {
            chapterImages = m.group(0).substring(startstr.length());
            chapterPath = m2.group(0).substring(endstr.length()).replace("\"", "");
        } else {
            try {
                System.out.println("正则解析"+time+"章节失败 ,章节链接为:"+url);
                Downloadmanhua.Writefailmsg("NO MATCH ", time, url);
            } catch (IOException e) {
                System.out.println("写入错误日志失败！！");
            }
            System.out.println("NO MATCH");
            return ;
        }
        /*获取到每一话的所有图片地址和相对路径*/
        System.out.println("chapterPath = "+chapterPath+" , 获取到图片数组 = "+chapterImages);
        /*拼接图片url*/
        List<String> list = tojpgurl(chapterImages , chapterPath);
        int i = 0 ;

        /*下载*/
        for (String jpgurl : list) {
            try {
                /*下载5张图，休息1秒*/
                if (i % 5 == 0 ) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                /*使用写好的下载方法*/
                boolean flag = Downloadmanhua.getImg(jpgurl, (++i)+".jpg" , path , time , false);
                /*返回状态码非200可还有一次下载机会*/
                if (!flag) {
                    Downloadmanhua.getImg(jpgurl, i+".jpg" , path , time , true);
                }
            } catch (IOException e) {
                //失败最多可再重试一次
                try {
                    Downloadmanhua.getImg(jpgurl, i+".jpg" , path , time + "" , true);
                } catch (IOException e2) {
                    //重试一次还失败，则写入fail.txt
                    try {
                        Downloadmanhua.Writefailmsg(path,i+".jpg" , jpgurl);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }
    }

    /**
     * 将页面中图片地址json转为真实地址返回
     */
    public static List<String> tojpgurl(String jsonstr , String chapterPath){
        List<String> list = new ArrayList<String>();
        JSONArray json = new JSONArray(jsonstr);
        for (Object temp : json) {
            String url = rooturl + chapterPath +(String) temp;
            list.add(url);
        }
        return list;
    }
}
