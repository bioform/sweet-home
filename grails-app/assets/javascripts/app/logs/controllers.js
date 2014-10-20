angular.module( 'logsPanel', [

])

    .controller('LogPanelCtrl', ['$rootScope', '$scope', '$routeParams', 'ws',
        function($rootScope, $scope, $routeParams, ws) {
            $scope.logs = [];
            var topic = null;
            var subscription = null;

            var getTopic = function getTopic(routeParams){
                var scriptId = routeParams.script_id;
                return scriptId ? "/topic/script/"+scriptId+"/logs" : "/topic/logs";
            };

            var subscribe = function subscribe(){
                subscription = ws.subscribe(topic, function(message) {
                    $scope.$apply(function () {
                        var msg = JSON.parse(message.body);
                        $scope.logs.push(msg);
                    });
                });
            }

            $rootScope.$watch("showLogs", function(val){ $scope.showLogs = val; });

            $scope.$on('$routeChangeSuccess', function (event, current, previous) {
                var newTopic = getTopic( current.params );
                if( newTopic != topic || !subscription){
                    // clear prev logs
                    $scope.logs = [];
                    // update topic
                    topic = newTopic
                    // unsubscribe/subscribe
                    if(subscription){
                        subscription.unsubscribe();
                        subscribe();
                    } else {
                        ws.connect({}, subscribe);
                    }
                }
            });

        }])
;
