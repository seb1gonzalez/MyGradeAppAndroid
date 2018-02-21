package edu.utep.cs.cs4330.mygrade;
import java.util.Collections;
import java.util.List;
/**
 * Created by sebas on 2/15/2018.
 */
/**
 * A student's grade consisting of an estimated final letter grade,
 * weighted total points, and details of earned points.
 */

public class Grade {
    /** Estimated final letter grade. */
    public final String grade;

    /** Weighted total points. */
    public final int total;

    /** Details of earned points */
    private final List<Score> scores;

    /** Creates a new grade. */
    public Grade(String grade, int total, List<Score> scores) {
        this.grade = grade;
        this.total = total;
        this.scores = scores;
    }

    /** Return the details of this grade. */
    public List<Score> scores() {
        return Collections.unmodifiableList(scores);
    }

    /** A score consisting of a name, the maximum points, and
     *  the earned points. */
    public static class Score {
        /** Name of this score. E.g., hw1. */
        public final String name;

        /** Maximum points. E.g., 100. */
        public final int max;

        /** Earned points. E.g., 95. */
        public final int earned;

        /** Create a score consisting of the given values. */
        public Score(String name, int max, int earned) {
            this.name = name;
            this.max = max;
            this.earned = earned;
        }
    }

}
