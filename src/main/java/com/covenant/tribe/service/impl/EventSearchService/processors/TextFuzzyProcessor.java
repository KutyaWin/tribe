package com.covenant.tribe.service.impl.EventSearchService.processors;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.util.ObjectBuilder;
import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchFunctionParam;
import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchParams;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields;

@Component
public class TextFuzzyProcessor extends AbstractSearchProcessor implements SearchProcessor{

    public TextFuzzyProcessor() {
        this.order = 1;
    }

    @Override
    public void process(SearchParams searchParams) {
        List<String> tokens = searchParams.getTokens();
        List<SearchFunctionParam> fieldParams = searchParams.getFieldParams();
        BoolQuery.Builder rootBool = searchParams.getRootBool();
        List<String> strings = fieldParams.stream().map(f -> f.getField()).toList();
        rootBool.should(q->q.multiMatch(
                mq->mq.fields(strings).fuzziness(String.valueOf(2)).query(searchParams.getText()).type(BestFields)));
        processNext(searchParams);
    }

    private static Function<FunctionScoreQuery.Builder, ObjectBuilder<FunctionScoreQuery>> getFunctionScore(String val, SearchFunctionParam param) {
        return fs -> fs.query(
                fq -> fq.fuzzy(
                        f -> f.field(param.getField()).value(val)
                )
        ).scoreMode(FunctionScoreMode.Sum).boost(param.getBoost());
    }
}
