package Modules;

/**
 * Created by ramitix on 8/12/16.
 */

import java.util.List;
import Modules.Route;




public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);

}