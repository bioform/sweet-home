var deviceModels = angular.module('deviceModel', ['ngResource']);

deviceModels.factory('Device', ['$resource', '$http',
    function($resource, $http){
        var Device = $resource('/devices/:id', {id: '@id'});

        Device.prototype.$read = function $read(){
            return $http.get('/devices/' + this.id + '/read');
        };

        return Device;
    }]);