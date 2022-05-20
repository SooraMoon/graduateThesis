package cn.py_gufengmanhua9;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Downloadmanhua {

    /*记录下载错误的数据的文本地址*/
    static String failtxt ;
    /**
     * Http get请求
     * @param httpUrl 连接
     * @return 响应数据
     */
    public static String doGet(String httpUrl){
        //链接
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            //创建连接
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            //设置连接超时时间
            connection.setReadTimeout(15000);
            //开始连接
            connection.connect();
            //获取响应数据
            if (connection.getResponseCode() == 200) {
                //获取返回的数据
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭远程连接
            connection.disconnect();
        }
        return result.toString();
    }


    /**
     * @Title: getImg
     * @Description: 通过一个url 去获取图片
     * @param url 图片的连接地址
     * @param filename 图片的名字
     * @param path 根路径
     * @param lenpath 创建章节文件夹
     * @param flag 为true是最后一次机会，为false则是第一次请求
     * @param @throws IOException
     * @throws 网络加载失败
     * return true为无需重复，false为返回状态吗非200，可再试一次
     */
    public static boolean getImg(String url , String filename , String path , String lenpath , boolean flag) throws IOException{
        Downloadmanhua.failtxt = path;
        path = path+lenpath;
        long startTime = System.currentTimeMillis();
        URL imgURL = new URL(url.trim());//转换URL
        HttpURLConnection urlConn = (HttpURLConnection) imgURL.openConnection();//构造连接
        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36");
        //更新了几条头信息，以便通过服务器检查
        urlConn.setRequestProperty("Accept","image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8");
        urlConn.setRequestProperty("Referer","https://www.gufengmh9.com/");
        urlConn.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        urlConn.connect();
        File pathfile = new File(path);
        if(!pathfile.exists()){//如果文件夹不存在
            pathfile.mkdir();//创建文件夹
        }
//    System.out.println(Downloadmanhua.class.toString()+":获取连接="+urlConn.getResponseMessage());
        if(urlConn.getResponseCode()==200){//返回的状态码是200 表示成功
            InputStream ins = urlConn.getInputStream(); //获取输入流,从网站读取数据到 内存中
            OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(path+"\\"+filename)));
            int len=0;
            byte[] buff = new byte[1024*100];//100k缓冲流 视你内存大小而定咯

            while(-1!=(len=(new BufferedInputStream(ins)).read(buff))){//长度保存到len,内容放入到 buff
                out.write(buff, 0, len);//将图片数组内容写入到图片文件
                out.flush();
            }
            urlConn.disconnect();
            ins.close();
            out.close();
            System.out.println(Downloadmanhua.class.toString()+"：获取"+filename+"完成,耗时="+((System.currentTimeMillis()-startTime)/1000)+"s");
            return true;
        }else {
            if (!flag) {
                return false;
            }
            Writefailmsg(path, filename, url);
            System.out.println("获取"+filename+"文件失败!!!!!!"+url);
            return true;
        }
    }

    /**
     * 写入错误信息
     * @param filepath 文件夹名
     * @param filename 文件名
     * @param url 请求连接
     * @throws IOException
     */
    static void Writefailmsg(String filepath , String filename , String url) throws IOException{
        File cmdtxt=new File(failtxt+"fail.txt");
        if (!cmdtxt.exists()) {
            cmdtxt.createNewFile();
        }
        FileWriter fw = new FileWriter(cmdtxt, true);
        PrintWriter pw = new PrintWriter(fw);
        pw.println(filepath + "\\" + filename + "&&" + url);
        pw.flush();

        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
