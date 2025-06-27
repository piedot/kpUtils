package utils;

import org.rspeer.game.position.Position;

import java.util.List;
import java.util.Set;

public class kpPathing
{
    // 5x5, from -2, -2 to 2, 2
    private static final List<Position> directions = List.of(
            new Position(-2, -2), new Position(-1, -2), new Position(0, -2), new Position(1, -2), new Position(2, -2),
            new Position(-2, -1), new Position(-1, -1), new Position(0, -1), new Position(1, -1), new Position(2, -1),
            new Position(-2,  0), new Position(-1,  0), /* skip center */ new Position(1,  0), new Position(2,  0),
            new Position(-2,  1), new Position(-1,  1), new Position(0,  1), new Position(1,  1), new Position(2,  1),
            new Position(-2,  2), new Position(-1,  2), new Position(0,  2), new Position(1,  2), new Position(2,  2)
    );

    // Positions that need to be clear to step from 0,0 to directions[index]
    private static final List<List<Position>> positionsThatNeedToBeClear = List.of(
            // 0: {-2, -2}
            List.of(
                    new Position(-2, -1), new Position(-1, -2), new Position(-1, -1),
                    new Position(0, -1),  new Position(-1,  0)
            ),
            // 1: {-1, -2}
            List.of(
                    new Position(-1, -1), new Position(0, -1), new Position(0, -2)
            ),
            // 2: {0, -2}
            List.of(
                    new Position(0, -1)
            ),
            // 3: {1, -2}
            List.of(
                    new Position(0, -1), new Position(0, -2), new Position(1, -1)
            ),
            // 4: {2, -2}
            List.of(
                    new Position(1,  0), new Position(0, -1), new Position(1, -1),
                    new Position(2, -1), new Position(1, -2)
            ),
            // 5: {-2, -1}
            List.of(
                    new Position(-1,  0), new Position(-2,  0), new Position(-1, -1)
            ),
            // 6: {-1, -1}
            List.of(
                    new Position(-1,  0), new Position(0, -1)
            ),
            // 7: {0, -1}
            List.of(
                    new Position(0, -1)
            ),
            // 8: {1, -1}
            List.of(
                    new Position(1,  0), new Position(0, -1)
            ),
            // 9: {2, -1}
            List.of(
                    new Position(1,  0), new Position(2,  0), new Position(1, -1)
            ),
            // 10: {-2, 0}
            List.of(
                    new Position(-1,  0)
            ),
            // 11: {-1, 0}
            List.of(
                    new Position(-1,  0)
            ),
            // 12: {1, 0}
            List.of(
                    new Position(1,  0)
            ),
            // 13: {2, 0}
            List.of(
                    new Position(1,  0)
            ),
            // 14: {-2, 1}
            List.of(
                    new Position(-1,  0), new Position(-2,  0), new Position(-1,  1)
            ),
            // 15: {-1, 1}
            List.of(
                    new Position(-1,  0), new Position(0,  1)
            ),
            // 16: {0, 1}
            List.of(
                    new Position(0,  1)
            ),
            // 17: {1, 1}
            List.of(
                    new Position(1,  0), new Position(0,  1)
            ),
            // 18: {2, 1}
            List.of(
                    new Position(1,  0), new Position(2,  0), new Position(1,  1)
            ),
            // 19: {-2, 2}
            List.of(
                    new Position(-2,  1), new Position(-1,  2), new Position(-1,  1),
                    new Position(0,   1), new Position(-1,  0)
            ),
            // 20: {-1, 2}
            List.of(
                    new Position(0,   1), new Position(0,   2), new Position(-1,  1)
            ),
            // 21: {0, 2}
            List.of(
                    new Position(0,   1)
            ),
            // 22: {1, 2}
            List.of(
                    new Position(0,   1), new Position(0,   2), new Position(1,   1)
            ),
            // 23: {2, 2}
            List.of(
                    new Position(2,   1), new Position(1,   2), new Position(1,   1),
                    new Position(0,   1), new Position(1,   0)
            )
    );

    public static boolean IsSingleSteppable(Position origin, Position destination, Set<Position> walkablePositions)
    {
        if (origin == null || destination == null)
            return false;

        if (origin.equals(destination))
        {
            return true;
        }

        Position offset = kpUtils.Subtract(destination, origin);

        // TODO Optimize
        // Find the index of the offset in the directions list
        int index = -1;
        for (int i = 0; i < directions.size(); i++)
        {
            Position direction = directions.get(i);
            if (direction.getX() == offset.getX() && direction.getY() == offset.getY())
            {
                index = i;
                break;
            }
        }

        // If the offset is not in the directions list, it's not a valid step
        if (index == -1)
        {
            return false;
        }

        // Direct step check: if it's within -1 to 1 in both axes, allow it without clearance check
        if (Math.abs(offset.getX()) <= 1 && Math.abs(offset.getY()) <= 1)
        {
            return walkablePositions.contains(destination);
        }

        List<Position> requiredClear = positionsThatNeedToBeClear.get(index);
        for (Position required : requiredClear)
        {
            Position translated = origin.translate(required.getX(), required.getY());
            if (!walkablePositions.contains(translated))
            {
                return false;
            }
        }

        return walkablePositions.contains(destination);
    }
}
