angular.module( 'scriptControllers', [

])

    .config(['$routeProvider', function config( $routeProvider ) {
        $routeProvider.
            when('/scripts', {
                templateUrl: '/assets/app/scripts/scripts.htm',
                controller: 'ScriptListCtrl',
                menu: 'scripts'
            }).
            when('/script/:id?', {
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
            var id = $routeParams.id;
            if(angular.isUndefined(id) || id === null ) {
                $scope.script = new Script({code: "// Put your code here\n"});
            }
            else {
                $scope.script = Script.get({id: id});
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
