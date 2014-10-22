angular.module( 'activeMenu', [

])

    .controller('MenuCtrl', ['$rootScope', '$scope', '$location', '$route', '$element',
        function($rootScope, $scope, $location, $route, $element) {

            $rootScope.$on('$locationChangeSuccess', function(event){
                var url = $location.path();
                $('ul.nav a, ul.menu a', $element).each(function(){
                    var a = $(this);
                    var li = a.parent();

                    if( $route.current && $route.current.menu && a.attr('href') == $route.current.menu ){
                        li.addClass('active');
                    } else {
                        li.removeClass('active');
                    }
                });
            })

            $scope.$watch("showLogs", function(val){ if($rootScope.showLogs != val) $rootScope.showLogs = val; });
            $rootScope.$watch("showLogs", function(val){ if($scope.showLogs != val) $scope.showLogs = val; });
        }])
;
