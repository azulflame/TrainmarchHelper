package me.azulflame.trainmarch.dmhelper.service;

import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;

public class Dmxp {

    public static String change(String userId, String name, int change, String reason)
    {
        String message = "";
        boolean isGain = change > 0;
        Integer oldDmxp = DatabaseManager.getDmxp(userId);
        boolean isNew = oldDmxp == null;
        if (isNew)
            oldDmxp = 0;
        if (oldDmxp + change >= 0)
        {
            DatabaseManager.setDmxp(userId, oldDmxp + change);
            message = xpString(oldDmxp, change, name) + "\nReason given: " + reason;
        }
        else
        {
            message = "Unable to spend **" + Math.abs(change) + "** dmxp from a total of **" + oldDmxp + "** dmxp.";
        }
        return message;
    }
    public static String give(String userId, String userName, String targetId, String targetName, int change, String reason)
    {
        String message = "";
        boolean isGain = change > 0;
        Integer oldDmxp = DatabaseManager.getDmxp(userId);
        boolean isNew = oldDmxp == null;
        if (userId.equals(targetId))
        {
            return "Cannot give yourself DMXP. Use `/dmxp update` to update your own total.";
        }
        if (!isGain)
        {
            return "Cannot give negative DMXP to someone else.";
        }
        boolean targetExisted = true;
        Integer old = DatabaseManager.getDmxp(targetId);
        if (old == null)
        {
            old = 0;
            targetExisted = false;
        }
        if (oldDmxp - change >= 0)
        {
            DatabaseManager.setDmxp(userId, oldDmxp - change);
            DatabaseManager.setDmxp(targetId, old + change);
            String giver = xpString(oldDmxp, -change, userName);
            String getter = xpString(old, change, targetName);
            message = "**" + userName + "** has given " + change + " DMXP to **" + targetName + "**.\n\n" + giver + "\n\n" + getter + "\n\nReason: " + reason;
        }
        else
        {
            message = "Unable to give **" + Math.abs(change) + "** dmxp from a total of **" + oldDmxp + "** dmxp.";
        }
        if (!targetExisted)
        {
            message += "\nSince the recipient did not have their DMXP tracked, they are now being tracked, starting at 0.";
        }
        return message;
    }

    public static String check(String userId, String name)
    {
        Integer dmxp = DatabaseManager.getDmxp(userId);
        if (dmxp != null)
        {
            return " has " + dmxp + " dmxp.";
        }
        return " does not have their dmxp tracked by the bot.";
    }

    private static String xpString(int original, int change, String name)
    {
        return "DMXP for **"+name+":**\n"+
                "Original: " + original + "\n"+
                "Change: " + change + "\n"+
                "New: " + (original+change);
    }
}
