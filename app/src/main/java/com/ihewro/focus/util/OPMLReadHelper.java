package com.ihewro.focus.util;

import android.Manifest;
import android.app.Activity;
import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.google.common.base.Strings;
import com.ihewro.focus.activity.FeedManageActivity;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.callback.DialogCallback;
import com.ihewro.focus.task.ShowFeedFolderListDialogTask;

import org.greenrobot.eventbus.EventBus;
import org.litepal.exceptions.LitePalSupportException;
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
public class OPMLReadHelper {

    public static final int RQUEST_STORAGE_READ = 8;
    private static final String OPML = "opml";
    private static final String BODY = "body";
    private static final String OUTLINE = "outline";
    private static OPMLReadHelper sInstance;

    private Activity activity;

    public OPMLReadHelper(Activity activity) {
        this.activity = activity;
    }

    public void run(){
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            //有权限
            new FileChooserDialog.Builder(activity)
                    .initialPath(Environment.getExternalStorageDirectory().getAbsolutePath())  //初始显示了目录
                    .extensionsFilter(".opml") //选择的文件类型
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
            List<Feed> feedList = readOPML(parser);
            //保存到指定的文件夹中
            saveFeedToFeedFolder(feedList);

            Toast.makeText(activity, "导入成功", Toast.LENGTH_SHORT).show();
        } catch (XmlPullParserException e) {

            Toast.makeText(activity, "文件格式错误", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, "文件导入失败", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Feed> readOPML(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Feed> feedList = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, OPML);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(BODY)) {
                feedList.addAll(readBody(parser));
            } else {
                skip(parser);
            }
        }
        return feedList;
    }

    private List<Feed> readBody(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Feed> feedList = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, BODY);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(OUTLINE)) {
                feedList.addAll(readOutline(parser,""));
            } else {
                skip(parser);
            }
        }
        return feedList;
    }

    private List<Feed> readOutline(XmlPullParser parser, String feedFolderName) throws IOException, XmlPullParserException {
        List<FeedFolder> feedFolders = new ArrayList<>();

        List<Feed> feedList = new ArrayList<>();
        String type = parser.getAttributeValue(null, "type");
        if (Objects.equals(type, "rss")) {//单个rss订阅源
            feedList.add(parseFeed(parser,feedFolderName));
            parser.nextTag();
        } else {//是一个文件夹
            parser.require(XmlPullParser.START_TAG, null, OUTLINE);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                //可能是null;
                feedFolderName = parser.getAttributeValue(null, "text");
                // Starts by looking for the entry tag
                if (name.equals(OUTLINE)) {
                    feedList.addAll(readOutline(parser,feedFolderName));
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

        Feed feed = new Feed(title,xmlUrl,description);
        feed.setIid();

        return feed;
    }

    private void saveFeedToFeedFolder(final List<Feed> feedList){
        //显示feedFolderList 弹窗
        new ShowFeedFolderListDialogTask(new DialogCallback() {
            @Override
            public void onFinish(MaterialDialog dialog, View view, int which, CharSequence text, int targetId) {
                //移动到指定的目录下

                for (int i =0; i < feedList.size();i++){
                    Feed feed = feedList.get(i);
                    feed.setIid();//否则会出现主键重复
                    feed.setFeedFolderId(targetId);
                    try{
                        feed.save();
                        Toasty.success(UIUtil.getContext(),"订阅成功").show();
                        EventBus.getDefault().post(new EventMessage(EventMessage.ADD_FEED));
                    }catch (LitePalSupportException exception){
                        Toasty.info(activity,feed.getName()+"该订阅已经存在了哦！").show();
                    }
                }

                EventBus.getDefault().post(new EventMessage(EventMessage.ADD_FEED));
            }
        }, activity,"添加到指定的文件夹下","").execute();
    }
}
