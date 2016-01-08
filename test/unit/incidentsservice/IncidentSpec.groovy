package incidentsservice
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.validation.ValidationException
import spock.lang.Specification
import util.Some

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Incident)
@Mock(Location)
class IncidentSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "null or blank description throws validation exception"() {
        when:
        new Incident(location: new Location(longitude: 10, latitude: 10), description: desc, date: Some.dateTime()).save(failOnError: true, flush: true)

        then:
        def exception = thrown(ValidationException)
        exception.message.contains('description')

        where:
        desc << [null, '']
    }

    void "null location throws validation exception"(){
        when:
        new Incident(description: 'description', date: Some.dateTime()).save(failOnError: true, flush: true)

        then:
        def exception = thrown(ValidationException)
        exception.message.contains('location')
    }

    void "null date throws validation exception"(){
        when:
        new Incident(description: 'description', location: new Location(longitude: 10, latitude: 10)).save(failOnError: true, flush: true)

        then:
        def exception = thrown(ValidationException)
        exception.message.contains('date')
    }

    void "sorts by date"(){
        when:
        def numOfIncidentsToCreate = 50
        createIncidents(numOfIncidentsToCreate)

        then:
        def allIncidents = Incident.all
        allIncidents.size() == numOfIncidentsToCreate
        def incidentDateTimes = allIncidents.collect{it.date}
        incidentDateTimes.sort() == incidentDateTimes
    }

    private static def createIncidents(numToCreate) {
        (1..numToCreate).each{
            new Incident(description: 'description', location: new Location(longitude: 10, latitude: 10), date: Some.dateTime()).save(failOnError: true, flush: true)
        }
    }
}
