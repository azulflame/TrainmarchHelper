package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RandomSelector {
    private List<String> choices;

    public void load(String filename) {
        try {
            choices = new ArrayList<String>();
            File f = new File(filename);
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                choices.add(s.nextLine());
            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get() {
        Random r = new Random();
        int rand = r.nextInt(choices.size());
        return choices.get(rand);
    }
}
