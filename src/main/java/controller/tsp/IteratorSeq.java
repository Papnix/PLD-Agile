package controller.tsp;

import java.util.Collection;
import java.util.Iterator;

import model.Checkpoint;

public class IteratorSeq implements Iterator<Checkpoint> {

	private Checkpoint[] candidats;
	private int nbCandidats;

	/**
	 * Cree un iterateur pour iterer sur l'ensemble des sommets de nonVus
	 * @param nonVus
	 * @param sommetcrt
	 */
	public IteratorSeq(Collection<Checkpoint> nonVus, Checkpoint sommetcrt){
		this.candidats = new Checkpoint[nonVus.size()];
		nbCandidats = 0;
		for (Checkpoint s : nonVus){
			candidats[nbCandidats++] = s;
		}
	}
	
	@Override
	public boolean hasNext() {
		return nbCandidats > 0;
	}

	@Override
	public Checkpoint next() {
		return candidats[--nbCandidats];
	}

	@Override
	public void remove() {}

}
