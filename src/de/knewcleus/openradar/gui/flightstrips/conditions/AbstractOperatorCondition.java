package de.knewcleus.openradar.gui.flightstrips.conditions;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.config.RulesManager;

public abstract class AbstractOperatorCondition extends AbstractCondition implements ListModel<AbstractCondition> {

	ArrayList<AbstractCondition> conditions = null;
	
	// --- constructors ---
	
	public AbstractOperatorCondition() {
		conditions = new ArrayList<AbstractCondition>();
	}

	public AbstractOperatorCondition(ArrayList<AbstractCondition> conditions) {
		this.conditions = conditions;
	}

	public AbstractOperatorCondition(AbstractCondition... conditions) {
		this.conditions = new ArrayList<AbstractCondition>();
		for (AbstractCondition condition : conditions) this.conditions.add(condition);
	}

	public AbstractOperatorCondition(Element element) throws Exception {
		this.conditions = new ArrayList<AbstractCondition>();
		if (element != null) {
			for (Element e : element.getChildren()) {
				AbstractCondition condition = RulesManager.createConditionClass(e);
				if (condition != null) add(condition);
			}
		}
	}
	
	// --- manage ---
	
	public void add (AbstractCondition condition) {
		conditions.add(condition);
	}
	
	public void remove (AbstractCondition condition) {
		conditions.remove(condition);
	}
	
	public ArrayList<AbstractCondition> getConditions() {
		return conditions;
	}
	
	// --- IRuleTextProvider ---
	
	@Override
	public ArrayList<String> getText () {
		ArrayList<String> result = new ArrayList<String>();
		String indent = "|  ";
		String operator = "";
		for (AbstractCondition condition : conditions) {
			if (!operator.isEmpty()) result.add(operator);
			for (String text : condition.getText()) result.add(indent + text);
			operator = indent + getSimpleText();
		}
		return result;
	}
	
	// --- IDomElement ---
	
	@Override
	public Element createDomElement() {
		Element element = super.createDomElement();
		for (AbstractCondition condition : conditions) {
			element.addContent(condition.createDomElement());
		}
		return element;
	}

	// --- ListModel<AbstractCondition> ---
	
	@Override
	public int getSize() {
		return conditions.size();
	}

	@Override
	public AbstractCondition getElementAt(int index) {
		return conditions.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
	}

}
