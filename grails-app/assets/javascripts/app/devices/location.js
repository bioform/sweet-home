var deviceModels = angular.module('locationModel', ['ngResource']);

deviceModels.factory('Location', ['$resource',
    function($resource){
        return $resource('/locations/:id', {id: '@id'});
    }]);