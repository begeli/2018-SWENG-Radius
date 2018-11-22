package ch.epfl.sweng.radius.database;

import java.util.HashSet;
import java.util.Set;

public abstract class DBObservable{
    private final Set<DBUserObserver> userObservers = new HashSet<>();
    private final Set<DBLocationObserver> locationObservers = new HashSet<>();

    public void addUserObserver(DBUserObserver observer){
        this.userObservers.add(observer);
    }

    public void removeUserObserver(DBUserObserver observer){
        this.userObservers.remove(observer);
    }
    public void notifyUserObservers(String id){
        for (DBUserObserver observer: this.userObservers) {
            observer.onUserChange(id);
        }
    }

    public void addLocationObserver(DBLocationObserver observer){
        this.locationObservers.add(observer);
    }

    public void removeLocationObserver(DBLocationObserver observer){
        this.locationObservers.remove(observer);
    }
    public void notifyLocactionObservers(String id){
        for (DBLocationObserver observer: this.locationObservers) {
            observer.onLocationChange(id);
        }
    }
}
