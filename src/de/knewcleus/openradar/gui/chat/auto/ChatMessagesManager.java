package de.knewcleus.openradar.gui.chat.auto;

import java.util.ArrayList;

// TODO: merge with similar class AtcMenuChatMessage
public class ChatMessagesManager {

	private final ArrayList<AtcMenuChatMessage> messages = new ArrayList<AtcMenuChatMessage>();

	public ChatMessagesManager() {
		loadMessages();
	}

	protected void loadMessages() {
		AtcMenuChatMessage msg;
		// TODO: messages in external file
		// squawk
        msg = new AtcMenuChatMessage("squawk");
        msg.addTranslation("en", "%s: Squawk %s");
        msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target");
        messages.add(msg);
		
		// altitude
        msg = new AtcMenuChatMessage("climb");
        msg.addTranslation("en", "%s: Climb and maintain %s");
        msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target"); 
        messages.add(msg);
        msg = new AtcMenuChatMessage("descend");
        msg.addTranslation("en", "%s: Descend and maintain %s");
        msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target"); 
        messages.add(msg);
        
        // heading
        msg = new AtcMenuChatMessage("left");
        msg.addTranslation("en", "%s: Turn left heading %s");
        msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target"); 
        messages.add(msg);
        msg = new AtcMenuChatMessage("right");
        msg.addTranslation("en", "%s: Turn right heading %s");
        msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target"); 
        messages.add(msg);
        
        // speed
        msg = new AtcMenuChatMessage("reduce");
        msg.addTranslation("en", "%s: Reduce speed to %s");
        msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target"); 
        messages.add(msg);
        msg = new AtcMenuChatMessage("increase");
        msg.addTranslation("en", "%s: Increase speed to %s");
        msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target"); 
        messages.add(msg);
        
	}
	
	public AtcMenuChatMessage getMessage(String msg) {
		for (AtcMenuChatMessage message : messages) {
			if (msg.equals(message.getDisplayMessage())) {
				return message;
			}
		}
		return null;
	}
}
