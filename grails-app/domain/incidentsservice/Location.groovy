package incidentsservice

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes='latitude,longitude')
class Location {

    BigDecimal latitude
    BigDecimal longitude

    static constraints = {
        latitude(max: new BigDecimal(90), min: new BigDecimal(-90))
        longitude(max: new BigDecimal(180), min: new BigDecimal(-180))
    }
}
