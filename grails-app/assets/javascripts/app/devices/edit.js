angular.module( 'deviceEditCtrl', [

])

    .config(['$routeProvider', function config( $routeProvider ) {
        $routeProvider.
            when('/device/:id', {
                templateUrl: '/assets/app/devices/edit.htm',
                controller: 'DeviceEditCtrl',
                menu: 'devices'
            })
    }])
    .controller('DeviceEditCtrl', ['$scope','$routeParams', 'Device', 'notificationService',
        function ($scope, $routeParams, Device, notify) {
            var id = $routeParams.id;
            $scope.device = Device.get({id: id});
            //$scope.locations = Location.query();

            $scope.save = function save(){
                $scope.device.$save(function(device){
                    notify.success("Data was saved")
                });
            };

            $scope.back = function back(){
                history.back();
            };
        }])
;
