package incidentsservice
import grails.test.spock.IntegrationSpec
import org.apache.commons.lang.time.DateUtils
import spock.lang.Shared

import java.text.SimpleDateFormat
/**
 *
 */
class IncidentControllerIntegrationSpec extends IntegrationSpec {

    @Shared IncidentController controller = new IncidentController()

    def originalIncidentDate
    def originalIncidentDateTimeString
    def originalLocation
    def originalIncident

    def date
    def dateTimeString

    def setup() {
        originalIncidentDate = someDate()
        originalLocation = new Location(longitude: 22, latitude: 22).save(flush: true, failOnError: true)
        originalIncident = new Incident(location: originalLocation, description: 'initial incident', date: originalIncidentDate)
        originalIncident.save(flush: true, failOnError: true)
        originalIncidentDateTimeString = toString(originalIncidentDate)

        date = someDate()
        dateTimeString = toString(date)
    }

    def cleanup() {
        controller.response.reset()
    }

    void "can retrieve all incidents"() {
        when:
        controller.response.format = 'json'
        controller.request.method = 'GET'
        controller.index()
        def response = controller.response

        then:
        response.status == 200
        response.contentAsString == "[{\"class\":\"incidentsservice.Incident\",\"id\":1,\"date\":\"$originalIncidentDateTimeString\",\"description\":\"initial incident\",\"location\":{\"class\":\"incidentsservice.Location\",\"id\":1,\"latitude\":22,\"longitude\":22}}]"
    }

    void "test creating an incident"() {
        when:
        controller.request.json = [
                description: 'description',
                latitude: 10,
                longitude: 10,
                dateTime: dateTimeString
        ]
        controller.save()
        def response = controller.response

        then:
        response.status == 200
        response.json.location.latitude == 10
        response.json.location.longitude == 10
        response.json.description == 'description'
        parse(response.json.date) == date
        Incident.count == 2
    }

    void "error validation with location returns 422"(BigDecimal longitude, BigDecimal latitude) {
        when:
        controller.request.json = [
                description: 'description',
                latitude: latitude,
                longitude: longitude,
                dateTime: dateTimeString
        ]
        controller.save()

        then:
        controller.response.status == 400
        controller.response.contentAsString == "Invalid coordinates of $longitude, $latitude"

        where:
        longitude | latitude
        20        | 100
        20        | -100
        -200      | 0
        200       | 0
        2000      | 20000
    }

    void "null description returns 422"(){
        when:
        controller.request.json = [
                description: description,
                latitude: 20,
                longitude: 20,
                dateTime: dateTimeString
        ]
        controller.save()
        def response = controller.response

        then:
        response.status == 400
        response.contentAsString == "Description must be non-empty"

        where:
        description << [null, '']
    }

    void "null dateTime returns 422"(){
        when:
        controller.request.json = [
                description: 'description',
                latitude: 20,
                longitude: 20,
        ]
        controller.save()
        def response = controller.response

        then:
        response.status == 400
        response.contentAsString == "Must enter date with the format 'yyyy-MM-dd'T'HH:mm:ssX'"
    }

    void "can delete an incident"() {
        when:
        def incidentToDelete = createIncident()
        def id = incidentToDelete.id.toString()
        controller.request.parameters = [id: id]
        controller.delete()

        then:
        controller.response.status == 204
        Incident.findById(id) == null
    }

    void "no id param responds with 400"() {
        when:
        createIncident()
        controller.request.parameters = [:]
        controller.delete()

        then:
        controller.response.status == 400
        controller.response.contentAsString == "Provide an ID of an incident to delete"
    }

    void "incident id not found returns 404"() {
        when:
        def id = Incident.findAll().sort{it.id}.last().id + 1
        controller.request.parameters = [id: id.toString()]
        controller.delete()

        then:
        controller.response.status == 404
    }

    private def createIncident() {
        def location = new Location(longitude: 24, latitude: 22).save(flush: true, failOnError: true)
        def incident = new Incident(location: location, description: 'an incident', date: someDate())
        incident.save(flush: true, failOnError: true)
        incident
    }

    private static def someDate(){
        def min = 1293861599L
        def max = 1325397600L
        def date = new Date(new Random().nextLong() % (max - min) + min);
        DateUtils.round(date, Calendar.SECOND);
    }

    private static String toString( Date date ) {
        getDateFormatter().format(date)
    }

    private static Date parse( String input ) {
        getDateFormatter().parse(input)
    }

    private static SimpleDateFormat getDateFormatter() {
        def tz = TimeZone.getTimeZone("UTC");
        def df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        df.setTimeZone(tz);
        df
    }
}
