package com.covenant.tribe.service.impl.EventSearchService.pojo;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static com.covenant.tribe.service.impl.EventSearchService.pojo.SearchFields.TAGLIST;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchParams {
    private String text;

    private List<String> tokens;

    private List<SearchFunctionParam> fieldParams;

    private List<SearchFunctionTerm> terms;

    private Pageable pageable;

    private Map<String, List<String>> field2termList = new HashMap<>();

    private BoolQuery.Builder rootBool;

    private BoolQuery.Builder rootFilter;

    private static List<String> getTokens(String text) {
        return Arrays.stream(text.trim().split("[\\s-,|]+")).map(String::toLowerCase).toList();
    }

    public static SearchParamsBuilder builder() {
        return new SearchParamsBuilder();
    }

    public static class SearchParamsBuilder {
        private String text;
        private List<String> tokens;
        private List<SearchFunctionParam> fieldParams = new ArrayList<>();
        private List<SearchFunctionTerm> terms = new ArrayList<>();
        private Pageable pageable;
        private Map<String, List<String>> field2termList = new HashMap<>();
        private BoolQuery.Builder rootBool;

        private BoolQuery.Builder rootFilter;

        SearchParamsBuilder() {
        }

        public SearchParamsBuilder text(String text) {
            this.text = text;
            return this;
        }

        public SearchParamsBuilder tokens(List<String> tokens) {
            this.tokens = tokens;
            return this;
        }

        public SearchParamsBuilder fieldParams(List<SearchFunctionParam> fieldParams) {
            this.fieldParams = fieldParams;
            return this;
        }

        public SearchParamsBuilder terms(List<SearchFunctionTerm> terms) {
            this.terms = terms;
            return this;
        }

        public SearchParamsBuilder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public SearchParamsBuilder field2termList(Map<String, List<String>> Field2termList) {
            this.field2termList = Field2termList;
            return this;
        }

        public SearchParamsBuilder rootBool(BoolQuery.Builder queryBuilder) {
            this.rootBool = queryBuilder;
            return this;
        }

        public SearchParams build() {
            List<String> createdTokens = getTokens(this.text);
            if (this.tokens == null || tokens.isEmpty()) {
                this.tokens = createdTokens;
            }
            this.field2termList.put(TAGLIST.getName(), createdTokens);
            return new SearchParams(
                    this.text,
                    this.tokens,
                    this.fieldParams,
                    this.terms,
                    this.pageable,
                    this.field2termList,
                    this.rootBool,
                    this.rootFilter);
        }

        public String toString() {
            return "SearchParams.SearchParamsBuilder(text=" + this.text + ", tokens=" + this.tokens + ", fieldParams=" + this.fieldParams + ", pageable=" + this.pageable + ", ids=" + this.field2termList + ", queryBuilder=" + this.rootBool + ")";
        }

        public SearchParamsBuilder rootFilter(BoolQuery.Builder rootFilter) {
            this.rootFilter = rootFilter;
            return this;
        }
    }
}
