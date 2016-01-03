class UrlMappings {

	static mappings = {
        "/incidents/$id?"(controller: 'incident') {
            action = [POST: 'save']
        }

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
