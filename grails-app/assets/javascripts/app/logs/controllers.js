angular.module( 'logsPanel', [

])

    .controller('LogPanelCtrl', ['$rootScope', '$scope', 'ws',
        function($rootScope, $scope, ws) {
            $scope.logs = [];

            $rootScope.$watch("showLogs", function(val){ $scope.showLogs = val; });

            ws.connect({}, function() {
                ws.subscribe("/topic/logs", function(message) {
                    $scope.$apply(function () {
                       var msg = JSON.parse(message.body);
                       $scope.logs.push(msg);
                    });
                });
            });

        }])
;
