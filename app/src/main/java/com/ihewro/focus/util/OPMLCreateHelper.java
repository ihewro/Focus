package com.ihewro.focus.util;

import android.Manifest;
import android.app.Activity;

import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;

import org.litepal.LitePal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/11
 *     desc   : 导出OPML文件
 *     version: 1.0
 * </pre>
 */
public class OPMLCreateHelper {

    public static final int REQUEST_STORAGE_WRITE = 210;

    private Activity activity;
    public OPMLCreateHelper() {
    }

    public OPMLCreateHelper(Activity activity) {
        this.activity = activity;
    }

    public void run(){
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            //有权限
           add();
        } else {
            //没有权限 1. 申请权限
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(activity, REQUEST_STORAGE_WRITE, perms)
                            .setRationale("必须允许写存储器才能导出OPML文件")
                            .setPositiveButtonText("确定")
                            .setNegativeButtonText("取消")
                            .build());
        }
    }

    private void add() {
        try {
            String name = DateUtil.getNowDateStr();//日期

            String outPutPath = GlobalConfig.appXMLPath+"export"+name+".xml";

            File outFile = new File(outPutPath);
            if (outFile.exists()){
                outFile.delete();
            }
            boolean flag = outFile.createNewFile();
            if (flag){
                FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                createXml(fileOutputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void createXml(OutputStream outputStream){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            document.setXmlStandalone(true);


            //<opml version="1.0">
            Element opml = document.createElement("opml");
            opml.setAttribute("version","1.0");

            //  <head>
            //    <title>walterlv</title>
            //  </head>

            Element head = document.createElement("head");
            Element title = document.createElement("title");
            title.setTextContent("Focus");
            opml.appendChild(head);

            //  <body>
            Element body = document.createElement("body");


            List<FeedFolder>feedFolders = LitePal.findAll(FeedFolder.class);

            for (FeedFolder feedFolder:feedFolders) {
                Element outLine = document.createElement("outline");
                outLine.setAttribute("title",feedFolder.getName());
                outLine.setAttribute("text",feedFolder.getName());
                //查询该文件夹下的所有订阅
                List<Feed> feedList = LitePal.where("feedfolderid = ?", String.valueOf(feedFolder.getId())).find(Feed.class);
                for (Feed feed: feedList) {
                    Element childOutLine = document.createElement("outline");
                    childOutLine.setAttribute("type","rss");
                    childOutLine.setAttribute("title",feed.getName());
                    childOutLine.setAttribute("text",feed.getName());
                    childOutLine.setAttribute("xmlUrl",feed.getUrl());
                    outLine.appendChild(childOutLine);
                }
                body.appendChild(outLine);
            }


            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer transformer = tff.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");//开启缩进
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");//设置编码格式

            DOMSource domSource = new DOMSource(document);
            PrintWriter printWriter = new PrintWriter(outputStream);
            StreamResult streamResult = new StreamResult(printWriter);
            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


}
