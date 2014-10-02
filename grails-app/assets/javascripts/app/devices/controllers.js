angular.module( 'deviceControllers', [

])

    .config(['$routeProvider', function config( $routeProvider ) {
        $routeProvider.
            when('/devices', {
                templateUrl: '/assets/app/devices/devices.htm',
                controller: 'DeviceListCtrl',
                menu: 'devices'
            })
    }])
    .filter('byLocation', function() {
        return function(devices, query) {
            var ids = $.map(query, function(v, k){return v ? parseInt(k):null});
            if(ids.length == 0){
                return devices;
            }
            return $.map(devices, function(d){return (d.location != null && ids.indexOf(d.location.id) != -1) ? d:null});;
        };
    })
    .controller('DeviceListCtrl', ['$scope','$filter', 'Device', 'Location',
        function ($scope, $filter, Device, Location) {
            $scope.devices = Device.query();

            // edit device title
            $scope.updateTitle = function(device, newTitle) {
                device.title = newTitle;
                var enabled = device.enabled;
                return device.$save().then(function(data){ data.enabled = enabled; return data; });
            };
            // edit device location
            $scope.emptyOption = [{value: null, text: 'Not set'}];
            $scope.showLocation = function showLocation(device) {
                var selected = $filter('filter')($scope.locations, {id: (device.location ? device.location.id : null) }, true);
                return (device.location && selected.length) ? selected[0].name : 'Not set';
            };

            $scope.updateLocation = function(device, data) {
                device.location = data;
                var enabled = device.enabled;
                return device.$save().then(function(data){ data.enabled = enabled; return data; });
            };
            // Location related part
            $scope.locationFilter = {};
            $scope.locations = Location.query()
            $scope.addLocation = function addLocation(){
                var item = Location.save({name: $scope.newLocation});
                $scope.locations.push(item);
            }
     }])
;
