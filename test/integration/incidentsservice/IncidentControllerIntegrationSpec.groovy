package incidentsservice

import grails.test.spock.IntegrationSpec
import spock.lang.Shared

/**
 *
 */
class IncidentControllerIntegrationSpec extends IntegrationSpec {

    @Shared IncidentController controller = new IncidentController()

    def setup() {
        def location = new Location(longitude: 10, latitude: 10).save()
        def incident = new Incident(location: location, description: 'description').save()
    }

    def cleanup() {
    }

    void "test creating an incident"() {
        when:
        controller.request.json = [
                description: 'description',
                latitude: 10,
                longitude: 10
        ]
        controller.save()
        def response = controller.response.json

        then:
        response.location.latitude == 10
        response.location.longitude == 10
        response.description == 'description'
        Incident.count == 2
    }

    void "error validation with location returns 422"(BigDecimal longitude, BigDecimal latitude) {
        when:
        controller.request.json = [
                description: 'description',
                latitude: latitude,
                longitude: longitude
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
                longitude: 20
        ]
        controller.save()
        def response = controller.response

        then:
        response.status == 422
        response.contentAsString == "Description must be non-empty."

        where:
        description << [null, '']
    }
}
