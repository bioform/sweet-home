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
        "/jobs"(controller: 'job', action: 'index')

        "/devices/$id/read"(controller: 'device', action: 'read')
        "/devices/$id?"(resources:'device', includes:['index', 'save'])
        "/locations/$id?"(resources:'location', includes:['index', 'save'])
        "/scripts/$id?"(resources:'script', includes:['index', 'save'])


        "500"(view:'/error')
	}
}
