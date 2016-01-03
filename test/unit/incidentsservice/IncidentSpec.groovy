package incidentsservice

import grails.test.mixin.TestFor
import grails.validation.ValidationException
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Incident)
class IncidentSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "null or blank description throws validation exception"() {
        when:
        def incident = new Incident(location: new Location(longitude: 10, latitude: 10), description: desc).save(failOnError: true, flush: true)

        then:
        def exception = thrown(ValidationException)

        where:
        desc << [null, '']
    }

    void "null location throws validation exception"(){
        when:
        def incident = new Incident(description: 'description').save(failOnError: true, flush: true)

        then:
        incident.location == 2
    }
}
