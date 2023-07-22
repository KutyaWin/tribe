package com.covenant.tribe.dto;

public enum EventCategory {

    BUSINESS_EVENTS("business-events", "Обучение"),
    CINEMA("cinema", "Кино"),
    CONCERTS("concert", "Фестиваль"),
    EDUCATION("education", "Обучение"),
    ENTERTAINMENT("entertainment", "Прогулки"),
    EXHIBITIONS("exhibition", "Выставка"),
    FASHION("fashion", "Выставка"),
    FESTIVALS("festival", "Фестиваль"),
    HOLIDAYS("holiday", "Посиделки"),
    KIDS("kids", "Для детей"),
    OTHER("other", "Посиделки"),
    PARTIES("party", "Фестиваль"),
    PHOTOGRAPHY("photo", "Выставка"),
    QUESTS("quest", "Прогулки"),
    RECREATION("recreation", "Прогулки"),
    SHOPPING("shopping", "Прогулки"),
    SOCIAL_ACTIVITY("social-activity", "Религия"),
    STOCKS("stock", "Stocks"), // Убрать из мероприятий шв 25
    THEATER("theater", "Кино"),
    TOURS("tour", "Посиделки"),
    FAIRS("yarmarki-razvlecheniya-yarmarki", "Выставка");

    private final String kudaGoName;
    private final String tribeName;

    EventCategory(String kudaGoName, String tribeName) {
        this.kudaGoName = kudaGoName;
        this.tribeName = tribeName;
    }

    public String getKudaGoName() {
        return kudaGoName;
    }

    public String getTribeName() {
        return tribeName;
    }

}
