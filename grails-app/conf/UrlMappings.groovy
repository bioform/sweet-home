class UrlMappings {

  static excludes = [
          '/app/*',
          '/robots.txt',
          '/sitemap.xml',
          '/favicon.ico',
          '/css/*',
          '/fonts/*',
          '/images/*',
          '/img/*',
          '/js/*'
  ]

	static mappings = {
        "/" {
            controller = "redirect"
            destination = "/admin"
        }
        "/admin**"(controller: 'home', action:'index')

        "/raw/devices"(controller: 'raw', action: 'index')

        "/devices/$id?"(resources:'device', includes:['index', 'save'])
        "/locations"(resources:'location', includes:['index', 'save'])


        "500"(view:'/error')
	}
}
