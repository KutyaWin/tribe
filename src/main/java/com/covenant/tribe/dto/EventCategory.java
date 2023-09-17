package com.covenant.tribe.dto;

public enum EventCategory {

    BUSINESS_EVENTS("business-events", "Обучение"),
    CINEMA("cinema", "Культура"),
    CONCERTS("concert", "Культура"),
    EDUCATION("education", "Обучение"),
    ENTERTAINMENT("entertainment", "Прогулки"),
    EXHIBITIONS("exhibition", "Культура"),
    FASHION("fashion", "Культура"),
    FESTIVALS("festival", "Культура"),
    HOLIDAYS("holiday", "Посиделки"),
    KIDS("kids", "Для детей"),
    OTHER("other", "Другое"),
    PARTIES("party", "Посиделки"),
    PHOTOGRAPHY("photo", "Культура"),
    QUESTS("quest", "Игры"),
    RECREATION("recreation", "Посиделки"),
    SHOPPING("shopping", "Шопинг"),
    SOCIAL_ACTIVITY("social-activity", "Другое"),
    STOCKS("stock", "Другое"), // Убрать из мероприятий шв 25
    THEATER("theater", "Культура"),
    TOURS("tour", "Поездки"),
    FAIRS("yarmarki-razvlecheniya-yarmarki", "Культура");

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
