package util

/**
 * Created by dgd1 on 1/7/16.
 */
class Some {

    static def dateTime(){
        def min = 1293861599L
        def max = 1325397600L
        return new Date(new Random().nextLong() % (max - min) + min);
    }
}
