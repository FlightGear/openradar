/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.radardata;

import java.util.HashSet;
import java.util.Set;
/**
 * This class is a the parent of classes that receive the radar data from somewhere.
 * 
 * The implementation here forwards the data to its registered listeners, the recipients.
 * 
 * @author Ralf Gehrlich
 *
 */
public abstract class RadarDataProvider implements IRadarDataProvider {
	protected final Set<IRadarDataRecipient> recipients = new HashSet<IRadarDataRecipient>();

	@Override
	public void registerRecipient(IRadarDataRecipient recipient) {
		assert(!recipients.contains(recipient));
		recipients.add(recipient);
	}

	@Override
	public void unregisterRecipient(IRadarDataRecipient recipient) {
		assert(recipients.contains(recipient));
		recipients.remove(recipient);
	}
	
	protected void publishRadarDataPacket(IRadarDataPacket data) {
		for (IRadarDataRecipient recipient: recipients) {
			recipient.acceptRadarData(this, data);
		}
	}

}
