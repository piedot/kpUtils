package pathing;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.position.Position;
import utils.kpPathing;

import java.util.*;

/**
 * The name 'pie' is just my name, this pathfinder can be used for any local pathfinding needs.
 * Does not and will not support multi-floor level or obstacle pathfinding, just direct position to position pathfinding.
 */
public class piePathFinder
{
    private Position start; // Starting position of the pathfinder
    private Position destination; // Destination position to reach
    private Map<Position, Integer> scoreMap; // Map of positions with their scores, lower is better
    private Set<Position> walkablePositions; // These are the ONLY positions we can use in the pathfinding
    private List<Position> path; // The resulting path from start to destination, if found
    private List<Position> oldPath = new ArrayList<>();

    // 5x5, from -2, -2 to 2, 2
    private final List<Position> directions = List.of(
            new Position(-2, -2), new Position(-1, -2), new Position(0, -2), new Position(1, -2), new Position(2, -2),
            new Position(-2, -1), new Position(-1, -1), new Position(0, -1), new Position(1, -1), new Position(2, -1),
            new Position(-2,  0), new Position(-1,  0), /* skip center */ new Position(1,  0), new Position(2,  0),
            new Position(-2,  1), new Position(-1,  1), new Position(0,  1), new Position(1,  1), new Position(2,  1),
            new Position(-2,  2), new Position(-1,  2), new Position(0,  2), new Position(1,  2), new Position(2,  2)
    );

    // Positions that need to be clear to step from 0,0 to directions[index]
    private final List<List<Position>> positionsThatNeedToBeClear = List.of(
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

    private Map<Position, Double> fScore = new HashMap<>(); // For debugging
    public Map<Position, Double> getFScore()
    {
        return Collections.unmodifiableMap(fScore);
    }

    private class PositionRecord implements Comparable<PositionRecord>
    {
        Position position;
        double gScore; // Cost from start to this position
        double fScore; // gScore + heuristic (estimated cost to destination)

        public PositionRecord(Position position, double gScore, double fScore)
        {
            this.position = position;
            this.gScore = gScore;
            this.fScore = fScore;
        }

        @Override
        public int compareTo(PositionRecord other)
        {
            return Double.compare(this.fScore, other.fScore);
        }
    }

    public piePathFinder(Position start, Position destination, Set<Position> walkablePositions)
    {
        this.start = start;
        this.destination = destination;
        this.walkablePositions = walkablePositions;
    }

    public piePathFinder setScores(Map<Position, Integer> scoreMap)
    {
        this.scoreMap = scoreMap;
        return this;
    }

    public piePathFinder calculatePath()
    {
        if (path == null)
        {
            path = new ArrayList<>();
        }
        else
        {
            path.clear();
        }

        fScore.clear();

        if (start == null || destination == null || walkablePositions == null)
        {
            Log.warn("piePathFinder: Missing required parameters for pathfinding.");
            return this;
        }

        if (start.equals(destination))
        {
            Log.debug("piePathFinder: Start and destination are the same, no path needed.");
            path.add(start);
            return this;
        }

        Log.debug("piePathFinder: Calculating path from " + start + " to " + destination);

        // A* algorithm implementation
        PriorityQueue<PositionRecord> open = new PriorityQueue<>();
        Set<Position> closed = new HashSet<>();
        Map<Position, Position> cameFrom = new HashMap<>();
        Map<Position, Double> gScore = new HashMap<>();
        fScore = new HashMap<>();

        // Initialize with start position
        gScore.put(start, 0.0);
        double startFScore = heuristic(start, destination);
        fScore.put(start, startFScore);
        open.add(new PositionRecord(start, 0, startFScore));

        while (!open.isEmpty())
        {
            PositionRecord current = open.poll();

            // If we reached the destination, reconstruct and return the path
            if (current.position.equals(destination))
            {
                reconstructPath(cameFrom, current.position);
                Log.debug("piePathFinder: Path found with " + path.size() + " steps.");
                return this;
            }

            closed.add(current.position);

            for (Position neighbor : getNeighbors(current.position))
            {
                if (closed.contains(neighbor))
                {
                    continue;
                }

                // Calculate tentative gScore (including danger level from scoreMap if available)
                double tentativeGScore = gScore.get(current.position) + 1;

                // Add danger level cost from scoreMap if available
                if (scoreMap != null && scoreMap.containsKey(neighbor))
                {
                    tentativeGScore += scoreMap.get(neighbor);
                }

                // If we found a better path or this neighbor hasn't been evaluated yet
                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor))
                {
                    // Record this path
                    cameFrom.put(neighbor, current.position);
                    gScore.put(neighbor, tentativeGScore);
                    double neighborFScore = tentativeGScore + heuristic(neighbor, destination);
                    fScore.put(neighbor, neighborFScore);

                    // Add to open set if not already there
                    boolean inOpenSet = false;
                    for (PositionRecord record : open)
                    {
                        if (record.position.equals(neighbor))
                        {
                            inOpenSet = true;
                            break;
                        }
                    }

                    if (!inOpenSet)
                    {
                        open.add(new PositionRecord(neighbor, tentativeGScore, neighborFScore));
                    }
                }
            }
        }

        Log.warn("piePathFinder: No path found from " + start + " to " + destination);
        return this;
    }

    private void reconstructPath(Map<Position, Position> cameFrom, Position current)
    {
        List<Position> totalPath = new ArrayList<>();
        totalPath.add(current);

        while (cameFrom.containsKey(current))
        {
            current = cameFrom.get(current);
            totalPath.add(0, current);
        }

        path = totalPath;

        boolean isOldPathValid = true;
        for (Position position : oldPath)
        {
            if (!path.contains(position))
            {
                isOldPathValid = false;
                break;
            }
        }

        if (isOldPathValid && !oldPath.isEmpty())
        {
            // If the old path is still valid, we should use it to avoid getting stuck between two positions
            Log.debug("piePathFinder: Using old path as fallback.");
            path = oldPath;
        }
        else
        {
            Log.debug("piePathFinder: New path calculated, replacing old path.");
        }

        oldPath = totalPath;
    }

    private double heuristic(Position a, Position b)
    {
        int chebyshevDistance = Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY()));
        double euclideanDistance = Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
        return chebyshevDistance * 10 + euclideanDistance * 10;
    }

    private double getChebyshevDistance(Position a, Position b)
    {
        // Chebyshev distance for grid-like movement
        return Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY())) * 10;
    }

    private List<Position> getNeighbors(Position position)
    {
        List<Position> neighbors = new ArrayList<>();
        //for (Position direction : directions)
        //{
        //    if (kpPathing.IsSingleSteppable(position, direction, walkablePositions))
        //    {
        //        Position neighbor = position.translate(direction.getX(), direction.getY());
        //        neighbors.add(neighbor);
        //    }
        //}

        for (int i = 0; i < directions.size(); i++)
        {
            Position direction = directions.get(i);
            int directionX = direction.getX();
            int directionY = direction.getY();

            Position neighbor = position.translate(directionX, directionY);
            boolean isClear = true;

            // Only check for collision on tiles that are not directly adjacent since it is guaranteed that we can step on them in one tick
            if (directionX > 1 || directionX < -1 || directionY > 1 || directionY < -1)
            {
                List<Position> positionsThatNeedToBeClearForThisPosition = positionsThatNeedToBeClear.get(i);
                for (int j = 0; j < positionsThatNeedToBeClearForThisPosition.size(); j++)
                {
                    Position clearPosition = positionsThatNeedToBeClearForThisPosition.get(j);
                    Position translatedPosition = position.translate(clearPosition.getX(), clearPosition.getY());

                    if (!walkablePositions.contains(translatedPosition))
                    {
                        isClear = false;
                        break;
                    }
                }
            }

            if (isClear && walkablePositions.contains(neighbor))
            {
                neighbors.add(neighbor);
            }
        }

        for (Position direction : directions)
        {
            Position neighbor = new Position(position.getX() + direction.getX(), position.getY() + direction.getY());
            if (walkablePositions.contains(neighbor))
            {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    public Position getNextWalkPosition()
    {
        if (path == null || path.isEmpty())
        {
            Log.warn("piePathFinder: No path available to get next position.");
            return null;
        }

        return path.get(Math.min(1, path.size() - 1));
    }

    public Position getStart()
    {
        return start;
    }

    public Position getDestination()
    {
        return destination;
    }

    public Map<Position, Integer> getScoreMap()
    {
        return scoreMap;
    }

    public Set<Position> getWalkablePositions()
    {
        return walkablePositions;
    }

    public List<Position> getPath()
    {
        return path;
    }

    public boolean foundPath()
    {
        return path != null && !path.isEmpty();
    }
}
