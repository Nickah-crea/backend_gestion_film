package com.example.films.service;

import com.example.films.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final JdbcTemplate jdbcTemplate;
    
    public GlobalStatisticsDTO getGlobalStatistics(Long projetId, String periode) {
        log.info("Chargement des statistiques globales - projetId: {}, periode: {}", projetId, periode);
        
        GlobalStatisticsDTO stats = new GlobalStatisticsDTO();
        
        try {
            stats.setKpi(getKPIData());
            stats.setStatistics(getStatistics(projetId));
            stats.setTopProjects(getTopProjects());
            stats.setTeamDistribution(getTeamDistribution());
            stats.setProjectsDetail(getProjectsDetail());
            stats.setMonthlyProgress(getMonthlyProgress(periode));
        } catch (Exception e) {
            log.error("Erreur lors du chargement des statistiques", e);
            throw e;
        }
        
        return stats;
    }
    
    public Map<String, Object> getKPIData() {
    try {
        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM projets) as total_projets,
                (SELECT COUNT(*) FROM episodes) as total_episodes,
                (SELECT COUNT(*) FROM scenes) as total_scenes,
                (SELECT COUNT(DISTINCT id_utilisateur) FROM utilisateurs WHERE role IN ('REALISATEUR', 'SCENARISTE')) as total_equipe,
                (SELECT COUNT(*) FROM sequences) as total_sequences,
                (SELECT COUNT(*) FROM dialogues) as total_dialogues
            """;
        
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        
        // Transform keys to camelCase
        Map<String, Object> camelCaseResult = new HashMap<>();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            String camelKey = toCamelCase(entry.getKey());
            camelCaseResult.put(camelKey, entry.getValue());
        }
        
        log.info("KPI data loaded: {}", camelCaseResult);
        return camelCaseResult;
    } catch (Exception e) {
        log.error("Erreur lors du chargement des KPI", e);
        // Retourner des valeurs par défaut (also in camelCase)
        Map<String, Object> defaultKpi = new HashMap<>();
        defaultKpi.put("totalProjets", 0);
        defaultKpi.put("totalEpisodes", 0);
        defaultKpi.put("totalScenes", 0);
        defaultKpi.put("totalEquipe", 0);
        defaultKpi.put("totalSequences", 0);
        defaultKpi.put("totalDialogues", 0);
        return defaultKpi;
    }
}

// Helper method
private String toCamelCase(String snakeCase) {
    String[] parts = snakeCase.split("_");
    StringBuilder camelCase = new StringBuilder(parts[0]);
    for (int i = 1; i < parts.length; i++) {
        camelCase.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
    }
    return camelCase.toString();
}
    
    private Map<String, GlobalStatisticsDTO.StatisticItem> getStatistics(Long projetId) {
        Map<String, GlobalStatisticsDTO.StatisticItem> statistics = new HashMap<>();
        
        // Statistiques des épisodes
        statistics.put("episodes", getEpisodeStatistics(projetId));
        
        // Statistiques des séquences
        statistics.put("sequences", getSequenceStatistics(projetId));
        
        // Statistiques des scènes
        statistics.put("scenes", getSceneStatistics(projetId));
        
        return statistics;
    }
    
    private GlobalStatisticsDTO.StatisticItem getEpisodeStatistics(Long projetId) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                COUNT(DISTINCT e.id_episode) as total,
                COUNT(DISTINCT CASE WHEN se.code = 'valide' THEN e.id_episode END) as termines,
                COUNT(DISTINCT CASE WHEN se.code IN ('tournage', 'monte') THEN e.id_episode END) as en_cours,
                COUNT(DISTINCT CASE WHEN se.code = 'planifie' THEN e.id_episode END) as planifies
            FROM episodes e
            LEFT JOIN episode_statuts es ON e.id_episode = es.id_episode AND es.date_fin IS NULL
            LEFT JOIN statuts_episode se ON es.id_statut = se.id_statut_episode
            """);
        
        Object[] params = {};
        if (projetId != null) {
            sql.append(" WHERE e.id_projet = ?");
            params = new Object[]{projetId};
        }
        
        Map<String, Object> result = jdbcTemplate.queryForMap(sql.toString(), params);
        return createStatisticItem(result);
    }
    
    private GlobalStatisticsDTO.StatisticItem getSequenceStatistics(Long projetId) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                COUNT(DISTINCT s.id_sequence) as total,
                COUNT(DISTINCT CASE WHEN ss.code = 'montee' THEN s.id_sequence END) as termines,
                COUNT(DISTINCT CASE WHEN ss.code IN ('tournage', 'pretee') THEN s.id_sequence END) as en_cours
            FROM sequences s
            JOIN episodes e ON s.id_episode = e.id_episode
            LEFT JOIN sequence_statuts sqs ON s.id_sequence = sqs.id_sequence AND sqs.date_fin IS NULL
            LEFT JOIN statuts_sequence ss ON sqs.id_statut = ss.id_statut_sequence
            """);
        
        Object[] params = {};
        if (projetId != null) {
            sql.append(" WHERE e.id_projet = ?");
            params = new Object[]{projetId};
        }
        
        Map<String, Object> result = jdbcTemplate.queryForMap(sql.toString(), params);
        return createStatisticItem(result);
    }
    
    private GlobalStatisticsDTO.StatisticItem getSceneStatistics(Long projetId) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                COUNT(DISTINCT sc.id_scene) as total,
                COUNT(DISTINCT CASE WHEN sts.code = 'validee' THEN sc.id_scene END) as termines,
                COUNT(DISTINCT CASE WHEN sts.code IN ('tournage', 'planifiee') THEN sc.id_scene END) as en_cours,
                COUNT(DISTINCT CASE WHEN sts.code = 'ecrite' THEN sc.id_scene END) as a_faire
            FROM scenes sc
            JOIN sequences s ON sc.id_sequence = s.id_sequence
            JOIN episodes e ON s.id_episode = e.id_episode
            LEFT JOIN scene_statuts scs ON sc.id_scene = scs.id_scene AND scs.date_fin IS NULL
            LEFT JOIN statuts_scene sts ON scs.id_statut = sts.id_statut_scene
            """);
        
        Object[] params = {};
        if (projetId != null) {
            sql.append(" WHERE e.id_projet = ?");
            params = new Object[]{projetId};
        }
        
        Map<String, Object> result = jdbcTemplate.queryForMap(sql.toString(), params);
        return createStatisticItem(result);
    }
    
    private GlobalStatisticsDTO.StatisticItem createStatisticItem(Map<String, Object> result) {
        GlobalStatisticsDTO.StatisticItem item = new GlobalStatisticsDTO.StatisticItem();
        item.setTotal(((Number) result.get("total")).longValue());
        
        Map<String, Long> counts = new HashMap<>();
        Map<String, Double> percentages = new HashMap<>();
        
        Long total = item.getTotal();
        
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            if (!entry.getKey().equals("total")) {
                Long count = ((Number) entry.getValue()).longValue();
                counts.put(entry.getKey().toUpperCase(), count);
                
                if (total > 0) {
                    double percentage = (count * 100.0) / total;
                    percentages.put(entry.getKey().toUpperCase(), Math.round(percentage * 10.0) / 10.0);
                } else {
                    percentages.put(entry.getKey().toUpperCase(), 0.0);
                }
            }
        }
        
        item.setCounts(counts);
        item.setPercentages(percentages);
        return item;
    }
    
    private List<GlobalStatisticsDTO.ProjectRanking> getTopProjects() {
        String sql = """
            SELECT 
                p.id_projet as id,
                p.titre as nom,
                COUNT(DISTINCT e.id_episode) as episodes,
                COUNT(DISTINCT sc.id_scene) as scenes,
                CASE 
                    WHEN COUNT(DISTINCT sc.id_scene) > 0 THEN 
                        (COUNT(DISTINCT CASE 
                            WHEN sts.code = 'validee' 
                            THEN sc.id_scene END) * 100.0 / COUNT(DISTINCT sc.id_scene))
                    ELSE 0 
                END as progression
            FROM projets p
            LEFT JOIN episodes e ON p.id_projet = e.id_projet
            LEFT JOIN sequences s ON e.id_episode = s.id_episode
            LEFT JOIN scenes sc ON s.id_sequence = sc.id_sequence
            LEFT JOIN scene_statuts scs ON sc.id_scene = scs.id_scene AND scs.date_fin IS NULL
            LEFT JOIN statuts_scene sts ON scs.id_statut = sts.id_statut_scene
            GROUP BY p.id_projet, p.titre
            ORDER BY progression DESC, scenes DESC
            LIMIT 5
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GlobalStatisticsDTO.ProjectRanking ranking = new GlobalStatisticsDTO.ProjectRanking();
            ranking.setId(rs.getLong("id"));
            ranking.setNom(rs.getString("nom"));
            ranking.setEpisodes(rs.getLong("episodes"));
            ranking.setScenes(rs.getLong("scenes"));
            ranking.setProgression(Math.round(rs.getDouble("progression") * 10.0) / 10.0);
            return ranking;
        });
    }
    
    private List<GlobalStatisticsDTO.TeamDistribution> getTeamDistribution() {
        String sql = """
            SELECT 
                role,
                COUNT(*) as count,
                (COUNT(*) * 100.0 / (SELECT COUNT(*) FROM utilisateurs WHERE role IN ('REALISATEUR', 'SCENARISTE'))) as percentage
            FROM utilisateurs 
            WHERE role IN ('REALISATEUR', 'SCENARISTE')
            GROUP BY role
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GlobalStatisticsDTO.TeamDistribution dist = new GlobalStatisticsDTO.TeamDistribution();
            dist.setNom(rs.getString("role"));
            dist.setCount(rs.getLong("count"));
            dist.setPercentage(Math.round(rs.getDouble("percentage") * 10.0) / 10.0);
            return dist;
        });
    }
    
    private List<GlobalStatisticsDTO.ProjectDetail> getProjectsDetail() {
        String sql = """
            SELECT 
                p.id_projet as id,
                p.titre as nom,
                p.synopsis as description,
                COUNT(DISTINCT e.id_episode) as episodes,
                COUNT(DISTINCT CASE 
                    WHEN se.code = 'valide' 
                    THEN e.id_episode END) as episodes_termines,
                COUNT(DISTINCT s.id_sequence) as sequences,
                COUNT(DISTINCT sc.id_scene) as scenes,
                COUNT(DISTINCT CASE 
                    WHEN sts.code = 'validee' 
                    THEN sc.id_scene END) as scenes_terminees,
                COUNT(DISTINCT d.id_dialogue) as dialogues,
                CASE 
                    WHEN COUNT(DISTINCT sc.id_scene) > 0 THEN 
                        (COUNT(DISTINCT CASE 
                            WHEN sts.code = 'validee' 
                            THEN sc.id_scene END) * 100.0 / COUNT(DISTINCT sc.id_scene))
                    ELSE 0 
                END as progression,
                MAX(e.modifie_le) as last_update
            FROM projets p
            LEFT JOIN episodes e ON p.id_projet = e.id_projet
            LEFT JOIN episode_statuts es ON e.id_episode = es.id_episode AND es.date_fin IS NULL
            LEFT JOIN statuts_episode se ON es.id_statut = se.id_statut_episode
            LEFT JOIN sequences s ON e.id_episode = s.id_episode
            LEFT JOIN scenes sc ON s.id_sequence = sc.id_sequence
            LEFT JOIN scene_statuts scs ON sc.id_scene = scs.id_scene AND scs.date_fin IS NULL
            LEFT JOIN statuts_scene sts ON scs.id_statut = sts.id_statut_scene
            LEFT JOIN dialogues d ON sc.id_scene = d.id_scene
            GROUP BY p.id_projet, p.titre, p.synopsis
            ORDER BY p.titre
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GlobalStatisticsDTO.ProjectDetail project = new GlobalStatisticsDTO.ProjectDetail();
            project.setId(rs.getLong("id"));
            project.setNom(rs.getString("nom"));
            project.setDescription(rs.getString("description"));
            project.setEpisodes(rs.getLong("episodes"));
            project.setEpisodesTermines(rs.getLong("episodes_termines"));
            project.setSequences(rs.getLong("sequences"));
            project.setScenes(rs.getLong("scenes"));
            project.setScenesTerminees(rs.getLong("scenes_terminees"));
            project.setDialogues(rs.getLong("dialogues"));
            project.setProgression(Math.round(rs.getDouble("progression") * 10.0) / 10.0);
            project.setLastUpdate(rs.getTimestamp("last_update") != null ? 
                rs.getTimestamp("last_update").toLocalDateTime().toString() : "N/A");
            
            // Récupérer l'équipe du projet
            project.setEquipe(getProjectTeam(project.getId()));
            
            return project;
        });
    }
    
    private List<GlobalStatisticsDTO.TeamMember> getProjectTeam(Long projetId) {
        String sql = """
            SELECT DISTINCT u.id_utilisateur as id, u.nom as nom
            FROM utilisateurs u
            LEFT JOIN realisateurs r ON u.id_utilisateur = r.id_utilisateur
            LEFT JOIN episode_realisateurs er ON r.id_realisateur = er.id_realisateur
            LEFT JOIN episodes e ON er.id_episode = e.id_episode
            LEFT JOIN scenaristes s ON u.id_utilisateur = s.id_utilisateur
            LEFT JOIN episode_scenaristes es ON s.id_scenariste = es.id_scenariste
            LEFT JOIN episodes e2 ON es.id_episode = e2.id_episode
            WHERE (e.id_projet = ? OR e2.id_projet = ?) AND u.role IN ('REALISATEUR', 'SCENARISTE')
            LIMIT 3
            """;
        
        return jdbcTemplate.query(sql, new Object[]{projetId, projetId}, (rs, rowNum) -> {
            GlobalStatisticsDTO.TeamMember member = new GlobalStatisticsDTO.TeamMember();
            member.setId(rs.getLong("id"));
            member.setNom(rs.getString("nom"));
            return member;
        });
    }
    
    private List<GlobalStatisticsDTO.MonthlyProgress> getMonthlyProgress(String periode) {
        String sql = """
            SELECT 
                TO_CHAR(e.cree_le, 'YYYY-MM') as mois,
                COUNT(DISTINCT e.id_episode) as episodes,
                COUNT(DISTINCT s.id_sequence) as sequences,
                COUNT(DISTINCT sc.id_scene) as scenes
            FROM episodes e
            LEFT JOIN sequences s ON e.id_episode = s.id_episode
            LEFT JOIN scenes sc ON s.id_sequence = sc.id_sequence
            WHERE e.cree_le >= CURRENT_DATE - INTERVAL '6 months'
            GROUP BY TO_CHAR(e.cree_le, 'YYYY-MM')
            ORDER BY mois
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GlobalStatisticsDTO.MonthlyProgress progress = new GlobalStatisticsDTO.MonthlyProgress();
            progress.setMois(rs.getString("mois"));
            progress.setEpisodes(rs.getLong("episodes"));
            progress.setSequences(rs.getLong("sequences"));
            progress.setScenes(rs.getLong("scenes"));
            return progress;
        });
    }

    // Ajouter ces méthodes dans StatisticsService.java

private Map<String, Object> getProjectTotals(Long projetId) {
    String sql = """
        SELECT 
            p.titre as projet_nom,
            COUNT(DISTINCT e.id_episode) as total_episodes,
            COUNT(DISTINCT s.id_sequence) as total_sequences,
            COUNT(DISTINCT sc.id_scene) as total_scenes,
            COUNT(DISTINCT d.id_dialogue) as total_dialogues,
            COUNT(DISTINCT CASE WHEN u.role = 'REALISATEUR' THEN u.id_utilisateur END) as total_realisateurs,
            COUNT(DISTINCT CASE WHEN u.role = 'SCENARISTE' THEN u.id_utilisateur END) as total_scenaristes
        FROM projets p
        LEFT JOIN episodes e ON p.id_projet = e.id_projet
        LEFT JOIN sequences s ON e.id_episode = s.id_episode
        LEFT JOIN scenes sc ON s.id_sequence = sc.id_sequence
        LEFT JOIN dialogues d ON sc.id_scene = d.id_scene
        LEFT JOIN episode_realisateurs er ON e.id_episode = er.id_episode
        LEFT JOIN realisateurs r ON er.id_realisateur = r.id_realisateur
        LEFT JOIN utilisateurs u_r ON r.id_utilisateur = u_r.id_utilisateur
        LEFT JOIN episode_scenaristes es ON e.id_episode = es.id_episode
        LEFT JOIN scenaristes scena ON es.id_scenariste = scena.id_scenariste
        LEFT JOIN utilisateurs u_s ON scena.id_utilisateur = u_s.id_utilisateur
        WHERE p.id_projet = ? OR ? IS NULL
        GROUP BY p.id_projet, p.titre
        """;
    
    return jdbcTemplate.queryForMap(sql, projetId, projetId);
}

// Nouvelle méthode pour les statuts détaillés par projet
private Map<String, Object> getDetailedStatusStatistics(Long projetId) {
    String sql = """
        -- Statistiques détaillées des statuts
        SELECT 
            -- Épisodes par statut
            (SELECT COUNT(*) FROM episodes e 
             JOIN episode_statuts es ON e.id_episode = es.id_episode AND es.date_fin IS NULL
             JOIN statuts_episode se ON es.id_statut = se.id_statut_episode
             WHERE (e.id_projet = ? OR ? IS NULL) AND se.code = 'valide') as episodes_valides,
             
            (SELECT COUNT(*) FROM episodes e 
             JOIN episode_statuts es ON e.id_episode = es.id_episode AND es.date_fin IS NULL
             JOIN statuts_episode se ON es.id_statut = se.id_statut_episode
             WHERE (e.id_projet = ? OR ? IS NULL) AND se.code = 'tournage') as episodes_tournage,
             
            -- Scènes par statut
            (SELECT COUNT(*) FROM scenes sc
             JOIN sequences seq ON sc.id_sequence = seq.id_sequence
             JOIN episodes e ON seq.id_episode = e.id_episode
             JOIN scene_statuts ss ON sc.id_scene = ss.id_scene AND ss.date_fin IS NULL
             JOIN statuts_scene sts ON ss.id_statut = sts.id_statut_scene
             WHERE (e.id_projet = ? OR ? IS NULL) AND sts.code = 'validee') as scenes_validees,
             
            (SELECT COUNT(*) FROM scenes sc
             JOIN sequences seq ON sc.id_sequence = seq.id_sequence
             JOIN episodes e ON seq.id_episode = e.id_episode
             JOIN scene_statuts ss ON sc.id_scene = ss.id_scene AND ss.date_fin IS NULL
             JOIN statuts_scene sts ON ss.id_statut = sts.id_statut_scene
             WHERE (e.id_projet = ? OR ? IS NULL) AND sts.code = 'tournage') as scenes_tournage
        """;
    
    return jdbcTemplate.queryForMap(sql, 
        projetId, projetId, projetId, projetId, projetId, projetId, projetId, projetId);
}

} 
