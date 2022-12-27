package me.azulflame.trainmarch.dmhelper.dto;

import javax.persistence.*;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player_characters")
public @Data class PlayerCharacter {
    private static final String key = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private int id;
    private int strength;
    private int dexterity;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;
    private int strengthSave;
    private int dexteritySave;
    private int constitutionSave;
    private int intelligenceSave;
    private int wisdomSave;
    private int charismaSave;
    private int artificer;
    private int barbarian;

    private int bard;
    private int cleric;
    private int druid;
    private int fighter;
    private int monk;
    private int paladin;
    private int ranger;
    private int rogue;
    private int sorcerer;
    private int warlock;
    private int wizard;
    private int bloodHunter;
    private String name;
    private String userId;
    private String link;
    private int platinum;
    private int gold;
    private int silver;
    private int copper;
    private int dt;
    private int stamps;

    private static final int[] profLookup = {0, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6};

    public int getProficiencyBonus() {
        return profLookup[artificer + barbarian + bard + cleric + druid + fighter + monk + paladin + ranger + rogue + sorcerer + warlock + wizard + bloodHunter];
    }

    public String toString() {
        return name + ":\nSTR: " + strength + " (save: " + strengthSave + ")\n" +
                "DEX: " + dexterity + " (save: " + dexteritySave + ")\n" +
                "CON: " + constitution + " (save: " + constitutionSave + ")\n" +
                "INT: " + intelligence + " (save: " + intelligenceSave + ")\n" +
                "WIS: " + wisdom + " (save: " + wisdomSave + ")\n" +
                "CHA: " + charisma + " (save: " + charismaSave + ")\n\n" +
                getLevels() + "\n" +
                platinum + "pp " + gold + "gp " + silver + "sp " + copper + "cp\n" +
                "\n<" + link + ">";
    }

    private String getLevels() {
        List<String> out = new ArrayList<>();
        int[] levels = {artificer, bard, barbarian, cleric, druid, fighter, monk, paladin, ranger, rogue, sorcerer, warlock, wizard, bloodHunter};
        String[] names = {"Artificer", "Bard", "Barbarian", "Cleric", "Druid", "Fighter", "Monk", "Paladin", "Ranger", "Rogue", "Sorcerer", "Warlock", "Wizard", "Blood Hunter"};

        for (int i = 0; i < levels.length; i++) {
            if (levels[i] > 0) {
                out.add(levels[i] + " " + names[i]);
            }
        }
        return String.join(", ", out);
    }

    public static PlayerCharacter from(String compressed, String userId) {
        PlayerCharacter pc = new PlayerCharacter();
        String[] split = compressed.split("\\|");
        String vals = split[0].trim();
        String link = split[1].trim();
        String[] nameArr = new String[split.length - 2];
        for (int i = 0; i < split.length - 2; i++) {
            nameArr[i] = split[i + 2];
        }
        String name = String.join("|", nameArr).trim();
        pc.setName(name);
        pc.setLink(getLink(link));
        pc.setUserId(userId);

        pc.setStrength(key.indexOf(vals.charAt(0)) - 6);
        pc.setDexterity(key.indexOf(vals.charAt(1)) - 6);
        pc.setConstitution(key.indexOf(vals.charAt(2)) - 6);
        pc.setIntelligence(key.indexOf(vals.charAt(3)) - 6);
        pc.setWisdom(key.indexOf(vals.charAt(4)) - 6);
        pc.setCharisma(key.indexOf(vals.charAt(5)) - 6);

        pc.setStrengthSave(key.indexOf(vals.charAt(6)) - 6);
        pc.setDexteritySave(key.indexOf(vals.charAt(7)) - 6);
        pc.setConstitutionSave(key.indexOf(vals.charAt(8)) - 6);
        pc.setIntelligenceSave(key.indexOf(vals.charAt(9)) - 6);
        pc.setWisdomSave(key.indexOf(vals.charAt(10)) - 6);
        pc.setCharismaSave(key.indexOf(vals.charAt(11)) - 6);

        pc.setArtificer(key.indexOf(vals.charAt(12)) - 6);
        pc.setBarbarian(key.indexOf(vals.charAt(13)) - 6);
        pc.setBard(key.indexOf(vals.charAt(14)) - 6);
        pc.setCleric(key.indexOf(vals.charAt(15)) - 6);
        pc.setDruid(key.indexOf(vals.charAt(16)) - 6);
        pc.setFighter(key.indexOf(vals.charAt(17)) - 6);
        pc.setMonk(key.indexOf(vals.charAt(18)) - 6);
        pc.setPaladin(key.indexOf(vals.charAt(19)) - 6);
        pc.setRanger(key.indexOf(vals.charAt(20)) - 6);
        pc.setRogue(key.indexOf(vals.charAt(21)) - 6);
        pc.setSorcerer(key.indexOf(vals.charAt(22)) - 6);
        pc.setWarlock(key.indexOf(vals.charAt(23)) - 6);
        pc.setWizard(key.indexOf(vals.charAt(24)) - 6);
        pc.setBloodHunter(key.indexOf(vals.charAt(25)) - 6);

        return pc;
    }

    private static String getLink(String str) {
        if (str.startsWith("beyond-")) {
            return "https://www.dndbeyond.com/characters/" + str.substring(7);
        } else if (str.startsWith("google-")) {
            return "https://docs.google.com/spreadsheets/d/" + str.substring(7);
        } else if (str.startsWith("dicecloud-")) {
            return "https://dicecloud.com/character/" + str.substring(10);
        }
        return "Error parsing link from " + str;
    }

    public int getLevel() {
        return artificer+bard+barbarian+cleric+druid+fighter+monk+paladin+ranger+rogue+sorcerer+warlock+wizard+bloodHunter;
    }
}
