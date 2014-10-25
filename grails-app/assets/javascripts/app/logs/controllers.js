angular.module( 'logsPanel', [

])

    .controller('LogPanelCtrl', ['$rootScope', '$scope', '$routeParams', 'ws', '$element',
        function($rootScope, $scope, $routeParams, ws, $element) {
            $scope.routeParams = $routeParams;
            $scope.logs = [];
            $scope.clear = function clear(){
                while($scope.logs.length) $scope.logs.pop();
            };
            $scope.hide = function hide(){
                $rootScope.showLogs = false;
            }

            var topic = null;
            var subscription = null;

            var getTopic = function getTopic(routeParams){
                var scriptId = routeParams.script_id;
                return scriptId ? "/topic/script/"+scriptId+"/logs" : "/topic/logs";
            };

            var subscribe = function subscribe(){
                subscription = ws.subscribe(topic, function(message) {
                    $scope.$apply(function () {
                        var json = JSON.parse(message.body);
                        if(json.target == 'console'){
                            console.log(json.msg);
                        } else {
                            $scope.logs.push(json);
                        }
                    });
                });
            }

            $rootScope.$watch("showLogs", function(val){
                if($scope.showLogs != val) $scope.showLogs = val;
                if(val){
                    var addScroll = function addScroll() {
                        var height = $element.children().first().outerHeight();
                        $element.height(height);
                    }
                    setTimeout(addScroll, 0);
                } else {
                    $element.height(0);
                }
            });

            $scope.$watch('routeParams', function (params) {
                var newTopic = getTopic( params );
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
            }, true);

        }])
;
