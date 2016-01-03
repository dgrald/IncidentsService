package incidentsservice

import grails.test.mixin.TestFor
import grails.validation.ValidationException
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Location)
class LocationSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "two locations are equal if they have the same longitude and latitude"() {
        when:
        def loc1 = new Location(latitude: 20, longitude: 30).save(failOnError: true, flush: true)
        def loc2 = new Location(latitude: 20, longitude: 30).save(failOnError: true, flush: true)

        then:
        loc1 == loc2
    }

    void "two locations are not equal if they have different latitude"() {
        when:
        def loc1 = new Location(latitude: 20, longitude: 30).save(failOnError: true, flush: true)
        def loc2 = new Location(latitude: 21, longitude: 30).save(failOnError: true, flush: true)

        then:
        loc1 != loc2
    }

    void "two locations are not equal if they have different longitude"() {
        when:
        def loc1 = new Location(latitude: 20, longitude: 30).save(failOnError: true, flush: true)
        def loc2 = new Location(latitude: 20, longitude: 31).save(failOnError: true, flush: true)

        then:
        loc1 != loc2
    }

    void "throws validation exception when latitude is greater than 90"(){
        when:
        def loc1 = new Location(latitude: 91, longitude: 20).save(failOnError: true, flush: true)

        then:
        def exception = thrown(ValidationException)
    }

    void "throws validation exception when latitude is less than -90"(){
        when:
        def loc1 = new Location(latitude: -91, longitude: 20).save(failOnError: true, flush: true)

        then:
        def exception = thrown(ValidationException)
    }

    void "throws validation exception when longitude is greater than 180"(){
        when:
        def loc1 = new Location(latitude: 90, longitude: 181).save(failOnError: true, flush: true)

        then:
        def exception = thrown(ValidationException)
    }

    void "throws validation exception when longitude is less than -180"(){
        when:
        def loc1 = new Location(latitude: 90, longitude: -181).save(failOnError: true, flush: true)

        then:
        def exception = thrown(ValidationException)
    }
}
