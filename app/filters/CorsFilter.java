package filters;

import play.filters.cors.CORSFilter;
import play.http.DefaultHttpFilters;
import javax.inject.Inject;

public class CorsFilter extends DefaultHttpFilters {
    @Inject
    public CorsFilter(CORSFilter corsFilter) {
        super(corsFilter);
    }
}
