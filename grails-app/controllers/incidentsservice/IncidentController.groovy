package incidentsservice

import grails.converters.JSON
import grails.rest.RestfulController

class IncidentController extends RestfulController<Incident> {

    static responseFormats = ['json']

    @Override
    def save() {
        def jsonParams = request.JSON
        def location = new Location(longitude: jsonParams.longitude, latitude: jsonParams.latitude)

        if(location.validate()) {
            location.save()
        } else{
            render(status: 422, text: "Invalid coordinates of $jsonParams.longitude, $jsonParams.latitude.")
            return
        }

        def incident = new Incident(description: jsonParams.description, location: location)

        if(incident.validate()) {
            incident.save()
        } else {
            render(status: 422, text: "Description must be non-empty.")
            return
        }

        JSON.use('deep'){
            render incident as JSON
        }
    }
}
