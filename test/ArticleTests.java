import controllers.routes;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;

public class ArticleTests extends WithApplication{

    @Test
    public void getAllArticlesTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.ArticleController.getAllArticles().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getRecentArticlesTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.ArticleController.getRecentArticles().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }
}
