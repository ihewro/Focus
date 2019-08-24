package com.ihewro.focus.util;

import android.Manifest;
import android.app.Activity;
import android.os.Handler;
import android.util.Xml;
import android.widget.Toast;

import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.blankj.ALog;
import com.google.common.base.Strings;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.activity.FeedManageActivity;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.ihewro.focus.util.FeedParser.skip;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/11
 *     desc   :
 *     thx: 该类大部分直接使用的feeder2 开源项目的代码OPMLHelper
 *     version: 1.0
 * </pre>
 */
public class OPMLReadHelper_backup {

    public static final int RQUEST_STORAGE_READ = 8;
    private static final String OPML = "opml";
    private static final String BODY = "body";
    private static final String OUTLINE = "outline";
    private static OPMLReadHelper_backup sInstance;

    private Activity activity;

    public OPMLReadHelper_backup(Activity activity) {
        this.activity = activity;
    }

    public void run(){
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            //有权限
            new FileChooserDialog.Builder(activity)
                    .initialPath(GlobalConfig.appDirPath)  //初始显示了目录
                    .extensionsFilter(".opml",".xml") //选择的文件类型
                    .tag("optional-identifier")
                    .goUpLabel("上一级")
                    .show((FeedManageActivity)activity);


        } else {
            //没有权限 1. 申请权限
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(activity, RQUEST_STORAGE_READ, perms)
                            .setRationale("必须读存储器才能解析OPML文件")
                            .setPositiveButtonText("确定")
                            .setNegativeButtonText("取消")
                            .build());
        }


    }
    public void add(String filePath) {

        if (Strings.isNullOrEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            parser.nextTag();
            List<FeedFolder> feedFolders = readOPML(parser);
            //保存到指定的文件夹中
            saveFeedToFeedFolder(feedFolders);

            Toasty.success(activity, "导入成功", Toast.LENGTH_SHORT).show();
        } catch (XmlPullParserException e) {

            Toast.makeText(activity, "文件格式错误", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, "文件导入失败", Toast.LENGTH_SHORT).show();
        }
    }

    private List<FeedFolder> readOPML(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<FeedFolder>feedFolders = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, OPML);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(BODY)) {
                feedFolders.addAll(readBody(parser));
            } else {
                skip(parser);
            }
        }
        return feedFolders;
    }

    private List<FeedFolder> readBody(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<FeedFolder> feedFolders = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, BODY);

        FeedFolder notFolder = new FeedFolder("");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(OUTLINE)) {
                String text = parser.getAttributeValue(null, "text");
                String type = parser.getAttributeValue(null, "type");
                if (type!=null){//没有文件夹

                }else {

                }
                feedFolders.add(readOutline(parser,text));
            } else {
                skip(parser);
            }
        }
        return feedFolders;
    }

    private FeedFolder readOutline(XmlPullParser parser, String feedFolderName) throws IOException, XmlPullParserException {

        FeedFolder feedFolder = new FeedFolder(feedFolderName);

        String type = parser.getAttributeValue(null, "type");
        if (Objects.equals(type, "rss")) {//只有一个订阅源，且没有分组
            feedFolder.getFeedList().add(parseFeed(parser,feedFolderName));
            parser.nextTag();
        } else {//是一个文件夹
            //我们只处理二级目录
            feedFolder.getFeedList().addAll(readFeedFolderOutLine(parser,feedFolderName));

        }
        return feedFolder;
    }


    /**
     * 读取文件夹下的所有feed
     * @param parser
     * @param feedFolderName
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private List<Feed> readFeedFolderOutLine (XmlPullParser parser, String feedFolderName) throws IOException, XmlPullParserException {
        List<Feed> feedList = new ArrayList<>();

        String type = parser.getAttributeValue(null, "type");
        if (Objects.equals(type, "rss")) {
            feedList.add(parseFeed(parser, feedFolderName));
            parser.nextTag();
        } else {
            parser.require(XmlPullParser.START_TAG, null, OUTLINE);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals(OUTLINE)) {
                    feedList.addAll(readFeedFolderOutLine(parser, feedFolderName));
                } else {
                    skip(parser);
                }
            }
        }
        return feedList;
    }

    private Feed parseFeed(XmlPullParser parser,String feedFolderName) {
        String text = parser.getAttributeValue(null, "text");
        String title = parser.getAttributeValue(null, "title");
        String xmlUrl = parser.getAttributeValue(null, "xmlUrl");
        String htmlUrl = parser.getAttributeValue(null, "htmlUrl");


        // may be null
        String description = parser.getAttributeValue(null, "description");

        String iconUrl;
        if (!Strings.isNullOrEmpty(htmlUrl)) {
            if (htmlUrl.endsWith("/")) {
                htmlUrl = htmlUrl.substring(0, htmlUrl.length() - 2);
            }
        }
        iconUrl = htmlUrl + "/favicon.ico";

        Feed feed = new Feed(title,xmlUrl,description, Feed.DEFAULT_TIMEOUT);

        return feed;
    }

    private void saveFeedToFeedFolder(final List<FeedFolder> feedFolders){
        //显示feedFolderList 弹窗

        for (int i = 0;i < feedFolders.size();i++){
            //首先检查这个feedFolder名称是否存在。
            FeedFolder feedFolder = feedFolders.get(i);
            List<FeedFolder> temp = LitePal.where("name = ?",feedFolder.getName()).find(FeedFolder.class);
            if (temp.size()<1){
                if (feedFolder.getName() == null || feedFolder.getName().equals("")){
                    feedFolder.setName("导入的未命名文件夹");
                }
                //新建一个feedFolder
                feedFolder.save();
            }else {//存在这个名称
                feedFolder.setId(temp.get(0).getId());//使用是数据库的对象
            }
            //将导入的feed全部保存
            for (Feed feed:feedFolder.getFeedList()) {
                feed.setFeedFolderId(feedFolder.getId());
                ALog.d(feed.getUrl());
                feed.save();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new EventMessage(EventMessage.IMPORT_OPML_FEED));
                }
            },500);

        }

        Toasty.success(activity,"导入成功！").show();
    }
}
