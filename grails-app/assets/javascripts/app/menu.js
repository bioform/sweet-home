angular.module( 'activeMenu', [

])

    .controller('MenuCtrl', ['$rootScope', '$location', '$route', '$element',
        function($rootScope, $location, $route, $element) {

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

        }])
;
