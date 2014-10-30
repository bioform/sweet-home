angular.module('measurementsCharts',[

])
    .config(['$routeProvider', function config( $routeProvider ) {
        $routeProvider.
            when('/measurements', {
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
                            top: 0.07
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
                        yFormatter: function(y) { return Math.round(y*100)/100 + " " + scope.units},
                        formatter: function(series, x, y, formattedX, formattedY){
                            return formattedY;
                        }
                    });

                    graph.render();
                });
            }
        }
    })
    .controller('MeasurementsCtrl', function ($scope, $http) {

        $scope.renderer = 'line';

        $http.get('/measurements/1.json').success(function(result){
            $scope.measurements = result.measurements;
            $scope.device = result.device;
            $scope.units = result.units;
        })
    });