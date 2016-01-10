package incidentsservice

import grails.converters.JSON
import grails.rest.RestfulController

import java.text.SimpleDateFormat

class IncidentController extends RestfulController<Incident> {

    static responseFormats = ['json']

    @Override
    def save() {
        def jsonParams = request.JSON
        def location = new Location(longitude: jsonParams.longitude, latitude: jsonParams.latitude)

        if(location.validate()) {
            location.save()
        } else{
            render(status: 422, text: "Invalid coordinates of $jsonParams.longitude, $jsonParams.latitude")
            return
        }

        def dateTimeParam = jsonParams.dateTime ? parse(jsonParams.dateTime) : null

        if(!dateTimeParam) {
            render(status: 422, text: "Must enter date with the format 'yyyy-MM-dd'T'HH:mm:ssX'")
            return
        }

        def incident = new Incident(description: jsonParams.description, location: location, date: dateTimeParam)

        if(incident.validate()) {
            incident.save()
        } else {
            render(status: 422, text: "Description must be non-empty")
            return
        }

        JSON.use('deep'){
            render incident as JSON
        }
    }

    @Override
    def delete(){
        if(!params.id) {
            render(status: 400, text: "Provide an ID of an incident to delete")
            return
        }

        def incidentToDelete = Incident.findById(params.long('id'))
        if(!incidentToDelete) {
            render(status: 404)
            return
        }

        incidentToDelete.delete()
        render(status: 204)
    }

     private static Date parse( String input ) {
         def tz = TimeZone.getTimeZone("UTC");
         def df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
         df.setTimeZone(tz);
         df.parse(input);
    }

}
