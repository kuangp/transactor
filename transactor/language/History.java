package transactor.language;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.Objects;
import java.io.Serializable;

public class History implements Serializable {
    private boolean stable;
    private AbstractList<Integer> incarnation_list; 
    private int incarnation;                       

    public History() {
        stable = false;
        incarnation_list = new LinkedList< >();
        incarnation = 0;
    }

    public History(boolean stable, int incarnation, AbstractList<Integer> incarnation_list) {
        this.stable = stable;
        this.incarnation = incarnation;
        this.incarnation_list = new LinkedList< >();
        this.incarnation_list.addAll(incarnation_list);
    }
    
    public History(History other) {
        this.stable = other.stable;
        this.incarnation = other.incarnation;
        this.incarnation_list = new LinkedList< >();
        this.incarnation_list.addAll(other.incarnation_list); 
    }


    public boolean isStable() {
        return stable;
    }

    public void stabilize() {
        stable = true;
    }

    public boolean isPersistent() {
        return !incarnation_list.isEmpty();
    }

    public void checkpoint() {
        incarnation_list.add(0, incarnation);
        incarnation = 0;
        stable = false;
    }

    public void rollback() {
        stable = false;
        incarnation += 1;
    }

    public boolean succeeds(History other) {
        if (incarnation_list.size() != other.incarnation_list.size())
            return incarnation_list.size() > other.incarnation_list.size();
        else if (incarnation != other.incarnation)
            return incarnation > other.incarnation;
        else if (stable == true && other.stable == false)
            return true;
        else 
            return false;
    }

    public boolean stablizesFrom(History other) {
        History temp = new History(other);
        temp.stabilize();
        return equals(temp);
    }
    
    /* A history h1 invalidates another history h2 if and only if h1 succeeds h2 and the current incarnation of h2
     * is either less than that of h1 if their incarnation list is of the same size or less than that of the next 
     * checkpointed incarnation occuring after h2
     */
    public boolean invalidates(History other) {
        if (this.incarnation_list.size() == other.incarnation_list.size())
            return this.incarnation > other.incarnation;
        else if (this.incarnation_list.size() > other.incarnation_list.size()) {
            int diff = this.incarnation_list.size() - other.incarnation_list.size();
            return this.incarnation_list.get(diff-1) > other.incarnation;
        }
        else
            return false;
    }
    
    /* A history h1 validates h2 if and only if h1 succeeds h2 in which the incarnation list of h1 is a greater size
     * than that of h2 signaling a checkpoint operation and the checkpoint occuring right after h2 should be of equal
     * incarnation value to that of h2's current value, otherwise this signals a rollback occured before checkpointing
     */
    public boolean validates(History other) {
        if (this.incarnation_list.size() > other.incarnation_list.size()) {
            int diff = this.incarnation_list.size() - other.incarnation_list.size();
            return this.incarnation_list.get(diff-1) == other.incarnation;
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.stable ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.incarnation_list);
        hash = 97 * hash + this.incarnation;
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof History))
            return false;
        History otherHist = (History) other;
        return (incarnation == otherHist.incarnation && incarnation_list.equals(otherHist.incarnation_list) && stable == otherHist.stable);
    }

    @Override
    public String toString() {
        StringBuilder hist = new StringBuilder();
        if (stable)
            hist.append("S(");
        else
            hist.append("V(");
        hist.append(incarnation);
        hist.append(") [ ");
        for (Integer element : incarnation_list) {
            hist.append(element);
            hist.append(" ");
        }
        hist.append(" ]");
        return hist.toString();
    }
}



        



