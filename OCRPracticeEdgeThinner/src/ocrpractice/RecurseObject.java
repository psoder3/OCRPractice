package generatelist;

import java.util.List;

/**
 * Created by psoder3 on 5/29/2015.
 */
class RecurseObject {
    int X;
    int Y;
    int currentDistanceFromOrigin;
    RecurseObject predecessor;

    RecurseObject(double X, double Y, double currentDistanceFromOrigin) {
        this.X = X;
        this.Y = Y;
        this.currentDistanceFromOrigin = currentDistanceFromOrigin;
    }

    public RecurseObject(double X, double Y, RecurseObject predecessor) {
        this.X = X;
        this.Y = Y;
        this.predecessor = predecessor;
    }
}
