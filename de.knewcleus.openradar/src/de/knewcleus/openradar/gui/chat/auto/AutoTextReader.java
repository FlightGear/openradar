package de.knewcleus.openradar.gui.chat.auto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class AutoTextReader {

    public static void loadTexts(List<String> languages, List<AtcMessage> messages) {

        BufferedReader br = null;

        try {
            // load commands.short
            File fileCmdShort = new File("settings/ATCmsg/cmd-short.txt");
            if (!fileCmdShort.exists()) {
                return;
            }
            br = new BufferedReader(new FileReader(fileCmdShort));
            String nextLine = br.readLine();
            while (nextLine != null) {
                AtcMessage msg = new AtcMessage(nextLine);
                messages.add(msg);
                nextLine = br.readLine();
            }
            br.close();

            // load commands.properties
            File fileCmdProps = new File("settings/ATCmsg/cmd-props.txt");
            br = new BufferedReader(new FileReader(fileCmdProps));
            nextLine = br.readLine();
            for (int i=0; i<messages.size()&&nextLine!=null;i++) {
                messages.get(i).setVariables(nextLine);
                nextLine = br.readLine();
            }
            br.close();

            // load Languages.txt
            languages.add("en");
            
            File fileLanguages = new File("settings/ATCmsg/Languages.txt");
            br = new BufferedReader(new FileReader(fileLanguages));
            nextLine = br.readLine();
            while (nextLine != null) {
                if (nextLine.trim().length() > 2) {
                    String code = nextLine.substring(0, 2);
                    if (!code.equals("--")) {
                        languages.add(code);
                    }
                }
                nextLine = br.readLine();
            }
            br.close();

            // load translations

            for(String code : languages) {
                File fileTranslation = new File("settings/ATCmsg/"+code+".txt");
                br = new BufferedReader(new FileReader(fileTranslation));
                nextLine = br.readLine();
                for (int i=0; i<messages.size()&&nextLine!=null;i++) {
                    messages.get(i).addTranslation(code, nextLine);
                    nextLine = br.readLine();
                }
            }
            // br.close(); below in finally

        } catch (Exception e) {
            throw new IllegalArgumentException("Problems to read translations!",e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
