import controllers.routes;
import org.junit.Test;
import play.core.j.JavaResultExtractor;
import play.mvc.Http;
import play.mvc.Result;
import play.test.*;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class GetAllTests extends WithApplication {

    @Test
    public void getAllArticlesTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.ArticleController.getAllArticles().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getAllBooksTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.BookController.getAllBooks().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getAllElectronicsTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.ElectronicController.getAllElectronics().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getAllOfficeSuppliesTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.OfficeSupplyController.getAllOfficeSupplies().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getAllOtherArticlesTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.OtherArticleController.getAllOtherArticles().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getAllBooksTestWithWrongURI(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/artics/books");
        Result result = route(request);

        assertEquals(NOT_FOUND,result.status());
    }

}
