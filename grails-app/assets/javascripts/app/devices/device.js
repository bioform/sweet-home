var deviceModels = angular.module('deviceModel', ['ngResource']);

deviceModels.factory('Device', ['$resource',
    function($resource){
        return $resource('/devices/:id', {id: '@id'});
    }]);