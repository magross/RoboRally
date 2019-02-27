/*
 * EffectManager.java
 *
 */
package roborally.effect;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import roborally.Timing;

/**
 *
 * @author Martin Groß
 */
public class EffectManager {

    private EnumMap<Timing, List<Effect>> effects;

    public EffectManager() {
        effects = new EnumMap<Timing, List<Effect>>(Timing.class);
        for (Timing timing : Timing.values()) {
            effects.put(timing, new LinkedList<Effect>());
        }
    }

    public void add(Timing timing, Effect effect) {
        int index = 0;
        while (index < effects.get(timing).size() && effect.getPriority() < effects.get(timing).get(index).getPriority()) {
            index++;
        }
        effects.get(timing).add(index, effect);
    }

    public List<Effect> get(Timing timing) {
        return effects.get(timing);
    }
}
