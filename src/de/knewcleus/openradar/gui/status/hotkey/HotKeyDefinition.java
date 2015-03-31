package de.knewcleus.openradar.gui.status.hotkey;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class HotKeyDefinition {

    public final String feature;
    public final char keyChar;
    public final String keyText;
    public final int keyCode;
    public final int keyLocation;
    public final int modifiers;
    public final String keyModifiersText;
    
    private final List<IHotKeyListener> listeners = new ArrayList<IHotKeyListener>();
    
    public HotKeyDefinition(String feature, KeyEvent e) {
        this.feature=feature; 
        this.keyChar=e.getKeyChar();
        this.keyCode=e.getKeyCode();
        this.keyText=KeyEvent.getKeyText(keyCode);
        this.keyLocation=e.getKeyLocation();
        this.modifiers = e.getModifiers();
        this.keyModifiersText=KeyEvent.getKeyModifiersText(modifiers);
    }

    public boolean equals(KeyEvent e) {
        return keyChar==e.getKeyChar() &&
               keyCode==e.getKeyCode() &&
               keyLocation==e.getKeyLocation() &&
               modifiers == e.getModifiers();
    }
    
    public void addListener(IHotKeyListener hkl) {
        listeners.add(hkl);
    }

    public void removeListener(IHotKeyListener hkl) {
        listeners.remove(hkl);
    }
    
    public void notifyListeners() {
        for(IHotKeyListener l : listeners) {
            l.hotKeyInvoked(this);
        }
    }
}
