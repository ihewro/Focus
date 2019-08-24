package com.ihewro.focus.http;

import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.bean.Website;
import com.ihewro.focus.bean.WebsiteCategory;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface HttpInterface {

    @GET("")
    Call<String> getRSSData();

 /*   @GET("{with}")
    Call<String> getRSSDataWith(@Path(value = "with", encoded = true) String with);
*/

    @GET
    Call<String> getRSSDataWith(@Url String with);


    @GET("?action=webcategory")
    Call<List<WebsiteCategory>> getCategoryList();

    @GET("?action=weblist")
    Call<List<Website>> getWebsiteListByCategory(@Query("name") String name);

    @GET("?action=feedlist")
    Call<List<Feed>> getFeedListByWebsite(@Query("name") String name);

    @GET("?action=feedRequireList")
    Call<List<FeedRequire>> getFeedRequireListByWebsite(@Query("id") String id);

    @GET("?action=searchFeedListByName")
    Call<List<Feed>> searchFeedListByName(@Query("name") String name);

    @GET("?action=searchWebsiteByName")
    Call<List<Website>> searchWebsiteByName(@Query("name") String name);

}
