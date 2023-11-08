package me.azulflame.trainmarch.dmhelper.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class PlayerCharacter {
    private String ownerId;
    private String characterName;
    private String sheetLink;

    public String toString() {
        return "<@" + ownerId + ">: " + characterName + " " + sheetLink;
    }
}
