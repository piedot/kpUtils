package pathing;

import org.rspeer.commons.math.Distance;
import org.rspeer.commons.math.DistanceEvaluator;
import org.rspeer.game.adapter.type.SceneNode;
import org.rspeer.game.position.Position;

public class DistanceEvaluators
{
    public static DistanceEvaluator PATH_FINDER_DISTANCE = new DistanceEvaluator()
    {
        @Override
        public double evaluate(int x1, int y1, int z1, int x2, int y2, int z2)
        {
            Position start = new Position(x1, y1, z1);
            Position destination = new Position(x2, y2, z2);

            return PathFinder.distance(start, destination);
        }
    };
}
