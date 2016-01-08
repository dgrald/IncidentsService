package incidentsservice

class Incident {

    String description
    Location location
    Date date

    static constraints = {
        description(blank: false, nullable: false)
    }
}