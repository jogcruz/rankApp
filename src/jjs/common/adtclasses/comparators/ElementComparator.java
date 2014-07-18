package jjs.common.adtclasses.comparators;

import java.util.Comparator;

import jjs.common.adtclasses.interfaces.IElement;

public class ElementComparator implements Comparator<IElement> {

	public int compare(IElement o1, IElement o2) {
		return (int) (o1.getId() - o2.getId());
	}

}
