package cmput301w15t07.TravelTracker.util;

/*
 *   Copyright 2015 Kirby Banman,
 *                  Stuart Bildfell,
 *                  Elliot Colp,
 *                  Christian Ellinger,
 *                  Braedy Kuzma,
 *                  Ryan Thornhill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * A class which can be observed by Observer.
 * 
 * @author kdbanman
 *
 * @param <E>
 */
public abstract class Observable <E> {
    private List<Observer<E>> observers = new ArrayList<Observer<E>>();

    public Observable() {
        observers = new ArrayList<Observer<E>>();
    }

    public void addObserver(Observer<E> obs) {
    	// make sure observer is only added once.
        if (!observers.contains(obs)) observers.add(obs);
    }

    public void removeObserver(Observer<E> obs) {
        observers.remove(obs);
    }

    protected void updateObservers(E self) {
        for (Observer<E> obs : observers) {
            obs.update(self);
        }
    }
}
