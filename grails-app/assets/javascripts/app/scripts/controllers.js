angular.module( 'scriptControllers', [

])

    .config(['$routeProvider', function config( $routeProvider ) {
        $routeProvider.
            when('/scripts', {
                templateUrl: '/assets/app/scripts/scripts.htm',
                controller: 'ScriptListCtrl',
                menu: 'scripts'
            }).
            when('/script/:id', {
                templateUrl: '/assets/app/scripts/edit.htm',
                controller: 'ScriptEditCtrl',
                menu: 'scripts'
            })
    }])
    .controller('ScriptListCtrl', ['$scope','$filter', 'Script',
        function ($scope, $filter, Script) {

            $scope.scripts = Script.query();

            $scope.save = function save(script){ script.$save() };

        }])
    .controller('ScriptEditCtrl', ['$scope','$filter', 'Script', '$routeParams',
        function ($scope, $filter, Script, $routeParams) {

            if(angular.isUndefined(val) || val === null ) {
                $scope.script = new Script({});
            }
            else {
                $scope.script = Script.get($routeParams.id);
            }

            $scope.save = function save(){ $scope.script.$save() };
            $scope.cmOptions = {
                lineNumbers: true,
                matchBrackets: true,
                theme: 'monokai',
                lineWrapping : false,
                viewportMargin: Infinity,
                readOnly: false,
                mode: 'text/javascript'
            };

        }])
;
