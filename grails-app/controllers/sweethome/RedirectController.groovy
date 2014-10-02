package sweethome

class RedirectController {

    def index() { 
      redirect(url: "${params.destination}", permanent: true)
    }
}
