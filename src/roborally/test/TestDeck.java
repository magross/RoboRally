/*
 * TestDeck.java
 *
 */

package roborally.test;

import java.util.LinkedList;
import java.util.List;
import roborally.Deck;
import roborally.robot.ProgramCard;
import roborally.robot.ProgramCommand;

/**
 *
 * @author Martin Groß
 */
public class TestDeck extends Deck<ProgramCard> {

    @Override
    public List<ProgramCard> drawCards(int number) {
        LinkedList<ProgramCard> result = new LinkedList<ProgramCard>();
        for (int i = 0; i < number; i++) {
            result.add(new ProgramCard(ProgramCommand.MOVE_1, 100));
        }
        return result;
    }
}
