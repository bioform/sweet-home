angular.module('measurementsCharts',[

])
    .config(['$routeProvider', function config( $routeProvider ) {
        $routeProvider.
            when('/measurements/:id', {
                templateUrl: '/assets/app/charts/measurements.htm',
                controller: 'MeasurementsCtrl',
                menu: 'measurements'
            })
    }])
    .directive('rickshawChart', function () {
        return {
            scope: {
                data: '=',
                units: '=',
                renderer: '='
            },
            template: '<div></div>',
            restrict: 'E',
            link: function postLink(scope, element, attrs) {
                scope.$watchCollection('[data, renderer]', function(newVal, oldVal){
                    if(!newVal[0]){
                        return;
                    }

                    element[0].innerHTML ='';

                    var graph = new Rickshaw.Graph({
                        element: element[0],
                        width: attrs.width,
                        height: attrs.height,
                        padding: {
                            top: 0.12
                        },
                        series: [{data: scope.data, color: attrs.color}],
                        renderer: scope.renderer
                    });

                    var xAxis = new Rickshaw.Graph.Axis.Time({graph: graph});
                    xAxis.render();

                    var yAxis = new Rickshaw.Graph.Axis.Y({graph: graph});
                    yAxis.render();

                    var hoverDetail = new Rickshaw.Graph.HoverDetail({
                        graph: graph,
                        yFormatter: function(y) { return y + " " + scope.units},
                        formatter: function(series, x, y, formattedX, formattedY){
                            return formattedY;
                        }
                    });

                    graph.render();
                });
            }
        }
    })
    .controller('MeasurementsCtrl', ['$scope','$http','$routeParams',
        function ($scope, $http, $routeParams) {
            var id = $routeParams.id
            $scope.renderer = 'line';

            $http.get('/measurements/'+id+'.json').success(function(result){
                $scope.measurements = result.measurements;
                $scope.device = result.device;
                $scope.units = result.units;
            })
    }]);