package com.example.films.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class GlobalStatisticsDTO {
    private Map<String, Object> kpi; // Garder comme Map
    private Map<String, StatisticItem> statistics;
    private List<ProjectRanking> topProjects;
    private List<TeamDistribution> teamDistribution;
    private List<ProjectDetail> projectsDetail;
    private List<MonthlyProgress> monthlyProgress;


    @Data
    public static class StatisticItem {
        private Long total;
        private Map<String, Double> percentages;
        private Map<String, Long> counts;
    }

    @Data
    public static class ProjectRanking {
        private Long id;
        private String nom;
        private Long episodes;
        private Long scenes;
        private Double progression;
    }

    @Data
    public static class TeamDistribution {
        private String nom;
        private Long count;
        private Double percentage;
    }

    @Data
    public static class ProjectDetail {
        private Long id;
        private String nom;
        private String description;
        private Long episodes;
        private Long episodesTermines;
        private Long sequences;
        private Long scenes;
        private Long scenesTerminees;
        private Long dialogues;
        private Double progression;
        private List<TeamMember> equipe;
        private String lastUpdate;
    }

    @Data
    public static class TeamMember {
        private Long id;
        private String nom;
    }

    @Data
    public static class MonthlyProgress {
        private String mois;
        private Long episodes;
        private Long sequences;
        private Long scenes;
    }

    // Ajouter dans GlobalStatisticsDTO.java
        
    @Data
    public static class ProjectTotals {
        private String projetNom;
        private Long totalEpisodes;
        private Long totalSequences;
        private Long totalScenes;
        private Long totalDialogues;
        private Long totalRealisateurs;
        private Long totalScenaristes;
        // getters/setters
    }
    
    @Data
    public static class DetailedStatusStats {
        private Long episodesValides;
        private Long episodesTournage;
        private Long scenesValidees;
        private Long scenesTournage;
        private Long scenesPlanifiees;
        private Long scenesEcrites;
        // getters/setters
    }
    
    @Data
    public static class TimelineStats {
        private String periode;
        private Long nouvellesScenes;
        private Long scenesTerminees;
        private Long progressionMoyenne;
        // getters/setters
    }
}
