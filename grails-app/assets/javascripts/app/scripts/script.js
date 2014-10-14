var deviceModels = angular.module('scriptModel', ['ngResource']);

deviceModels.factory('Script', ['$resource', '$http',
    function($resource, $http){
        return $resource('/scripts/:id', {id: '@id'});
    }]);