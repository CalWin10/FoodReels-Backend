package com.foodreels.backend.integration;

import com.foodreels.backend.entity.*;
import com.foodreels.backend.support.BackendIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecommendationIntegrationTests extends BackendIntegrationTest {
    @Test
    void recommendations_shouldPrioritizePreferredCategory() throws Exception {
        var preferred = reel(food, "Preferred", LocalDateTime.now().minusHours(6), 0);
        reel(food(restaurant, "Salad", "SALAD", 80), "Newer unrelated", LocalDateTime.now(), 0);
        preference();
        mvc.perform(get("/api/reels/personalized").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(preferred.getId()));
        mvc.perform(get("/api/recommendations/scores").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].category").value("PIZZA"))
                .andExpect(jsonPath("$[0].score").value(6));
    }

    @ParameterizedTest
    @CsvSource({"likes,3,likeCategoryCounts", "saves,5,saveCategoryCounts", "comments,4,commentCategoryCounts", "view,1,watchCategoryCounts"})
    void recommendations_shouldScoreAuthenticatedUserInteractions(String action, int expected, String field) throws Exception {
        var reel = reel(food, "Interactive", LocalDateTime.now(), 0);
        var request = post("/api/reels/{id}/" + action, reel.getId()).with(as(customer));
        if (action.equals("comments")) request.contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"Delicious\"}");
        mvc.perform(request).andExpect(status().is(action.equals("comments") ? 201 : 200));
        mvc.perform(get("/api/recommendations/scores").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].score").value(expected));
        mvc.perform(get("/api/recommendations/profile").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(customer.getId()))
                .andExpect(jsonPath("$." + field + ".PIZZA").value(1));
        mvc.perform(get("/api/recommendations/scores").with(as(otherCustomer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @ParameterizedTest
    @CsvSource({"0,0", "1,1", "2,1", "3,3", "4,3", "5,5", "8,5"})
    void recommendations_shouldApplyWatchPenaltyThresholds(int count, int penalty) throws Exception {
        var watched = reel(food, "Watched", LocalDateTime.now(), 0);
        if (count > 0) watch(watched, count);
        mvc.perform(get("/api/recommendations/reels").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].watchPenalty").value(penalty))
                .andExpect(jsonPath("$[0].categoryScore").value(count))
                .andExpect(jsonPath("$[0].totalScore").value(count + 5 - penalty));
    }

    @Test
    void recommendations_shouldRankUnwatchedReelAboveFrequentlyWatchedReel() throws Exception {
        var unseen = reel(food, "Unseen", LocalDateTime.now().minusHours(1), 0);
        var watched = reel(food, "Repeated", LocalDateTime.now(), 0);
        watch(watched, 5);
        mvc.perform(get("/api/reels/personalized").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(unseen.getId()));
    }

    @ParameterizedTest
    @CsvSource({"0,5", "1,5", "2,3", "7,3", "8,1", "30,1", "31,0"})
    void recommendations_shouldApplyRecencyThresholds(int days, int score) throws Exception {
        reel(food, "Age test", LocalDateTime.now().minusDays(days).minusMinutes(5), 0);
        mvc.perform(get("/api/recommendations/reels").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].recencyScore").value(score));
    }

    @ParameterizedTest
    @CsvSource({"0,0", "1,1", "4,1", "5,2", "19,2", "20,3", "99,3", "100,5"})
    void recommendations_shouldApplyPopularityThresholds(long views, int score) throws Exception {
        reel(food, "Popular", LocalDateTime.now(), views);
        mvc.perform(get("/api/recommendations/reels").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].popularityScore").value(score));
    }

    @Test
    void recommendations_shouldFavorRecencyAndBreakScoreTiesByNewestFirst() throws Exception {
        preference();
        var old = reel(food, "Old", LocalDateTime.now().minusDays(10), 0);
        var recent = reel(food, "Recent", LocalDateTime.now().minusHours(5), 0);
        var newest = reel(food, "Newest", LocalDateTime.now().minusHours(1), 0);
        mvc.perform(get("/api/reels/personalized").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(newest.getId()))
                .andExpect(jsonPath("$.content[1].id").value(recent.getId()))
                .andExpect(jsonPath("$.content[2].id").value(old.getId()));
    }

    @Test
    void recommendations_shouldBreakIdenticalTimestampTiesByHigherId() throws Exception {
        preference();
        var time = LocalDateTime.now().minusHours(2);
        reel(food, "First", time, 0);
        var second = reel(food, "Second", time, 0);
        mvc.perform(get("/api/reels/personalized").with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(second.getId()));
    }

    @Test
    void recommendations_shouldUseColdStartNewestFeedAndPaginate() throws Exception {
        var old = reel(food, "Old popular", LocalDateTime.now().minusDays(2), 100);
        var latest = reel(food, "New", LocalDateTime.now(), 0);
        mvc.perform(get("/api/reels/personalized").with(as(customer)).param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(latest.getId()));
        mvc.perform(get("/api/reels/personalized").with(as(customer)).param("size", "1").param("page", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(old.getId()));
        mvc.perform(get("/api/reels/personalized").with(as(customer)).param("size", "1").param("page", "2"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void recommendations_shouldNormalizePagination() throws Exception {
        mvc.perform(get("/api/reels/personalized").with(as(customer)).param("page", "-1").param("size", "0"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
        mvc.perform(get("/api/reels/personalized").with(as(customer)).param("size", "100"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.size").value(50));
    }

    private void preference() throws Exception {
        mvc.perform(post("/api/preferences").with(as(customer)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category\":\"pizza\"}"))
                .andExpect(status().isCreated());
    }

    private void watch(Reel reel, int count) {
        WatchHistory history = new WatchHistory();
        history.setUser(customer);
        history.setReel(reel);
        history.setWatchCount(count);
        history.setFirstWatchedAt(LocalDateTime.now().minusDays(1));
        history.setLastWatchedAt(LocalDateTime.now());
        em.persist(history);
        em.flush();
    }
}
