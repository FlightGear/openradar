/**
 * Copyright (C) 2014-2016 Wolfram Wagner 
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
package de.knewcleus.openradar.gui;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.setup.AirportData;


public class SoundManager {

    public enum Sound { MUTE, CHAT, CONTACT, METAR }

    private static boolean mute = false;
    private static boolean initiated = false;

    private static Clip popClip;
    private static Clip aircraftClip;
    private static Clip weatherClip;

    private static Set<Sound> mutedSounds = Collections.synchronizedSet(new HashSet<Sound>());

    private final static Logger log = Logger.getLogger(SoundManager.class);
    
    public static void init(AirportData data) {
        try {
            popClip = AudioSystem.getClip();
            popClip.open( AudioSystem.getAudioInputStream(new File("res/sounds/printer.wav")));
            aircraftClip = AudioSystem.getClip();
            aircraftClip.open(AudioSystem.getAudioInputStream(new File("res/sounds/aircraft.wav")));
            weatherClip = AudioSystem.getClip();
            weatherClip.open(AudioSystem.getAudioInputStream(new File("res/sounds/wind.wav")));

            mute = data.getToggleState(Sound.MUTE.toString(), false);

            if(data.getToggleState(Sound.CHAT.toString(), false)) {
                mutedSounds.add(Sound.CHAT);
            }
            if(data.getToggleState(Sound.CONTACT.toString(), false)) {
                mutedSounds.add(Sound.CONTACT);
            }
            if(data.getToggleState(Sound.METAR.toString(), false)) {
                mutedSounds.add(Sound.METAR);
            }
            log.info("SoundManager initialized!");
            initiated=true;
        } catch (Exception e) {
        	log.info("Problem to init SoundManager!",e);
        }
    }

    public static synchronized boolean isMute() {
        return mute;
    }

    public static synchronized void setMute(boolean mute) {
        SoundManager.mute = mute;
    }

    public static synchronized void toggleMute() {
        mute=!mute;
    }

    public static synchronized void toggle(Sound s) {
        if(mutedSounds.contains(s)) {
            mutedSounds.remove(s);
        } else {
            mutedSounds.add(s);
        }

    }

    public static synchronized void playChatSound() {
        if(!initiated || mute || mutedSounds.contains(Sound.CHAT)) return;

        popClip.setFramePosition(0);
        popClip.start();
    }

    public static synchronized void playContactSound() {
        if(!initiated || mute || mutedSounds.contains(Sound.CONTACT)) return;

        aircraftClip.setFramePosition(0);
        aircraftClip.start();
    }

    public static synchronized void playWeather() {
        if(!initiated || mute  || mutedSounds.contains(Sound.METAR)) return;

        weatherClip.setFramePosition(0);
        weatherClip.start();
    }

//    public static void playSound(String filename) {
//        AudioStream as = null;
//        try {
//            as = new AudioStream(new FileInputStream(filename));
//            AudioPlayer.player.start(as);
//            as.close();
//        } catch (Exception e1) {
//            Logger.getLogger(SoundManager.class).warning("Could not play '"+filename+"' "+e1.getMessage());
//        } finally {
//            if(as!=null) {
//                try {
//                    as.close();
//                } catch (Exception e) {}
//            }
//        }
//    }
//    public synchronized static void playSound2(String filename) {
//        AudioInputStream as = null;
//        try {
//            as = AudioSystem.getAudioInputStream(new File(filename));
//            AudioSystem.getClip().open(as);
//        } catch (Exception e1) {
//            Logger.getLogger(SoundManager.class).warning("Could not play '"+filename+"' "+e1.getMessage());
//        } finally {
//            if(as!=null) {
//                try {
//                    AudioSystem.getClip().close();
//                    as.close();
//                } catch (Exception e) {}
//            }
//        }
//    }

    public synchronized static void close() {
        try {
            aircraftClip.close();
            popClip.close();
            weatherClip.close();
        } catch (Exception e) {}
    }
}
