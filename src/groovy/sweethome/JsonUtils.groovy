package sweethome

class JsonUtils {
    static def gorm_errors(results) {
        if(!results || !results.fieldErrors){
            return null
        }
        results =  results.fieldErrors.toList()
        def errors = []
        for(error in results) {
            errors.add([
                    'type' : 'invalid_entry',
                    'field' : error.field,
                    'rejected_value' : error.rejectedValue,
                    'message' : error.defaultMessage
            ])
        }
        return errors
    }
}
