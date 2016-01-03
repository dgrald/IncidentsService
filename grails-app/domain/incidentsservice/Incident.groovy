package incidentsservice

class Incident {

    String description
    Location location

    static constraints = {
        description(blank: false, nullable: false)
    }
}
