package transactor.language;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.io.Serializable;

public class Worldview implements Serializable {
    private HashMap<String, History> histMap;
    private HashMap<String, HashSet<String> > depGraph;
    private HashSet<String> rootSet;

    public Worldview() {
        histMap = new HashMap<>();
        depGraph = new HashMap< >();
        rootSet = new HashSet<>();
    }
    
    public Worldview(HashMap<String, History> hmap, HashMap<String, HashSet<String> > deps, HashSet<String> roots) {
        histMap = new HashMap<>();
        for (String t : hmap.keySet()) {
            histMap.put(t, new History(hmap.get(t)));
        }
        depGraph = new HashMap< >();
        for (String t : deps.keySet()) {
            depGraph.put(t, new HashSet<String>());
            depGraph.get(t).addAll(deps.get(t));
        }
        rootSet = new HashSet<>();
        rootSet.addAll(roots);
    }
    // Tuple structure class for use in worldview union algorithm
    private class Tuple<X, Y> {
        private final X first;
        private final Y second;

        public Tuple(X first, Y second) {
            this.first = first;
            this.second = second;
        }

        public X getFirst() {
            return first;
        }

        public Y getSecond() {
            return second;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Worldview.Tuple))
                return false;
            Tuple<X, Y> otherTuple = (Tuple<X, Y>) other;
            return first.equals(otherTuple.first) && second.equals(otherTuple.second);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + Objects.hashCode(this.first);
            hash = 67 * hash + Objects.hashCode(this.second);
            return hash;
        }

        @Override
        public String toString() {
            return "( " + first.toString() 
                + ", " + second.toString() + ") ";
        }
    }
    
    // Worldview union algorithm as described by the transactor model
    public Worldview union(Worldview other) {
        HashMap<String, HashSet<History> > V = new HashMap< >();
        HashSet<Worldview.Tuple<Worldview.Tuple<String, History>, Worldview.Tuple<String, History> > > E; 
        E = new HashSet< >();

        // Loop through names and collect histories from both histMaps to add into V
        HashSet<String> names = new HashSet<>();
        names.addAll(histMap.keySet());
        names.addAll(other.histMap.keySet());
        for (String element : names) {
            HashSet<History> hists = new HashSet<>();
            if (histMap.containsKey(element))
                hists.add(histMap.get(element));
            if (other.histMap.containsKey(element))
                hists.add(other.histMap.get(element));
            V.put(element, hists);
        }
        /* For debugging....
        for (String t : V.keySet()) { 
            System.out.println(t + ":");
            for (History h : V.get(t))
                System.out.println(h);
        }
        */
        // Loop through depedencies and create set of dependency map edges in E
        for (String t : depGraph.keySet()) {
            Worldview.Tuple<String, History> t_h = new Worldview.Tuple<>(t, histMap.get(t));
            for (String t_dep : depGraph.get(t)) {
                Worldview.Tuple<String, History> t_h_dep = new Worldview.Tuple<>(t_dep, histMap.get(t_dep));
                E.add(new Worldview.Tuple< >(t_h, t_h_dep));
            }
        }
        for (String t : other.depGraph.keySet()) {
            Worldview.Tuple<String, History> t_h = new Worldview.Tuple<>(t, other.histMap.get(t));
            for (String t_dep : other.depGraph.get(t)) {
                Worldview.Tuple<String, History> t_h_dep = new Worldview.Tuple<>(t_dep, other.histMap.get(t_dep));
                E.add(new Worldview.Tuple< >(t_h, t_h_dep));
            }
        }
        /* For debugging....
        for (Worldview.Tuple<Worldview.Tuple<String, History>, Worldview.Tuple<String, History> > e : E)
            System.out.println(e);
        */
        boolean done = false;
       
        while (!done) {
            done = true;
            for (String t : V.keySet()) {
                /* Transactor t will only have history versions that must be different due to 
                 * the requirement of a HashSet assuming the toequals() and hashCode() methods are implemented 
                 * correctly in the History class, therefore one must succeed the other
                 */
                if (V.get(t).size() > 1) {
                    done = false;
                    Iterator<History> h = V.get(t).iterator();
                    History first = h.next();
                    History second = h.next();
                    History temp;
                    if (first.succeeds(second)) {
                        temp = first;
                        first = second;
                        second = temp;
                    }
                    HashSet<Worldview.Tuple<Worldview.Tuple<String, History>, Worldview.Tuple<String, History> > > new_E = new HashSet< >();
                    Worldview.Tuple<String, History> current = new Tuple<>(t, first);
                    if (second.stablizesFrom(first)) {
                        Worldview.Tuple<String, History> updated = new Tuple<>(t, second);
                        for (Worldview.Tuple<Worldview.Tuple<String, History>, Worldview.Tuple<String, History> > e : E) {
                            if (e.getFirst().equals(current)) {
                                new_E.add(new Worldview.Tuple< >(updated, e.getSecond()));
                            }
                            else if (e.getSecond().equals(current)) {
                                new_E.add(new Worldview.Tuple< >(e.getFirst(), updated));
                            }
                        }
                    }
                    else if (second.invalidates(first)) {
                        for (Worldview.Tuple<Worldview.Tuple<String, History>, Worldview.Tuple<String, History> > e : E) {
                            if (e.getSecond().equals(current)) {
                                History updated_hist = new History(e.getFirst().getSecond());
                                updated_hist.rollback();
                                V.get(e.getFirst().getFirst()).add(updated_hist);
                            }
                        }
                    }
                    else if (second.validates(first)) {
                        HashSet<Worldview.Tuple<String, History> > transitive = new HashSet< >();
                        // For dependency cycle detection
                        HashSet<Worldview.Tuple<String, History> > found = new HashSet< >();
                        transitive.add(current);
                        while (transitive.size() > 0) {
                            HashSet<Worldview.Tuple<String, History> > new_transitive = new HashSet< >();
                            for (Worldview.Tuple<String, History> find : transitive) {
                                for (Worldview.Tuple<Worldview.Tuple<String, History>, Worldview.Tuple<String, History> > e : E) {
                                    if (e.getFirst().equals(find) && !found.contains(e.getSecond())) {
                                        new_transitive.add(e.getSecond());
                                        History updated_hist = new History(e.getSecond().getSecond());
                                        updated_hist.stabilize();
                                        V.get(e.getSecond().getFirst()).add(updated_hist);
                                    }
                                }
                            }
                            found.addAll(transitive);
                            transitive.clear();
                            transitive.addAll(new_transitive);
                        }
                    }
                    V.get(t).remove(first);
                    for (Worldview.Tuple<Worldview.Tuple<String, History>, Worldview.Tuple<String, History> > e : E) {
                        if (!e.getFirst().equals(current) && !e.getSecond().equals(current)) {
                            new_E.add(e);
                        }
                    }
                    E = new_E;           
                }
            }
        }
        
        Worldview updated_worldview = new Worldview();
        HashMap<String, History> new_histMap = new HashMap<>();
        HashMap<String, HashSet<String> > new_depGraph = new HashMap< >();
        HashSet<String> new_rootSet = new HashSet<>();
        // Add transactor-history mappings to new histMap; each transactor should now only map to one updated history
        // NOTE: inner loop should only iterate once 
        for (String t : V.keySet())
            for (History h : V.get(t))
                new_histMap.put(t, h);
        for (Worldview.Tuple<Worldview.Tuple<String, History>, Worldview.Tuple<String, History> > e : E) {
            String key = e.getFirst().getFirst();
            HashSet<String> newDeps = new HashSet<>();
            newDeps.add(e.getSecond().getFirst());
            if (new_depGraph.containsKey(key)) 
                newDeps.addAll(new_depGraph.get(key));
            new_depGraph.put(key, newDeps);
        }
        for (String t : other.rootSet) {
            if (!new_histMap.get(t).validates(other.histMap.get(t)))
                new_rootSet.add(t);
        }
        updated_worldview.setHistMap(new_histMap);
        updated_worldview.setDepGraph(new_depGraph);
        updated_worldview.setRootSet(new_rootSet);
        
        return updated_worldview;
    }

    // Check if this worldview invalidates anothers history map
    // NOTE: This should be called from the unionized worldview against the transactor name or message dependencies
    // or a null pointer error might occur from missing history mappings
    public boolean invalidates(HashMap<String, History> other, HashSet<String> domain) {
        for (String t : domain) {
            if (histMap.get(t).invalidates(other.get(t)))
                return true;
        }
        return false;
    }
    
    // Auxiliary function to gather closure of transitive dependencies 
    private void getDependencies(String t, HashSet<String> closure) {
        if (closure.add(t)) {
            if (depGraph.get(t) == null) return;
            for (String child : depGraph.get(t)) {
                getDependencies(child, closure);
            }
        }
    }
    
    // Tests weak independence of t; t itself may not be stable
    public boolean independent(String t) {
        HashSet<String> depClosure = new HashSet<>();
        getDependencies(t, depClosure);
        for (String dep : depClosure) {
           if (!t.equals(dep) && !histMap.get(dep).isStable())
               return false;
        }
        return true;
    }
    
    public void setHistMap(HashMap<String, History> histMap) {
        this.histMap = histMap;
    }

    public void setDepGraph(HashMap<String, HashSet<String> > depGraph) {
        this.depGraph = depGraph;
    }

    public void setRootSet(HashSet<String> rootSet) {
        this.rootSet = rootSet;
    }

    public HashMap<String, History> getHistMap() {
        return histMap;
    }

    public HashMap<String, HashSet<String> > getDepGraph() {
        return depGraph;
    }

    public HashSet<String> getRootSet() {
        return rootSet;
    }
    
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("History Map: \n");
        for (String t : histMap.keySet()) {
            output.append(t).append("->").append(histMap.get(t).toString()).append(", ");
        }
        output.append("\nDep Graph:\n");
        for (String t : depGraph.keySet()) {
            for (String t_dep : depGraph.get(t))
                output.append(t).append("<-").append(t_dep).append(", ");
        }
        output.append("\nRoot Set:\n");
        for (String t : rootSet)
            output.append(t).append(", ");
        
        return output.toString();
    }
}
