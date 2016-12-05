package controller.tsp;

import java.util.Collection;
import java.util.Iterator;

import model.Checkpoint;
import model.DeliveryTime;

public class IteratorSeq implements Iterator<DeliveryTime> {

	private DeliveryTime[] candidats;
	private int nbCandidats;

	/**
	 * Cree un iterateur pour iterer sur l'ensemble des sommets de nonVus
	 * @param nonVus
	 * @param sommetcrt
	 */
	public IteratorSeq(Collection<DeliveryTime> nonVus, DeliveryTime sommetcrt){
		this.candidats = new DeliveryTime[nonVus.size()];
		nbCandidats = 0;
		for (DeliveryTime s : nonVus){
			candidats[nbCandidats++] = s;
		}
	}
	
	@Override
	public boolean hasNext() {
		return nbCandidats > 0;
	}

	@Override
	public DeliveryTime next() {
		return candidats[--nbCandidats];
	}

	@Override
	public void remove() {}

}
