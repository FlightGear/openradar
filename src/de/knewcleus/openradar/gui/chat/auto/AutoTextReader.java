/**
 * Copyright (C) 2012 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.chat.auto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AutoTextReader {

    public static void loadTexts(List<String> languages, List<AtcMenuChatMessage> menuMessages, Map<String,String> chatMessages) {

        BufferedReader br = null;

        try {
            // load commands.short
            File fileCmdShort = new File("data/atcMessages/cmd-short.txt");
            if (!fileCmdShort.exists()) {
                return;
            }
            br = new BufferedReader(new FileReader(fileCmdShort));
            String nextLine = br.readLine();
            while (nextLine != null) {
                AtcMenuChatMessage msg = new AtcMenuChatMessage(nextLine);
                menuMessages.add(msg);
                nextLine = br.readLine();
            }
            br.close();

            // load commands.properties
            File fileCmdProps = new File("data/atcMessages/cmd-props.txt");
            br = new BufferedReader(new FileReader(fileCmdProps));
            nextLine = br.readLine();
            for (int i=0; i<menuMessages.size()&&nextLine!=null;i++) {
                menuMessages.get(i).setVariables(nextLine);
                nextLine = br.readLine();
            }
            br.close();

            // load Languages.txt
            languages.add("en");

            File fileLanguages = new File("data/atcMessages/Languages.txt");
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
                File fileTranslation = new File("data/atcMessages/"+code+".txt");
                br = new BufferedReader(new FileReader(fileTranslation));
                nextLine = br.readLine();
                for (int i=0; i<menuMessages.size()&&nextLine!=null;i++) {
                    menuMessages.get(i).addTranslation(code, nextLine);
                    nextLine = br.readLine();
                }
            }

            // load alias.txt (chatMessages)
            File fileAlias = new File("data/atcMessages/alias.txt");
            br = new BufferedReader(new FileReader(fileAlias));
            nextLine = br.readLine();
            while (nextLine!=null) {
                int pos = nextLine.indexOf("=");
                if(nextLine.trim().length()>0 && !nextLine.trim().startsWith("#") && pos>-1 && pos+1<nextLine.length()) {
                    chatMessages.put(nextLine.substring(0,pos).trim().toLowerCase(),nextLine.substring(pos+1));
                }
                nextLine = br.readLine();
            }
            br.close();


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
