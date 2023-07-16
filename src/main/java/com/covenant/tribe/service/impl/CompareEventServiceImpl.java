package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.EventComparisonDto;
import com.covenant.tribe.dto.event.ExternalEventDescription;
import com.covenant.tribe.service.CompareEventService;

import java.util.List;

public class CompareEventServiceImpl implements CompareEventService {
    @Override
    public List<ExternalEventDescription> getExternalEventForAdding(List<EventComparisonDto> eventsFromDb, List<KudagoEventDto> eventsToDb) {

    }

    private Double calculateSimilarityInPercent(
            EventComparisonDto eventFromDb, KudagoEventDto eventToDb
    ) {
        String eventFromDbDescription = eventFromDb.eventDescription();
        String eventToDbDescription = eventToDb.getDescription();
        int[][] dp = new int[eventFromDbDescription.length() + 1]
                [eventToDbDescription.length() + 1];

        for (int i = 0; i <= eventFromDbDescription.length(); i++) {
            for (int j = 0; j <= eventToDbDescription.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (eventFromDbDescription.charAt(i - 1)
                                    == eventToDbDescription.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }

        int levenshteinDistance = dp[eventFromDbDescription.length()][eventToDbDescription.length()];
        double similarityPercentage = (
                1 - (double) levenshteinDistance / Math.max(eventFromDbDescription.length(), eventToDbDescription.length())
        ) * 100;
        return similarityPercentage;
    }
};
