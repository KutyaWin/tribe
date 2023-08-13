package com.covenant.tribe.service.impl.EventSearchService.pojo;

import lombok.Getter;

@Getter
public enum SearchFields {
    IDS("id"), TAGLIST("taglist");

    private String name;

    SearchFields(String name) {
        this.name = name;
    }
}
