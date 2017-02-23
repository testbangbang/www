package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.*;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.OnyxBookStoreService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.TestUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;

/**
 * Created by zhuzeng on 8/10/16.
 */
public class BookStoreTest extends ApplicationTestCase<Application> {

    static OnyxBookStoreService service;
    static List<Product> productList;
    static OnyxAccount currentAccount;

    public BookStoreTest() {
        super(Application.class);
    }

    private final OnyxBookStoreService getService() {
        if (service == null) {
            CloudManager cloudManager = new CloudManager();
            service = ServiceFactory.getBookStoreService(cloudManager.getCloudConf().getApiBase());
        }
        return service;
    }

    public static OnyxAccount getCurrentAccount() throws Exception {
        if (currentAccount == null || currentAccount.sessionToken == null) {
            currentAccount = AccountTest.testSignUpRequest();
        }
        return currentAccount;
    }

    public void testBookList() throws Exception {
        Call<ProductResult<Product>> object = getService().bookList(JSON.toJSONString(new ProductQuery()));
        Response<ProductResult<Product>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        productList = response.body().list;
        assertNotNull(productList);
    }

    public void testSingleBook() throws Exception {
        final String uniqueId = productList.get(0).getGuid();
        Call<Product> object = getService().book(uniqueId);
        Response<Product> response = object.execute();
        assertNotNull(response.body());
        Product product = response.body();
        assertTrue(product.getGuid().equals(uniqueId));

        assertNotNull(product.formats);
        assertTrue(product.formats.size() > 0);
        String type = product.formats.toArray(new String[]{})[0];

        // test covers and storage
        assertNotNull(product.covers);
        Link link = product.getDownloadLink(Constant.COVER_TYPE_NORMAL, Constant.DEFAULT_CLOUD_STORAGE);
        assertNotNull(link);
        assertEquals(Constant.DEFAULT_CLOUD_STORAGE, link.provider);

        //test get book cover bytes
        Response<ResponseBody> byteResponse = getService().getBookCover(uniqueId, Constant.COVER_TYPE_NORMAL).execute();
        assertTrue(byteResponse.isSuccessful());
        assertTrue(byteResponse.body().bytes().length == link.size);

        //test get book file bytes
        link = product.getDownloadLink(type, link.provider);
        byteResponse = getService().getBookBytes(uniqueId, type, getCurrentAccount().sessionToken).execute();
        assertTrue(byteResponse.isSuccessful());
        assertTrue(byteResponse.body().bytes().length == link.size);

        //test book comment list
        Response<ProductResult<Comment>> commentListResponse = getService().getBookCommentList(uniqueId, null).execute();
        assertNotNull(commentListResponse.body());
        ProductResult<Comment> commentList = commentListResponse.body();
        assertNotNull(commentList);
        assertTrue(commentList.list.size() >= 0);

        // test one comment
        if (commentList.list.size() > 0) {
            Comment comment = commentList.list.get(0);
            String commentId = comment.getGuid();
            Response<Comment> commentResponse = getService().getBookComment(uniqueId, commentId).execute();
            assertTrue(commentResponse.isSuccessful());
            assertNotNull(commentResponse.body());
            Comment testComment = commentResponse.body();
            assertEquals(testComment.title, comment.title);
        }

        //test post comment
        Comment comment = new Comment("三体", "中国科幻巅峰");
        Response<Comment> postResponse = getService().postBookComment(uniqueId, comment, getCurrentAccount().sessionToken).execute();
        assertNotNull(postResponse);
        Comment postComment = postResponse.body();
        assertNotNull(postComment);
        assertEquals(postComment.productId, uniqueId);
        assertEquals(postComment.title, comment.title);
        //接口没有返回account的guid
        //assertEquals(postComment.commentatorId, getCurrentAccount().getGuid());

        //test update comment
        String updateContent = postComment.content + "近十年来";
        Comment testComment = new Comment();
        testComment.content = updateContent;
        testComment.title = postComment.title;
        testComment.rating = 4;
        postResponse = getService().updateBookComment(uniqueId, postComment.getGuid(), testComment, getCurrentAccount().sessionToken).execute();
        assertNotNull(postResponse.body());
        Comment updateComment = postResponse.body();
        assertEquals(updateComment.getGuid(), postComment.getGuid());
        assertEquals(updateComment.content, testComment.content);

        //test up comment
        postResponse = getService().supportBookComment(uniqueId, updateComment.getGuid(), getCurrentAccount().sessionToken).execute();
        assertNotNull(postResponse);
        assertNotNull(postResponse.body());
        Comment upComment = postResponse.body();
        assertTrue(upComment.upCount > updateComment.upCount);

        //test down comment
        postResponse = getService().disagreeBookComment(uniqueId, updateComment.getGuid(), getCurrentAccount().sessionToken).execute();
        assertNotNull(postResponse);
        assertNotNull(postResponse.body());
        Comment downComment = postResponse.body();
        assertTrue(downComment.downCount > updateComment.downCount);

        //test delete one book comment
        Response<ResponseBody> deleteResponse = getService().deleteBookComment(uniqueId, downComment.getGuid(), getCurrentAccount().sessionToken).execute();
        assertNotNull(deleteResponse);
        assertTrue(deleteResponse.isSuccessful());
        postResponse = getService().getBookComment(uniqueId, downComment.getGuid()).execute();
        assertNotNull(postResponse);
        assertNull(postResponse.body());
    }

    // 推荐还没有内容
    public void testBookRecommendedList() throws Exception {
        ProductQuery query = new ProductQuery();
        query.count = 6;
        Response<ProductResult<Product>> productResult = getService().bookRecommendedList(JSON.toJSONString(query)).execute();
        assertNotNull(productResult.body());
        List<Product> list = productResult.body().list;
        assertNotNull(list);
        assertTrue(list.size() <= query.count);
    }

    public void testBookListWithProductQuery() throws Exception {
        int count = TestUtils.randInt(4, 8);
        ProductQuery productQuery = new ProductQuery();
        productQuery.count = count;
        productQuery.sortOrder = false;
        Call<ProductResult<Product>> object = getService().bookList(JSON.toJSONString(productQuery));
        Response<ProductResult<Product>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        List<Product> list = response.body().list;
        assertNotNull(list);
        assertTrue(list.size() <= count);

        Product tmp = list.get(0);
        for (int i = 0; i < list.size(); i++) {
            assertTrue(tmp.getCreatedAt().getTime() >= list.get(i).getCreatedAt().getTime());
            tmp = list.get(i);
        }
    }

    //暂时不可用
    /*public void testBookSearch() throws Exception {
        ProductSearch productSearch = new ProductSearch();
        productSearch.limit = 5;
        productSearch.pattern = "三";
        Call<ProductResult<Product>> object = getService().bookSearch(JSON.toJSONString(productSearch));
        Response<ProductResult<Product>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        assertNotNull(response.body().list);
        assertTrue(response.body().list.size() <= 5);
    }*/

    public void testContainerList() throws Exception {
        Call<List<Category>> call = getService().bookContainerList();
        Response<List<Category>> response = call.execute();
        assertNotNull(response.body());
        assertTrue(response.body().size() > 0);
        List<Category> list = response.body();
        assertNotNull(list);
        assertTrue(list.size() > 0);

        //test one container
        Response<Category> categoryResponse = getService().bookContainer(list.get(0).getGuid()).execute();
        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.body());
        Category category = categoryResponse.body();
        category.save();
        assertEquals(category.name, list.get(0).name);
        if (category.children != null && category.children.size() > 0) {
            for (Category child : category.children) {
                assertEquals(child.parentGuid, category.getGuid());
            }
        }
    }

    private void runProductSharedListTest(BaseQuery query) throws Exception {
        Response<ProductResult<ProductShared>> resp = getService().productSharedList(JSON.toJSONString(query)).execute();
        assertNotNull(resp);
        assertNotNull(resp.body());
        assertNotNull(resp.body().list);

        List<ProductShared> list = resp.body().list;
        assertTrue(list.size() <= query.count);
        if (list.size() > 0) {
            ProductShared tmp = list.get(0);
            for (int i = 0; i < list.size(); i++) {
                assertNotNull(tmp.productId);
                assertNotNull(tmp.productTitle);
                assertNotNull(tmp.productName);
                assertTrue(tmp.productSize > 0);
                assertNotNull(tmp.sharerId);
                assertNotNull(tmp.sharerName);
                assertTrue(tmp.shareDate.getTime() >= list.get(i).shareDate.getTime());
                tmp = list.get(i);
            }
        }
    }

    public void testProductSharedList() throws Exception {
        BaseQuery query = new BaseQuery();
        query.sortOrder = false;
        query.sortBy = "shareDate";//basedOn ProductShared.shareDate method
        int testCount = 0;
        while (testCount != 10) {
            runProductSharedListTest(query);
            query.offset += query.count;
            testCount++;
        }
    }
}
