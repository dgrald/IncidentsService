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

    def date
    def dateTimeString

    def setup() {
        def location = new Location(longitude: 22, latitude: 22).save(flush: true, failOnError: true)
        new Incident(location: location, description: 'initial incident', date: someDate()).save(flush: true, failOnError: true)
        date = someDate()
        dateTimeString = toString(date)
    }

    def cleanup() {
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
        def response = controller.response.json

        then:
        response.location.latitude == 10
        response.location.longitude == 10
        response.description == 'description'
        parse(response.date) == date
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
        controller.response.status == 422
        controller.response.contentAsString == "Invalid coordinates of $longitude, $latitude."

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
        response.status == 422
        response.contentAsString == "Description must be non-empty."

        where:
        description << [null, '']
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
