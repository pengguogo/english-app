package com.englishapp.service.ai.safety;

import java.util.List;
import java.util.Map;

/**
 * 死亡与离别（6.1）：自然知识（恐龙灭绝、落叶、小狗会死吗）→ DEATH_NATURE；
 * 家人 / 自家宠物去世、或分不清对象（人死了会去哪）→ DEATH_FAMILY（从严）。
 */
final class DeathRule {

    private final List<String> idioms;
    private final List<String> words;
    private final List<String> natureOnlyWords;
    private final List<String> natureSubjects;
    private final List<String> familySubjects;
    private final List<String> familyGonePhrases;
    private final List<String> possessives;
    private final List<String> pets;
    private final List<String> petGonePhrases;
    private final List<String> englishFamily;
    private final List<String> englishDeath;

    DeathRule(Map<String, Object> death, List<String> idioms) {
        this.idioms = idioms;
        this.words = YamlResource.list(death, "words");
        this.natureOnlyWords = YamlResource.list(death, "natureOnlyWords");
        this.natureSubjects = YamlResource.list(death, "natureSubjects");
        this.familySubjects = YamlResource.list(death, "familySubjects");
        this.familyGonePhrases = YamlResource.list(death, "familyGonePhrases");
        this.possessives = YamlResource.list(death, "possessives");
        this.pets = YamlResource.list(death, "pets");
        this.petGonePhrases = YamlResource.list(death, "petGonePhrases");
        this.englishFamily = YamlResource.list(death, "englishFamily");
        this.englishDeath = YamlResource.list(death, "englishDeath");
    }

    /** 返回 NONE / DEATH_NATURE / DEATH_FAMILY */
    SafetyCategory classify(String normalized) {
        String s = normalized;
        for (String idiom : idioms) {
            s = s.replace(idiom, "");
        }
        boolean family = KeywordRule.containsAny(s, familySubjects);
        if (KeywordRule.containsAny(s, englishFamily) && KeywordRule.containsAny(s, englishDeath)) {
            return SafetyCategory.DEATH_FAMILY;
        }
        if (family && KeywordRule.containsAny(s, familyGonePhrases)) {
            return SafetyCategory.DEATH_FAMILY;
        }
        boolean pet = KeywordRule.containsAny(s, pets);
        if (pet && KeywordRule.containsAny(s, petGonePhrases)) {
            return SafetyCategory.DEATH_FAMILY;
        }
        List<String> hit = words.stream().filter(s::contains).toList();
        if (hit.isEmpty()) {
            return SafetyCategory.NONE;
        }
        boolean ownPet = pet && KeywordRule.containsAny(s, possessives);
        if (family || ownPet) {
            return SafetyCategory.DEATH_FAMILY;
        }
        if (natureOnlyWords.containsAll(hit) || KeywordRule.containsAny(s, natureSubjects)) {
            return SafetyCategory.DEATH_NATURE;
        }
        return SafetyCategory.DEATH_FAMILY;
    }
}
