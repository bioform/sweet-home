angular.module( 'logsPanel', [

])

    .controller('LogPanelCtrl', ['$rootScope', '$scope', '$element',
        function($rootScope, $scope, $element) {
            $scope.logs = [];

            $rootScope.$watch("showLogs", function(val){ $scope.showLogs = val; });

            var socket = new SockJS(window.webSocketLink);
            var client = Stomp.over(socket);

            client.connect({}, function() {
                client.subscribe("/topic/logs", function(message) {
                    $scope.$apply(function () {
                       var msg = JSON.parse(message.body);
                       console.log("Message: " + message.body);
                       $scope.logs.push(msg);
                    });
                });
            });

        }])
;
