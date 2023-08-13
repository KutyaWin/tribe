package com.covenant.tribe.service.impl.EventSearchService.processors;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsSetQuery;
import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchFunctionTerm;
import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchParams;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IdsFilterSearchProcessor extends AbstractSearchProcessor implements SearchProcessor {

    public IdsFilterSearchProcessor() {
        this.order = 2;
    }

    @Override
    public void process(SearchParams searchParams) {
        List<SearchFunctionTerm> terms = searchParams.getTerms();
        for (SearchFunctionTerm term : terms) {
            String field = term.getField();
                handleTerms(term, searchParams, field);
        }
        processNext(searchParams);
    }

    private static void handleTerms(SearchFunctionTerm term, SearchParams searchParams, String field) {
        List<String> strings= searchParams.getField2termList().get(field);
        if (strings.size() > 0) {
            TermsSetQuery.Builder terms = QueryBuilders.termsSet().field(field)
                    .terms(strings)
                    .minimumShouldMatchScript(
                            s -> s.inline(
                                    i -> i.source(String.valueOf(1))
                            )
                    );
            if (!term.getIsOptional()) {
                BoolQuery.Builder rootFilter = searchParams.getRootFilter();
                rootFilter.must(t -> t.termsSet(terms.build()));
            } else {
                BoolQuery.Builder rootBool = searchParams.getRootBool();
                rootBool.should(f -> f.termsSet(terms.build()));
            }
        }
    }
}
